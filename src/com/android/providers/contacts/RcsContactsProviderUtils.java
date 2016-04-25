/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.providers.contacts;

import com.android.common.content.ProjectionMap;
import com.android.providers.contacts.ContactsDatabaseHelper.AccountsColumns;
import com.android.providers.contacts.ContactsDatabaseHelper.ContactsColumns;
import com.android.providers.contacts.ContactsDatabaseHelper.RawContactsColumns;
import com.android.providers.contacts.ContactsDatabaseHelper.Tables;
import com.android.providers.contacts.ContactsDatabaseHelper.Views;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract.DisplayNameSources;
import android.provider.ContactsContract.PinnedPositions;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.os.SystemProperties;

public class RcsContactsProviderUtils {
    private static final String PROPERTY_NAME_RCS_ENABLED = "persist.sys.rcs.enabled";

    private static final String NO_SUCH_COLUMN_EXCEPTION_MESSAGE = "no such column";

    private static final String LOCAL_PHOTO_SETTED = "local_photo_setted";
    private static final ProjectionMap sRawContactSyncColumns = ProjectionMap.builder()
            .add(RawContacts.SYNC1)
            .add(RawContacts.SYNC2)
            .add(RawContacts.SYNC3)
            .add(RawContacts.SYNC4)
            .build();
    private static final ProjectionMap sRawContactColumns = ProjectionMap.builder()
            .add(RawContacts.ACCOUNT_NAME)
            .add(RawContacts.ACCOUNT_TYPE)
            .add(RawContacts.DATA_SET)
            .add(RawContacts.ACCOUNT_TYPE_AND_DATA_SET)
            .add(RawContacts.DIRTY)
            .add(RawContacts.SOURCE_ID)
            .add(RawContacts.BACKUP_ID)
            .add(RawContacts.VERSION)
            .build();
    public static final ProjectionMap sRawContactsProjectionMapForRcs = ProjectionMap.builder()
            .add(RawContacts._ID)
            .add(RawContacts.CONTACT_ID)
            .add(RawContacts.DELETED)
            .add(RawContacts.DISPLAY_NAME_PRIMARY)
            .add(RawContacts.DISPLAY_NAME_ALTERNATIVE)
            .add(RawContacts.DISPLAY_NAME_SOURCE)
            .add(RawContacts.PHONETIC_NAME)
            .add(RawContacts.PHONETIC_NAME_STYLE)
            .add(RawContacts.SORT_KEY_PRIMARY)
            .add(RawContacts.SORT_KEY_ALTERNATIVE)
            .add(RawContactsColumns.PHONEBOOK_LABEL_PRIMARY)
            .add(RawContactsColumns.PHONEBOOK_BUCKET_PRIMARY)
            .add(RawContactsColumns.PHONEBOOK_LABEL_ALTERNATIVE)
            .add(RawContactsColumns.PHONEBOOK_BUCKET_ALTERNATIVE)
            .add(RawContacts.TIMES_CONTACTED)
            .add(RawContacts.LAST_TIME_CONTACTED)
            .add(RawContacts.CUSTOM_RINGTONE)
            .add(RawContacts.SEND_TO_VOICEMAIL)
            .add(RawContacts.STARRED)
            .add(RawContacts.PINNED)
            .add(RawContacts.AGGREGATION_MODE)
            .add(RawContacts.RAW_CONTACT_IS_USER_PROFILE)
            .addAll(sRawContactColumns)
            .addAll(sRawContactSyncColumns)
            .add(LOCAL_PHOTO_SETTED)
            .build();
    public static String rawContactOptionColumns =
            RawContacts.CUSTOM_RINGTONE + ","
                    + RawContacts.SEND_TO_VOICEMAIL + ","
                    + RawContacts.LAST_TIME_CONTACTED + ","
                    + RawContacts.TIMES_CONTACTED + ","
                    + RawContacts.STARRED + ","
                    + RawContacts.PINNED;
    public static String syncColumns =
            RawContactsColumns.CONCRETE_ACCOUNT_ID + ","
                    + AccountsColumns.CONCRETE_ACCOUNT_NAME + " AS " + RawContacts.ACCOUNT_NAME +
                    ","
                    + AccountsColumns.CONCRETE_ACCOUNT_TYPE + " AS " + RawContacts.ACCOUNT_TYPE +
                    ","
                    + AccountsColumns.CONCRETE_DATA_SET + " AS " + RawContacts.DATA_SET + ","
                    + "(CASE WHEN " + AccountsColumns.CONCRETE_DATA_SET + " IS NULL THEN "
                    + AccountsColumns.CONCRETE_ACCOUNT_TYPE
                    + " ELSE " + AccountsColumns.CONCRETE_ACCOUNT_TYPE + "||'/'||"
                    + AccountsColumns.CONCRETE_DATA_SET + " END) AS "
                    + RawContacts.ACCOUNT_TYPE_AND_DATA_SET + ","
                    + RawContactsColumns.CONCRETE_SOURCE_ID + " AS " + RawContacts.SOURCE_ID + ","
                    + RawContactsColumns.CONCRETE_BACKUP_ID + " AS " + RawContacts.BACKUP_ID + ","
                    + RawContactsColumns.CONCRETE_VERSION + " AS " + RawContacts.VERSION + ","
                    + RawContactsColumns.CONCRETE_DIRTY + " AS " + RawContacts.DIRTY + ","
                    + RawContactsColumns.CONCRETE_SYNC1 + " AS " + RawContacts.SYNC1 + ","
                    + RawContactsColumns.CONCRETE_SYNC2 + " AS " + RawContacts.SYNC2 + ","
                    + RawContactsColumns.CONCRETE_SYNC3 + " AS " + RawContacts.SYNC3 + ","
                    + RawContactsColumns.CONCRETE_SYNC4 + " AS " + RawContacts.SYNC4;
    public static String rawContactsSelectForRcs = "SELECT "
            + RawContactsColumns.CONCRETE_ID + " AS " + RawContacts._ID + ","
            + RawContacts.CONTACT_ID + ", "
            + RawContacts.AGGREGATION_MODE + ", "
            + RawContacts.RAW_CONTACT_IS_READ_ONLY + ", "
            + RawContacts.DELETED + ", "
            + RawContacts.DISPLAY_NAME_SOURCE + ", "
            + RawContacts.DISPLAY_NAME_PRIMARY + ", "
            + RawContacts.DISPLAY_NAME_ALTERNATIVE + ", "
            + RawContacts.PHONETIC_NAME + ", "
            + RawContacts.PHONETIC_NAME_STYLE + ", "
            + RawContacts.SORT_KEY_PRIMARY + ", "
            + RawContactsColumns.PHONEBOOK_LABEL_PRIMARY + ", "
            + RawContactsColumns.PHONEBOOK_BUCKET_PRIMARY + ", "
            + RawContacts.SORT_KEY_ALTERNATIVE + ", "
            + RawContactsColumns.PHONEBOOK_LABEL_ALTERNATIVE + ", "
            + RawContactsColumns.PHONEBOOK_BUCKET_ALTERNATIVE + ", "
            + RawContactsColumns.LOCAL_PHOTO_SETTED + ", "
            + dbForProfile() + " AS " + RawContacts.RAW_CONTACT_IS_USER_PROFILE + ", "
            + rawContactOptionColumns + ", "
            + syncColumns
            + " FROM " + Tables.RAW_CONTACTS
            + " JOIN " + Tables.ACCOUNTS + " ON ("
            + RawContactsColumns.CONCRETE_ACCOUNT_ID + "=" + AccountsColumns.CONCRETE_ID
            + ")";

