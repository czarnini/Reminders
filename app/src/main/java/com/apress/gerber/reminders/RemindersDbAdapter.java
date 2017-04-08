package com.apress.gerber.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Michał on 08.04.2017.
 */

public class RemindersDbAdapter
{
    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";

    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;

    //used for logging
    private static final String TAG = "RemindersDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_remdrs";
    private static final String TABLE_NAME = "tbl_remdrs";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    //SQL statement used to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANT + " INTEGER );";

    RemindersDbAdapter (Context context)
    {
        this.mCtx = context;
    }


    public void open() throws SQLException{
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }


    public void close()
    {
        if (mDbHelper != null)
        {
            mDbHelper.close();
        }
    }

    public void createReminder(String name, boolean important)
    {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, name);
        values.put(COL_IMPORTANT, important);
        mDb.insert(TABLE_NAME, null ,values);
    }

    public long createReminder (Reminder reminder)
    {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getmContent());
        values.put(COL_IMPORTANT, reminder.getmImportant());
        return mDb.insert(TABLE_NAME, null, values);
    }

    public Reminder fetchReminderById(int id)
    {
        Cursor cursor = mDb.query(
                TABLE_NAME,
                new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT},
                COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        try
        {
            if (cursor != null)
                cursor.moveToFirst();
            return new Reminder(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
        }
        finally {
            cursor.close();
        }

    }

    public Cursor fetchAllReminders()
    {
        Cursor mCursor = mDb.query(
                TABLE_NAME,
                new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT},
                null, null, null, null, null
        );

        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void updateReminder (Reminder reminder)
    {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getmContent());
        values.put(COL_IMPORTANT, reminder.getmImportant());

        mDb.update(TABLE_NAME,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(reminder.getmID())}
        );
    }

    public void deleteReminderById(int id){
        mDb.delete(TABLE_NAME,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public void deleteAllReminders()
    {
        mDb.delete(TABLE_NAME, null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Log.d(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1)
        {
            Log.w(TAG, "Proba upgradu z wersji " + i + " na wersje " + i1 );
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
