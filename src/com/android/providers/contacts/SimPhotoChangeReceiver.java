/*
 * Copyright (c) 2013, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.providers.contacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.android.internal.telephony.MSimConstants;
import com.android.providers.contacts.util.ContactsProviderUtil;

/**
 * Upgrade SIM card photo when default image in Settings changed
 */
public class SimPhotoChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int subscription = intent.getIntExtra("sim_sub", 0);
        if (subscription != MSimConstants.SUB1 && subscription != MSimConstants.SUB2) {
            return;
        }

        int resourceId = ContactsProviderUtil.getSimIconResourceId(context, subscription);
        if (resourceId < 0) {
            return;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE data");
        sql.append("  SET data15 = ? ");
        sql.append(" WHERE mimetype_id = 10 ");
        sql.append("   AND is_super_primary = 1 ");
        sql.append("   AND raw_contact_id IN (");
        sql.append("     SELECT _id FROM raw_contacts WHERE account_id IN (  ");
        sql.append("       SELECT _id FROM accounts WHERE account_name =  ? ");
        sql.append("     ))");

        SQLiteDatabase database = null;
        try {
            ContactsDatabaseHelper helper = ContactsDatabaseHelper.getInstance(context);
            database = helper.getWritableDatabase();
            if (database.isOpen()) {
                SQLiteStatement statement = database.compileStatement(sql.toString());
                database.beginTransaction();
                try {
                    statement.bindBlob(1, ContactsProviderUtil.getBitmapData(context, resourceId));
                    statement.bindString(2, ContactsProviderUtil.getSimAccountName(subscription));
                    statement.execute();
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            }
            database.close();
        } catch (Exception e) {
            try {
                if (database != null)
                    database.close();
            } finally {
                database = null;
            }
        }
    }
}
