package com.zxt.notebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.SimpleCursorAdapter;
import android.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class NotesListActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private int maxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        //Create an OnItemClickListener
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> listView,
                                            View v,
                                            int position,
                                            long id) {

                        Intent intent = new Intent(NotesListActivity.this,
                                NoteActivity.class);
                        intent.putExtra(NoteActivity.EXTRA_NOTENO, (int)id);
                        startActivity(intent);

                    }
                };
        //Add the listener to the list view
        ListView listNotes = (ListView) findViewById(R.id.notes_list);
        try {
            SQLiteOpenHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
            db = noteDatabaseHelper.getReadableDatabase();
            cursor = db.query("NOTEBOOK", new String[] {"MAX(_id) AS max"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                maxId = cursor.getInt(0);
            }
            cursor = db.query("NOTEBOOK",
                    new String[] {"_id", "TITLE", "LAST_EDIT"},
                    null, null, null, null,
                    "LAST_EDIT DESC");
            CursorAdapter cursorAdapter =
                    new SimpleCursorAdapter(NotesListActivity.this,
                            android.R.layout.simple_list_item_1,
                            cursor,
                            new String[]{"TITLE"},
                            new int[]{android.R.id.text1}, 0);
            listNotes.setAdapter(cursorAdapter);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        listNotes.setOnItemClickListener(itemClickListener);
    }

    public void onClickNewNote(View view) {
        ContentValues noteValues = new ContentValues();
        noteValues.put("_id", maxId + 1);
        noteValues.put("TITLE", "");
        noteValues.put("MAIN_BODY", "");
        try {
            SQLiteOpenHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
            db = noteDatabaseHelper.getWritableDatabase();
            db.insert("NOTEBOOK", null,noteValues);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.EXTRA_NOTENO, maxId + 1);
        startActivity(intent);
    }

    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    public void onRestart() {
        super.onRestart();
        try {
            NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
            db = noteDatabaseHelper.getReadableDatabase();
            cursor = db.query("NOTEBOOK", new String[] {"MAX(_id) AS max"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                maxId = cursor.getInt(0);
            }
            Cursor newCursor = db.query("NOTEBOOK",
                    new String[] {"_id", "TITLE", "LAST_EDIT"},
                    null, null, null, null,
                    "LAST_EDIT DESC");
            ListView listNotes = (ListView) findViewById(R.id.notes_list);
            CursorAdapter adapter = (CursorAdapter) listNotes.getAdapter();
            adapter.changeCursor(newCursor);
            cursor = newCursor;
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
