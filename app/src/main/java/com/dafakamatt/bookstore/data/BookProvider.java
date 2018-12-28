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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Getting readable version of the database:
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of our query:
        Cursor cursor = null;


        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Inserting new book into the Database. See helper methods underneath
        // the overridden methods below:
        final int match = sUriMatcher.match(uri);
        switch (match) {
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
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri,contentValues,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

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

        // Notify all listeners that the data has changed for the book content uri:
        getContext().getContentResolver().notifyChange(uri, null);

        // Checking if data insertion was successful:
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateBook (Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product Requires a Name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            String price = values.getAsString(BookEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Product requires a price value");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0 ) {
                throw new IllegalArgumentException("Book Produce Line requires valid quantity");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product requires valid supplier name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Product requires valid supplier telephone number");
            }
        }

        // If there's no values to update, bail out:
        if (values.size() == 0) {
            return values.size();
        }

        // Otherwise, get writable database instance and update data:
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows are updated, then notify all listeners of the change:
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        // Return number of affected rows by the change:
        return rowsUpdated;

    }
}
