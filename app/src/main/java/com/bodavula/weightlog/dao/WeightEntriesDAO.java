package com.bodavula.weightlog.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bodavula.weightlog.db.WeightDBSchema;
import com.bodavula.weightlog.db.WeightLogDBHelper;
import com.bodavula.weightlog.model.WeightEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbodavula on 8/2/16.
 * WeightEntryDAO to manage all CRUD operations on WeightEntry table.
 */
public class WeightEntriesDAO {
    private WeightLogDBHelper mDBHelper;

    /**
     * Constructor which takes context as param to get DBHelper instance.
     * @param context
     */
    public WeightEntriesDAO(Context context) {
        mDBHelper = WeightLogDBHelper.getInstance(context);
    }

    /**
     * Method to add/modify Weight Entry.
     * @param WeightEntry
     * @return booelan
     */
    public boolean save(WeightEntry weightEntry) {
        // Get writable database.
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = -1;
        try {
            // Setting content values.
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeightDBSchema.WeightEntries.DATE_TIME, weightEntry.getDateTime());
            contentValues.put(WeightDBSchema.WeightEntries.WEIGHT, weightEntry.getWeight());

            // Save to table.
            if (weightEntry.getId() > 0) {
                // Update weight entry/
                id = db.update(WeightDBSchema.WeightEntries.TABLE_NAME, contentValues, WeightDBSchema.WeightEntries.ENTRY_ID + " = ?", new String[] {String.valueOf(weightEntry.getId())});
            } else {
                // Add weight entry to table.
                id = db.insert(WeightDBSchema.WeightEntries.TABLE_NAME, null, contentValues);
            }

        } catch (SQLException sqlException) {
            // Handle SQL Exception.
            sqlException.printStackTrace();
        } finally {
            db.close();
        }

        // Return true if save is success.
        return id > 0;
    }

    /**
     * Method to delete weight entry.
     * @param WeightEntry
     * @return boolean
     */
    public boolean delete(WeightEntry weightEntry) {
        // Get writable database.
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = -1;
        try {
            // Delete WeightEntry based on EntryId.
            if (weightEntry.getId() > 0) {
                id = db.delete(WeightDBSchema.WeightEntries.TABLE_NAME, WeightDBSchema.WeightEntries.ENTRY_ID + " = ?", new String[] {String.valueOf(weightEntry.getId())});
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            db.close();
        }

        // Return true if delete is success.
        return id > 0;
    }

    /**
     * Get All WightEntries from database table.
     * @return List<WeightEntry>
     */
    public List<WeightEntry> getWeightEntries() {
        List<WeightEntry> entries = new ArrayList<>();
        // Get readable database.
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Table columns.
        String[] columns = new String[] { WeightDBSchema.WeightEntries.ENTRY_ID,
                                          WeightDBSchema.WeightEntries.DATE_TIME,
                                          WeightDBSchema.WeightEntries.WEIGHT };

        // Sorting data based on weight entry date time to get latest on top.
        String sortOrder = columns[1] + " DESC";

        try {

            // Executing query to get data as cursor.
            Cursor entriesCursor = db.query(WeightDBSchema.WeightEntries.TABLE_NAME, columns, null, null, null, null, sortOrder);

            // Iterating over cursor converting data into WeightEntry objects and adding it to list.
            if (entriesCursor != null) {
                WeightEntry entry;
                // Get column indexes for each column we are querying for.
                int idIndex = entriesCursor.getColumnIndex(columns[0]);
                int dateTimeIndex = entriesCursor.getColumnIndex(columns[1]);
                int weightIndex = entriesCursor.getColumnIndex(columns[2]);

                // Loop through cursor data till we don't have next record.
                while (entriesCursor.moveToNext()) {
                    // Create new WeightEntry for each record and add to list.
                    entry = new WeightEntry(entriesCursor.getInt(idIndex), entriesCursor.getLong(dateTimeIndex), (float) entriesCursor.getDouble(weightIndex));
                    entries.add(entry);
                }
                entriesCursor.close();
            }
        } catch (SQLException ex) {
            // Handle exception.
            ex.printStackTrace();
        } finally {
            db.close();
        }

        // Return list of weight entries.
        return entries;
    }
}