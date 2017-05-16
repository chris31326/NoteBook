package com.zxt.notebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chris on 5/14/2017.
 */

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notebook";
    private static final int DB_VERSION = 1;

    NoteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE NOTEBOOK (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TITLE TEXT," +
                "LAST_EDIT NUMERIC," +
                "MAIN_BODY TEXT);");
        insertNote(db, "This is a title","This is a sample note.");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static void insertNote(SQLiteDatabase db, String title, String mainBody) {
        ContentValues noteValues = new ContentValues();
        noteValues.put("MAIN_BODY", mainBody);
        noteValues.put("TITLE", title);
        db.insert("NOTEBOOK", null, noteValues);
    }
}
