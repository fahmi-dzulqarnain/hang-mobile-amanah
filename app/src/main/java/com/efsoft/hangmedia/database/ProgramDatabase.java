package com.efsoft.hangmedia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProgramDatabase extends SQLiteOpenHelper {

    private static final String Database_Name = "programhang.db";
    private static final String Tabel_Program = "ProgramTabel";

    public ProgramDatabase(Context context){
        super(context, Database_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Tabel_Program + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAMA_PROGRAM TEXT, PEMBAHASAN TEXT, USTADZ TEXT, JAM TEXT, IMAGE_URL TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Tabel_Program);
        onCreate(db);
    }

    public void insertProgram(String namaProgram, String pembahasan, String ustadz, String jam, String image_url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conVal = new ContentValues();
        conVal.put("NAMA_PROGRAM", namaProgram);
        conVal.put("PEMBAHASAN", pembahasan);
        conVal.put("USTADZ", ustadz);
        conVal.put("JAM", jam);
        conVal.put("IMAGE_URL", image_url);
        db.insert(Tabel_Program, null, conVal);
    }

    public Cursor getProgram2(String namaProgram){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT PEMBAHASAN, USTADZ, JAM, IMAGE_URL FROM ProgramTabel WHERE NAMA_PROGRAM = ?", new String[]{namaProgram});
    }

    public void deleteProgram(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("ProgramTabel",null, null);
    }
}
