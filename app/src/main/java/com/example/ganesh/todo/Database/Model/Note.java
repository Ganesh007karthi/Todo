package com.example.ganesh.todo.Database.Model;

public class Note {

    public static String Table_Name="todo_table";
    public static String Column_id="id";
    public static String Column_note="note";
    public static String Column_Timestamp="timestamp";

    private int id;
    private String notes;
    private String timestamp;

    public static final String Create_table=
            "CREATE TABLE "+Table_Name+"("
                    +Column_id+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +Column_note+" TEXT,"
                    +Column_Timestamp+" DATETIME DEFAULT CURRENT_TIMESTAMP"
                    +")";

    public Note(){

    }


    public Note(int id, String notes, String timestamp) {
        this.id = id;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getNotes() {
        return notes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
