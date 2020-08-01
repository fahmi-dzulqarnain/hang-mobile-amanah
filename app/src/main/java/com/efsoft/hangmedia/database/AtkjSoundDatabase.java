package com.efsoft.hangmedia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AtkjSoundDatabase extends SQLiteOpenHelper {

    private static final String Database_Name = "atkjhang.db";
    private static final String Tabel_Atkj = "AtkjTabel";

    public AtkjSoundDatabase(Context context){
        super(context, Database_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Tabel_Atkj + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "JUDUL_ATKJ TEXT, TGL_ATKJ TEXT, SOUND_URL TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Tabel_Atkj);
        onCreate(db);
    }

    public void insertAtkj(String judul, String tanggal, String url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conVal = new ContentValues();
        conVal.put("JUDUL_ATKJ", judul);
        conVal.put("TGL_ATKJ", tanggal);
        conVal.put("SOUND_URL", url);
        db.insert(Tabel_Atkj, null, conVal);
    }

    public Cursor getAtkj(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT JUDUL_ATKJ, TGL_ATKJ, SOUND_URL FROM AtkjTabel ORDER BY TGL_ATKJ", null);
    }

    public void deleteAtkj(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("AtkjTabel",null, null);
    }
}
