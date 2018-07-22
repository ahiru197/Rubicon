package com.ahiru.rubicon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ChoiceOpenHelper  extends SQLiteOpenHelper {

    public static final String DB_NAME = "test.db";
    public static final int DB_VERSION = 1;

    public static final String CREATE_TABLE =
            "create table " + com.ahiru.rubicon.TestContract.Choices.TABLE_NAME + " (" +
                    com.ahiru.rubicon.TestContract.Choices._ID + " integer primary key autoincrement," +
                    com.ahiru.rubicon.TestContract.Choices.COL_NAME + " text not null," +
                    // タイトルIDと連携させるためのカラムを追加
                    com.ahiru.rubicon.TestContract.Choices.COL_TITLEID + " integer not null)";

    public static final String CREATE_TABLE_TITLES =
            "create table " + com.ahiru.rubicon.TestContract.Titles.TABLE_NAME + " (" +
                    com.ahiru.rubicon.TestContract.Titles._ID + " integer primary key autoincrement," +
                    com.ahiru.rubicon.TestContract.Titles.COL_NAME + " text not null )";




    public static final String DROP_TABLE =
            "drop table if exists choices";
    public static final String DROP_TABLE_TITLES =
            "drop table if exists titles";



    public ChoiceOpenHelper(Context c){
        super(c, DB_NAME, null, DB_VERSION);
    }

    // 最初にopenhelperが呼ばれた時に起動される
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table
        db.execSQL(CREATE_TABLE_TITLES);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop table
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_TABLE_TITLES);
        // onCreate
        onCreate(db);
    }
}
