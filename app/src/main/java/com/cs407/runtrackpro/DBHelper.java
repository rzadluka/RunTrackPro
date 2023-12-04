package com.cs407.runtrackpro;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    /**

    Context context =getApplicationContext();
    SQLiteDatabase db =context.openOrCreateDatabase("NoteSQL",Context.MODE_PRIVATE,null);
    dbHelper.sqLiteDatabase =db;

    dbHelper = DBHelper.getInstance();**/

    private static volatile DBHelper INSTANCE =null;
    private static final String DATABASE_NAME = "NoteSQL";
    private static final int DATABASE_VERSION = 1;

    //static SQLiteDatabase sqLiteDatabase;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.sqLiteDatabase = sqLiteDatabase;
    }
    public static synchronized DBHelper getInstance(Context context){
        if(INSTANCE ==null){
            INSTANCE =new DBHelper(context.getApplicationContext());
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS stats " +
                "(id INTEGER PRIMARY KEY, statId INTEGER, date TEXT, time TEXT, distance TEXT, speed TEXT)");
    }

    public ArrayList<Stats> readStats() {
        //createTable();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM stats", new String[] {});
        int dateIndex = c.getColumnIndex("date");
        int timeIndex = c.getColumnIndex("time");
        int distanceIndex = c.getColumnIndex("distance");
        int speedIndex = c.getColumnIndex("speed");
        c.moveToFirst();
        ArrayList<Stats> statsList = new ArrayList<>();
        while (!c.isAfterLast()) {
            String date = c.getString(dateIndex);
            String time = c.getString(timeIndex);
            String distance = c.getString(distanceIndex);
            String speed = c.getString(speedIndex);

            Stats stats = new Stats(date, time, distance, speed);
            statsList.add(stats);
            c.moveToNext();
        }
        c.close();
        db.close();
        return statsList;
    }

    public synchronized void saveStats(String date, String time, String distance, String speed) {
        SQLiteDatabase db = getWritableDatabase();
        createTable(db);
        db.execSQL("INSERT INTO stats (date, time, distance, speed) VALUES (?, ?, ?, ?)",
                new String[] {date, time, distance, speed});
    }

    public synchronized void deleteStats(String date) {
        SQLiteDatabase db = getWritableDatabase();
        String time = "";
        Cursor cursor = db.rawQuery("SELECT time FROM stats WHERE date = ?",
                new String[] {date});
        if (cursor.moveToNext()) {
            time = cursor.getString(0);
        }
        db.execSQL("DELETE FROM stats WHERE date = ? AND time = ?",
                new String[] {date, time});
        cursor.close();
    }
}