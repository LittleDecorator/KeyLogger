package com.acme.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ProjectHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "KeyLog.db";

    public ProjectHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("OnCreate in db");
        db.execSQL(ProjectContract.SQL_CREATE_KEY_TABLE);
        db.execSQL(ProjectContract.SQL_CREATE_FILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        System.out.println("OnUpdate in db");
        Log.i(ProjectHelper.class.getSimpleName(), "Upgrading database from version " + oldVersion + " to " + newVersion);
    }

    public void addKeyRecord(String keyName,String keyValue){
        System.out.println("Insert new key to db");
        long id;
        ContentValues firstProjectValues = new ContentValues();
        firstProjectValues.put(ProjectContract.ProjectEntry.COLUMN_KEY_NAME, keyName);
        firstProjectValues.put(ProjectContract.ProjectEntry.COLUMN_KEY_VALUE,keyValue);
        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(ProjectContract.KEY_TABLE_NAME, null, firstProjectValues);
        db.close();
    }

    public void addFileRecord(String filePath){
        System.out.println("Insert new file pointer to db");
        long id;
        ContentValues firstProjectValues = new ContentValues();
        firstProjectValues.put(ProjectContract.ProjectEntry.COLUMN_FILE_NAME, filePath);
        firstProjectValues.put(ProjectContract.ProjectEntry.COLUMN_FILE_STATUS,"stashed");
        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(ProjectContract.FILE_TABLE_NAME, null, firstProjectValues);
        db.close();
    }

    public String getKeyValue(String name){
        System.out.println("Get key from db");
        String key="";
        SQLiteDatabase db = getReadableDatabase();
        Cursor projCursor = db.query(ProjectContract.KEY_TABLE_NAME, null, null, null, null, null, null);
        while (projCursor.moveToNext()) {
            if(projCursor.getString(projCursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_KEY_NAME)).contentEquals(name)) {
                key = projCursor.getString(projCursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_KEY_VALUE));
                projCursor.close();
                db.close();
                break;
            }
        }
        if(!projCursor.isClosed()){
            projCursor.close();
            db.close();
        }
        return key;
    }

    public Map<Long,String> getFilePath(){
        System.out.println("Get file path from db");
        Map<Long,String> map = new HashMap<>();
        Long id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor projCursor = db.query(ProjectContract.FILE_TABLE_NAME, null, null, null, null, null, null);
        while (projCursor.moveToNext()) {
            id = projCursor.getLong(projCursor.getColumnIndex(ProjectContract.ProjectEntry._ID));
            System.out.println("row id -> "+id);
            if(projCursor.getString(projCursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_FILE_STATUS)).contentEquals("stashed")) {
                map.put(id, projCursor.getString(projCursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_FILE_NAME)));
            }
        }
        if(!projCursor.isClosed()){
            projCursor.close();
            db.close();
        }
        return map;
    }

    public void deleteFileRow(long id){
        SQLiteDatabase db = getWritableDatabase();
        System.out.println("delete record with id -> "+id);
        db.delete(ProjectContract.FILE_TABLE_NAME, ProjectContract.ProjectEntry._ID+"=" + id, null);
        db.close();
    }

    public int couFilePointers(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from "+ProjectContract.FILE_TABLE_NAME, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        db.close();
        return count;
    }

    public void getAllFromFileTable(){
        System.out.println("Get all from file db");
        SQLiteDatabase db = getReadableDatabase();
        Cursor projCursor = db.query(ProjectContract.FILE_TABLE_NAME, null, null, null, null, null, null);
        while (projCursor.moveToNext()) {
            System.out.println(projCursor.getString(projCursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_FILE_STATUS)));
            System.out.println(projCursor.getString(projCursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_FILE_NAME)));
            System.out.println(projCursor.getLong(projCursor.getColumnIndex(ProjectContract.ProjectEntry._ID)));
        }
        if(!projCursor.isClosed()){
            projCursor.close();
            db.close();
        }
    }

}
