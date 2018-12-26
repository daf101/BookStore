package com.dafakamatt.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class BookProvider extends ContentProvider {

    // Creating constant values for table/item ID URIs:
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    // Creating constant for Uri Matcher, when requesting to do allowed actions on the DB:
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Adding supported URI's here:
    static {
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    // Initializing database helper object:
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Inserting new book into the Database. See helper methods underneath
        // the overridden methods below:
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    // Helper method that's called by Overridden Insert() method:
    private Uri insertBook(Uri uri, ContentValues values) {
        // Checking that below columns are not Null:
        String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        String price = values.getAsString(BookEntry.COLUMN_PRICE);
        String stock = values.getAsString(BookEntry.COLUMN_QUANTITY);
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        // If any of the fields are here. Stop and send a toast to the users to properly fill
        // in the fields:
        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(price) || TextUtils.isEmpty(stock)
                || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhoneNumber)) {
            throw new IllegalArgumentException("User has not completed all fields");
        }
        // Preping DB Object for insertion:
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Inserting the data:
        long id = db.insert(BookEntry.TABLE_NAME, null, values);

        // Checking if data insertion was successful:
        return ContentUris.withAppendedId(uri,id);
    }
}
