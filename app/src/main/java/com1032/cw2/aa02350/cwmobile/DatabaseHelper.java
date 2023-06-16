package com1032.cw2.aa02350.cwmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by anthonyawobasivwe on 19/05/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Distance.db";
    public static final String TABLE_NAME = "Distance_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "origin";
    public static final String COL_3 = "destination";
    public static final String COL_4 = "Distance";
    public static final String COL_5 = "Time";
    private Context context;

    public DatabaseHelper(Context context ) {
        //sets up database from super class
        super(context, DATABASE_NAME,null,1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //loads up table from create method
        Log.d("[CountryDB::onCreate]", "Creating Distance_table table");
        createTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertTable(Route route) {
        //inserts fields into table
        ContentValues cv = new ContentValues();
        cv.put(COL_2, route.getStartAddress());
        cv.put(COL_3, route.endAddress);
        cv.put(COL_4, route.distance.getValue());
        cv.put(COL_5, route.duration.getValue());
        Toast.makeText(context, "added to database", Toast.LENGTH_SHORT).show();
        return getWritableDatabase().insert(TABLE_NAME, null, cv);
    }

    private void createTable(SQLiteDatabase db) {
        //creates table using primary key and fields
        String createSQL = "CREATE TABLE " + TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Distance TEXT, " +
                "Duration TEXT, " +
                "Origin TEXT, " +
                "Destination TEXT);";

        /** Execute the SQL creation clause...Since we don't expect any results back, we call the execSQL and not the rawQuery */
        db.execSQL(createSQL);
    }

}

