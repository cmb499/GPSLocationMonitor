package com.mobileappclass.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Careena on 11/2/16.
 */
public class MyDBHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationDB.db";
    //private static final String DATABASE_NAME = "myGPSDB.db";
    private static final String TABLE_NAME = "location";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_X = "xCord";
    private static final String COLUMN_Y = "yCord";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    //while creating table first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                        COLUMN_ID + " UNSIGNED PRIMARY KEY, " +
                        COLUMN_X + " REAL, " +
                        COLUMN_Y + " REAL, " +
                        COLUMN_TIMESTAMP + " TEXT " +
                ");";

        System.out.println(query);
        db.execSQL(query);
    }

    //ever upgrading your version -- this will be called
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addRow(String datetime, double x, double y){
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIMESTAMP, datetime);
        values.put(COLUMN_X, x);
        values.put(COLUMN_Y, y);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public void deleteRow(){

    }

    public ArrayList<String> getRows(){
        ArrayList<String> myValues = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if (cursor != null && icount > 0) {
            String query = "SELECT * FROM " + TABLE_NAME;

            cursor = db.rawQuery(query, null);

            cursor.moveToFirst();


            do {

                if (cursor.getDouble(cursor.getColumnIndex(COLUMN_X)) != 0) {

                    String datetime = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                    String xCord = "\t" + cursor.getDouble(cursor.getColumnIndex(COLUMN_X));
                    String yCord = "\t" + cursor.getDouble(cursor.getColumnIndex(COLUMN_Y));
                    myValues.add(0,datetime + xCord + yCord);
                    //myValues.add(cursor.getInt(cursor.getColumnIndex(COLUMN_X)));
                }
            } while (cursor.moveToNext());

           //
           // db.close();
        }
        else{
            System.out.println(" ------Empty db-----");
        }
        return myValues;
    }




}
