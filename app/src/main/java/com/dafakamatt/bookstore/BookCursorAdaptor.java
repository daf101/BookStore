package com.dafakamatt.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class BookCursorAdaptor extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Hooking UI elements from list_item.xml:
        TextView productNameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceDollarValueTextView = view.findViewById(R.id.price_dollar_value_text_view);
        TextView stockOnHandQuantityTextView = view.findViewById(R.id.stock_on_hand_quantity_text_view);

        // Pulling book data from the DB:
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceDollarValueColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int stockOnHandQuantityValueColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        String productName = cursor.getString(productNameColumnIndex);
        String priceDollarValue = cursor.getString(priceDollarValueColumnIndex);
        int stockOnHandQuantityValue = cursor.getInt(stockOnHandQuantityValueColumnIndex);

        // Applying values to the TextViews:
        productNameTextView.setText(productName);
        priceDollarValueTextView.setText(priceDollarValue);
        stockOnHandQuantityTextView.setText(stockOnHandQuantityValue);

        // We'll plug these in later, but bring them into variables for now so I
        // don't forget:
        Button saleButton = view.findViewById(R.id.sale_button);
        Button contactSupplierButton = view.findViewById(R.id.contact_supplier_button);

    }
}