    public static boolean isRcsEnabled() {
        return SystemProperties.getBoolean(PROPERTY_NAME_RCS_ENABLED, false);
    }

    public static void createRcsRawContacts(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.RAW_CONTACTS + " (" +
                RawContacts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RawContactsColumns.ACCOUNT_ID + " INTEGER REFERENCES " +
                Tables.ACCOUNTS + "(" + AccountsColumns._ID + ")," +
                RawContacts.SOURCE_ID + " TEXT," +
                RawContacts.BACKUP_ID + " TEXT," +
                RawContacts.RAW_CONTACT_IS_READ_ONLY + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.VERSION + " INTEGER NOT NULL DEFAULT 1," +
                RawContacts.DIRTY + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.DELETED + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.CONTACT_ID + " INTEGER REFERENCES contacts(_id)," +
                RawContacts.AGGREGATION_MODE + " INTEGER NOT NULL DEFAULT " +
                RawContacts.AGGREGATION_MODE_DEFAULT + "," +
                RawContactsColumns.AGGREGATION_NEEDED + " INTEGER NOT NULL DEFAULT 1," +
                RawContacts.CUSTOM_RINGTONE + " TEXT," +
                RawContacts.SEND_TO_VOICEMAIL + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.TIMES_CONTACTED + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.LAST_TIME_CONTACTED + " INTEGER," +
                RawContacts.STARRED + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.PINNED + " INTEGER NOT NULL DEFAULT " + PinnedPositions.UNPINNED +
                "," + RawContacts.DISPLAY_NAME_PRIMARY + " TEXT," +
                RawContacts.DISPLAY_NAME_ALTERNATIVE + " TEXT," +
                RawContacts.DISPLAY_NAME_SOURCE + " INTEGER NOT NULL DEFAULT " +
                DisplayNameSources.UNDEFINED + "," +
                RawContacts.PHONETIC_NAME + " TEXT," +
                RawContacts.PHONETIC_NAME_STYLE + " TEXT," +
                RawContacts.SORT_KEY_PRIMARY + " TEXT COLLATE " +
                ContactsProvider2.PHONEBOOK_COLLATOR_NAME + "," +
                RawContactsColumns.PHONEBOOK_LABEL_PRIMARY + " TEXT," +
                RawContactsColumns.PHONEBOOK_BUCKET_PRIMARY + " INTEGER," +
                RawContacts.SORT_KEY_ALTERNATIVE + " TEXT COLLATE " +
                ContactsProvider2.PHONEBOOK_COLLATOR_NAME + "," +
                RawContactsColumns.PHONEBOOK_LABEL_ALTERNATIVE + " TEXT," +
                RawContactsColumns.PHONEBOOK_BUCKET_ALTERNATIVE + " INTEGER," +
                RawContactsColumns.NAME_VERIFIED_OBSOLETE + " INTEGER NOT NULL DEFAULT 0," +
                RawContacts.SYNC1 + " TEXT, " +
                RawContacts.SYNC2 + " TEXT, " +
                RawContacts.SYNC3 + " TEXT, " +
                RawContacts.SYNC4 + " TEXT, " +
                RawContactsColumns.LOCAL_PHOTO_SETTED + " INTEGER NOT NULL DEFAULT 0 " +
                ");");
    }

    public static void createRcsRawContactsView(SQLiteDatabase db) {
        String syncColumns =
                RawContactsColumns.CONCRETE_ACCOUNT_ID + ","
                        + AccountsColumns.CONCRETE_ACCOUNT_NAME + " AS " + RawContacts
                        .ACCOUNT_NAME + ","
                        + AccountsColumns.CONCRETE_ACCOUNT_TYPE + " AS " + RawContacts
                        .ACCOUNT_TYPE + ","
                        + AccountsColumns.CONCRETE_DATA_SET + " AS " + RawContacts.DATA_SET + ","
                        + "(CASE WHEN " + AccountsColumns.CONCRETE_DATA_SET + " IS NULL THEN "
                        + AccountsColumns.CONCRETE_ACCOUNT_TYPE
                        + " ELSE " + AccountsColumns.CONCRETE_ACCOUNT_TYPE + "||'/'||"
                        + AccountsColumns.CONCRETE_DATA_SET + " END) AS "
                        + RawContacts.ACCOUNT_TYPE_AND_DATA_SET + ","
                        + RawContactsColumns.CONCRETE_SOURCE_ID + " AS " + RawContacts.SOURCE_ID
                        + ","
                        + RawContactsColumns.CONCRETE_BACKUP_ID + " AS " + RawContacts.BACKUP_ID
                        + ","
                        + RawContactsColumns.CONCRETE_VERSION + " AS " + RawContacts.VERSION + ","
                        + RawContactsColumns.CONCRETE_DIRTY + " AS " + RawContacts.DIRTY + ","
                        + RawContactsColumns.CONCRETE_SYNC1 + " AS " + RawContacts.SYNC1 + ","
                        + RawContactsColumns.CONCRETE_SYNC2 + " AS " + RawContacts.SYNC2 + ","
                        + RawContactsColumns.CONCRETE_SYNC3 + " AS " + RawContacts.SYNC3 + ","
                        + RawContactsColumns.CONCRETE_SYNC4 + " AS " + RawContacts.SYNC4;
        String rawContactOptionColumns =
                RawContacts.CUSTOM_RINGTONE + ","
                        + RawContacts.SEND_TO_VOICEMAIL + ","
                        + RawContacts.LAST_TIME_CONTACTED + ","
                        + RawContacts.TIMES_CONTACTED + ","
                        + RawContacts.STARRED + ","
                        + RawContacts.PINNED;

        String rawContactsSelectDefault = "SELECT "
                + RawContactsColumns.CONCRETE_ID + " AS " + RawContacts._ID + ","
                + RawContacts.CONTACT_ID + ", "
                + RawContacts.AGGREGATION_MODE + ", "
                + RawContacts.RAW_CONTACT_IS_READ_ONLY + ", "
                + RawContacts.DELETED + ", "
                + RawContacts.DISPLAY_NAME_SOURCE + ", "
                + RawContacts.DISPLAY_NAME_PRIMARY + ", "
                + RawContacts.DISPLAY_NAME_ALTERNATIVE + ", "
                + RawContacts.PHONETIC_NAME + ", "
                + RawContacts.PHONETIC_NAME_STYLE + ", "
                + RawContacts.SORT_KEY_PRIMARY + ", "
                + RawContactsColumns.PHONEBOOK_LABEL_PRIMARY + ", "
                + RawContactsColumns.PHONEBOOK_BUCKET_PRIMARY + ", "
                + RawContacts.SORT_KEY_ALTERNATIVE + ", "
                + RawContactsColumns.PHONEBOOK_LABEL_ALTERNATIVE + ", "
                + RawContactsColumns.PHONEBOOK_BUCKET_ALTERNATIVE + ", "
                + dbForProfile() + " AS " + RawContacts.RAW_CONTACT_IS_USER_PROFILE + ", "
                + rawContactOptionColumns + ", "
                + syncColumns
                + " FROM " + Tables.RAW_CONTACTS
                + " JOIN " + Tables.ACCOUNTS + " ON ("
                + RawContactsColumns.CONCRETE_ACCOUNT_ID + "=" + AccountsColumns.CONCRETE_ID
                + ")";
        String rawContactsSelectForRcs = "SELECT "
                + RawContactsColumns.CONCRETE_ID + " AS " + RawContacts._ID + ","
                + RawContacts.CONTACT_ID + ", "
                + RawContacts.AGGREGATION_MODE + ", "
                + RawContacts.RAW_CONTACT_IS_READ_ONLY + ", "
                + RawContacts.DELETED + ", "
                + RawContacts.DISPLAY_NAME_SOURCE + ", "
                + RawContacts.DISPLAY_NAME_PRIMARY + ", "
                + RawContacts.DISPLAY_NAME_ALTERNATIVE + ", "
                + RawContacts.PHONETIC_NAME + ", "
                + RawContacts.PHONETIC_NAME_STYLE + ", "
                + RawContacts.SORT_KEY_PRIMARY + ", "
                + RawContactsColumns.PHONEBOOK_LABEL_PRIMARY + ", "
                + RawContactsColumns.PHONEBOOK_BUCKET_PRIMARY + ", "
                + RawContacts.SORT_KEY_ALTERNATIVE + ", "
                + RawContactsColumns.PHONEBOOK_LABEL_ALTERNATIVE + ", "
                + RawContactsColumns.PHONEBOOK_BUCKET_ALTERNATIVE + ", "
                + RawContactsColumns.LOCAL_PHOTO_SETTED + ", "
                + dbForProfile() + " AS " + RawContacts.RAW_CONTACT_IS_USER_PROFILE + ", "
                + rawContactOptionColumns + ", "
                + syncColumns
                + " FROM " + Tables.RAW_CONTACTS
                + " JOIN " + Tables.ACCOUNTS + " ON ("
                + RawContactsColumns.CONCRETE_ACCOUNT_ID + "=" + AccountsColumns.CONCRETE_ID
                + ")";
        String rawContactsSelect =
                isRcsEnabled() ? rawContactsSelectForRcs
                        : rawContactsSelectDefault;
        db.execSQL("DROP VIEW IF EXISTS " + Views.RAW_CONTACTS + ";");
        db.execSQL("CREATE VIEW " + Views.RAW_CONTACTS + " AS " + rawContactsSelect);
    }

    private static void checkAndUpdateRawContactsTable(SQLiteDatabase db) {
        try {
            db.query(Tables.RAW_CONTACTS, new String[]{"local_photo_setted"}, null, null, null,
                    null,
                    null);
        } catch (SQLiteException e) {
            //Log.e(TAG, "checkAndUpgradeSmsTable: ex. ", e);
            if (e.getMessage().startsWith(NO_SUCH_COLUMN_EXCEPTION_MESSAGE)) {
                db.execSQL("ALTER TABLE " + Tables.RAW_CONTACTS + " ADD COLUMN "
                        + RawContactsColumns.LOCAL_PHOTO_SETTED + " INTEGER NOT NULL DEFAULT 0 ");
                //createContactsViews(db);
                createRcsRawContactsView(db);
            }
        }
    }

    public static void updateRawContactsTable(SQLiteDatabase db) {
        try {
            if (isRcsEnabled()) {
                checkAndUpdateRawContactsTable(db);
            }
        } catch (SQLiteException e) {
            createRcsRawContacts(db);
            createRcsRawContactsView(db);
        }
    }

    protected static int dbForProfile() {
        return 0;
    }
}
