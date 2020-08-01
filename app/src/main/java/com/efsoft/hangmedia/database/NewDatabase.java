package com.efsoft.hangmedia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewDatabase extends SQLiteOpenHelper {

    private static final String Database_Name = "datahang.db";
    private static final String Tabel_Alarm = "AlarmTabel";
    private static final String Tabel_Jadwal = "JadwalTabel";

    public NewDatabase(Context context){
        super(context, Database_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Tabel_Alarm + " (ID_ALARM INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TITLE TEXT, TIME TEXT)");

        db.execSQL("create table " + Tabel_Jadwal + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "PRAYNAME TEXT, TIME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Tabel_Alarm);
        db.execSQL("drop table if exists " + Tabel_Jadwal);
        onCreate(db);
    }

    public void insertJadwal(String prayname, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conVal = new ContentValues();
        conVal.put("PRAYNAME", prayname);
        conVal.put("TIME", time);
        db.insert(Tabel_Jadwal, null, conVal);
    }

    public void updateJadwal(String prayname, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conVal = new ContentValues();
        conVal.put("TIME", time);
        db.update(Tabel_Jadwal, conVal, "PRAYNAME = ?", new String[]{prayname});
    }

    public Cursor getPrayTime(String prayname){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT TIME FROM JadwalTabel WHERE PRAYNAME = ?", new String[]{prayname});
    }

    public void insertPengaturan(String settingField, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conVal = new ContentValues();
        conVal.put("PRAYNAME", settingField);
        conVal.put("TIME", value);
        db.insert(Tabel_Jadwal, null, conVal);
    }

    public void updateSetting(String settingField, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conVal = new ContentValues();
        conVal.put("TIME", value);
        db.update(Tabel_Jadwal, conVal, "PRAYNAME = ?", new String[]{settingField});
    }

    public Cursor getSetting(String settingField){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT TIME FROM JadwalTabel WHERE PRAYNAME = ?", new String[]{settingField});
    }
}
