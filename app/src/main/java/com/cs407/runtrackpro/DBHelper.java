package com.cs407.runtrackpro;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    static SQLiteDatabase sqLiteDatabase;

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        DBHelper.sqLiteDatabase = sqLiteDatabase;
    }

    public static void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS stats " +
                "(id INTEGER PRIMARY KEY, statId INTEGER, date TEXT, time TEXT, distance TEXT, path TEXT)");
    }

    public ArrayList<Stats> readStats() {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM stats", new String[]{});
        int dateIndex = c.getColumnIndex("date");
        int timeIndex = c.getColumnIndex("time");
        int distanceIndex = c.getColumnIndex("distance");
        int pathIndex = c.getColumnIndex("path");
        c.moveToFirst();
        ArrayList<Stats> statsList = new ArrayList<>();
        while (!c.isAfterLast()) {
            String date = c.getString(dateIndex);
            String time = c.getString(timeIndex);
            String distance = c.getString(distanceIndex);
            String path = c.getString(pathIndex);

            Stats stats = new Stats(date, time, distance, path);
            statsList.add(stats);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();
        return statsList;
    }

    public void saveStats(String date, String time, String distance, String path) {
        createTable();
        sqLiteDatabase.execSQL("INSERT INTO stats (date, time, distance, path) VALUES (?, ?, ?, ?)",
                new String[]{date, time, distance, path});
    }

    public void deleteStats(String date) {
        createTable();
        String time = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT time FROM stats WHERE date = ?",
                new String[]{date});
        if (cursor.moveToNext()) {
            time = cursor.getString(0);
        }
        sqLiteDatabase.execSQL("DELETE FROM stats WHERE date = ? AND time = ?",
                new String[]{date, time});
        cursor.close();
    }
}