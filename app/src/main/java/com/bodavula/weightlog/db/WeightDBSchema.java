package com.bodavula.weightlog.db;

import android.provider.BaseColumns;

/**
 * Created by kbodavula on 8/2/16.
 */
public class WeightDBSchema {

    // Weight entry table/columns.
    public static abstract class WeightEntries implements BaseColumns {
        public static final String TABLE_NAME = "WeightEntries";
        public static final String ENTRY_ID = "EntryId";
        public static final String DATE_TIME = "DateTime";
        public static final String WEIGHT = "Weight";
    }
}
