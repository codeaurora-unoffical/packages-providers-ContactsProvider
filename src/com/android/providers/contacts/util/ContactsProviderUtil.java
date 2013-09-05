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

package com.android.providers.contacts.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.telephony.MSimTelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ContactsProviderUtil {

    public static byte[] getBitmapData(Context context, int resourceId) {
        if (context == null) {
            return null;
        }
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), resourceId);
        if (photo == null) {
            return null;
        }
        final int size = photo.getWidth() * photo.getHeight() * 4;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * @return the SIM icon for the special subscription.
     */
    public static int getSimIconResourceId(Context context, int subscription) {
        if (context == null) {
            // If the context is null, return 0 as no resource found.
            return 0;
        }

        TypedArray icons = context.getResources().obtainTypedArray(
                com.android.providers.contacts.R.array.sim_icon_180_holo_light);
        String simIconIndex = Settings.System.getString(context.getContentResolver(),
                Settings.System.PREFERRED_SIM_ICON_INDEX);
        if (TextUtils.isEmpty(simIconIndex)) {
            return -1;
        } else {
            String[] indexs = simIconIndex.split(",");
            if (subscription >= indexs.length) {
                return -1;
            }
            return icons.getResourceId(Integer.parseInt(indexs[subscription]), -1);
        }
    }

    private static final String ACCOUNT_NAME_SIM = "SIM";

    public static String getSimAccountName(int subscription) {
        if (MSimTelephonyManager.getDefault().isMultiSimEnabled()) {
            return ACCOUNT_NAME_SIM + (subscription + 1);
        } else {
            return ACCOUNT_NAME_SIM;
        }
    }

}
