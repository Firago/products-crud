package com.dfirago.simplecrud.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dfirago.simplecrud.domain.ProductItem;

/**
 * Created by dmfi on 18/02/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "products_crud";

    // Table create statement
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + ProductItem.KEY_TABLE_NAME + "("+
            ProductItem.KEY_ID + " integer primary key ," +
            ProductItem.KEY_TITLE + " text ," +
            ProductItem.KEY_DESCRIPTION + " text ," +
            ProductItem.KEY_IMAGE_URI + " text);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating table
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + ProductItem.KEY_TABLE_NAME);
        // create new table
        onCreate(db);
    }
}