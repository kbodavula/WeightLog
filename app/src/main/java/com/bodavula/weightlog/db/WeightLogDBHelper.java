package com.bodavula.weightlog.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kbodavula on 8/2/16.
 * Weight Log Database helper to setup and manage database for the app.
 */
public class WeightLogDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "WeightLog.db";
    public static final int DB_VERSION = 1;

    private static WeightLogDBHelper sInstance;

    private WeightLogDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Singleton DB Helper.
    public static synchronized WeightLogDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WeightLogDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Create tables on create very first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEIGHT_ENTRIES_TABLE);
    }

    // Functionality to add on app upgrade like adding new tables or loading data.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // WeightEntries table create script.
    String CREATE_WEIGHT_ENTRIES_TABLE = "CREATE TABLE " + WeightDBSchema.WeightEntries.TABLE_NAME +
            "(" +
                  WeightDBSchema.WeightEntries.ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                  WeightDBSchema.WeightEntries.DATE_TIME + " INTEGER," +
                  WeightDBSchema.WeightEntries.WEIGHT + " REAL" +
            ")";

}
