package com.dafakamatt.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    // Declaring global/constant variables:
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;


    // Mandetory class constructor here:
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // String that contains the SQLite command to create the books table:
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + " TEXT, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

        // Create the SQL table here:
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    // Required onUpgrade method, as specified by project rubic.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
