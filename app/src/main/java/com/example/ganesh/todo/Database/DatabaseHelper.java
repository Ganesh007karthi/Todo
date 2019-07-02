package com.example.ganesh.todo.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ganesh.todo.Database.Model.Note;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    //database version
    private static final int DATABASE_VERSION=1;
    //database name
    private static final String DATABASE_NAME="todo_db";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //create database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.Create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Note.Table_Name);
        onCreate(db);

    }
    public long insertNote(String note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note.Column_note, note);

        // insert row
        long id = db.insert(Note.Table_Name, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Note getNote(long id){
        SQLiteDatabase db=this.getReadableDatabase();
       Cursor cursor=db.query(Note.Table_Name,new String[]{Note.Column_id,Note.Column_note,Note.Column_Timestamp},Note.Column_id+"=?",new String[]{String.valueOf(id)},null,null,null,null);

       if(cursor!=null){
        cursor.moveToFirst();
       }
       Note note=new Note(
               cursor.getInt(cursor.getColumnIndex(Note.Column_id)),
               cursor.getString(cursor.getColumnIndex((Note.Column_note))),
                            cursor.getString(cursor.getColumnIndex(Note.Column_Timestamp)));
       cursor.close();

        return note;
    }

    public List<Note> getallNotes(){
        List<Note> list=new ArrayList<>();
        String selectQuery="SELECT * FROM " +Note.Table_Name+" ORDER BY "+Note.Column_Timestamp+" DESC";

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                Note note=new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.Column_id)));
                note.setNotes(cursor.getString(cursor.getColumnIndex(Note.Column_note)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.Column_Timestamp)));
                list.add(note);
            }while (cursor.moveToNext());

            db.close();
        }


        return list;
    }

    public int updateNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Note.Column_note,note.getNotes());
        return db.update(Note.Table_Name,values,Note.Column_id + "=?",new String[]{String.valueOf(note.getId())});
    }

    public int cursorcount(){
        SQLiteDatabase db=this.getReadableDatabase();
        String countQuery="SELECT * FROM "+Note.Table_Name;
        Cursor cursor=db.rawQuery(countQuery,null);
        int count=cursor.getCount();
        cursor.close();
        return count;
    }
    public void deleteNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(Note.Table_Name,Note.Column_id+" = ? ",new String[]{String.valueOf(note.getId())});
        close();
    }


}
