package com.dafakamatt.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

import java.util.Locale;

public class BookCursorAdaptor extends CursorAdapter {

    private Uri mCurrentBookUri;

    // Required Constructor:
    public BookCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0 /*flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflating the list item:
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Hooking UI elements from list_item.xml:
        TextView productNameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceDollarValueTextView = view.findViewById(R.id.price_dollar_value_text_view);
        TextView stockOnHandQuantityTextView = view.findViewById(R.id.stock_on_hand_quantity_text_view);

        // Pulling book data from the DB:
        int currentBookDbIdIndex = cursor.getColumnIndex(BookEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceDollarValueColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int stockOnHandQuantityValueColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        int currentBookDbId = cursor.getInt(currentBookDbIdIndex);
        final String productName = cursor.getString(productNameColumnIndex);
        final String priceDollarValue = cursor.getString(priceDollarValueColumnIndex);
        final int stockOnHandQuantityValue = cursor.getInt(stockOnHandQuantityValueColumnIndex);
        String strStockOnHandQuantityValue = Integer.toString(stockOnHandQuantityValue);
        final String supplierName = cursor.getString(supplierNameColumnIndex);
        final String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

        // Applying values to the TextViews:
        productNameTextView.setText(productName);
        priceDollarValueTextView.setText(priceDollarValue);
        stockOnHandQuantityTextView.setText(strStockOnHandQuantityValue);

        // Constructing current book URI so we can get the Telephone number/Perform a sale:
        mCurrentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentBookDbId);

        // We'll plug these in later, but bring them into variables for now so I
        // don't forget:
        Button saleButton = view.findViewById(R.id.sale_button);
//        Button contactSupplierButton = view.findViewById(R.id.contact_supplier_button);



        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newStockOnHandQuantityValue = stockOnHandQuantityValue - 1;
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
                values.put(BookEntry.COLUMN_PRICE, priceDollarValue);
                values.put(BookEntry.COLUMN_QUANTITY, newStockOnHandQuantityValue);
                values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
                values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER,supplierPhoneNumber);

                String selectionClause = UserDictionary.Words.LOCALE + " LIKE ?";
                String[] selectionArgs = {"en_%"};

                // Updating DB with new value:
                int rowsUpdated = context.getContentResolver().update(mCurrentBookUri,values,selectionClause,selectionArgs);
                context.getContentResolver().notifyChange(BookEntry.CONTENT_URI, null);
            }
        });

    }
}
