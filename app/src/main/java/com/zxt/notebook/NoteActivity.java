package com.zxt.notebook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    public static final String EXTRA_NOTENO = "noteNo";

    private SQLiteDatabase db;
    private Cursor cursor;
    private int noteNo;

    private EditText titleEditText;
    private EditText bodyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //add return
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        bodyEditText = (EditText) findViewById(R.id.bodyEditText);

        noteNo = (Integer)getIntent().getExtras().get(EXTRA_NOTENO);
        try {
            SQLiteOpenHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
            db = noteDatabaseHelper.getWritableDatabase();
            cursor = db.query("NOTEBOOK",
                    new String[] {"TITLE", "MAIN_BODY", "LAST_EDIT"},
                    "_id = ?", new String[] {Integer.toString(noteNo)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                String title = cursor.getString(0);
                String body = cursor.getString(1);
                titleEditText.setText(title);
                bodyEditText.setText(body);
            }
            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickDelete(View view) {
        delete();
        this.finish();
    }

    public void onClickSave(View view) {
        this.finish();
    }

    public void onPause() {
        super.onPause();
        String title = titleEditText.getText().toString();
        String body = bodyEditText.getText().toString();
        if (title.trim().equals("") && body.trim().equals("")) {
            delete();
            return;
        }
        ContentValues noteValues = new ContentValues();
        noteValues.put("TITLE", title);
        noteValues.put("MAIN_BODY", body);
        try {
            SQLiteOpenHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
            db = noteDatabaseHelper.getWritableDatabase();
            db.update("NOTEBOOK", noteValues, "_id = ?", new String[] {Integer.toString(noteNo)});
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void delete() {
        try {
            SQLiteOpenHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
            db = noteDatabaseHelper.getWritableDatabase();
            db.delete("NOTEBOOK", "_id = ?", new String[] {Integer.toString(noteNo)});
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
