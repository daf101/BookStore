package com.dafakamatt.bookstore;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.dafakamatt.bookstore.data.BookDbHelper;
import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class BookCursorAdaptor extends CursorAdapter {

    // Required Constructor:
    public BookCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0 /*flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflating the list item:
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Hooking UI elements from list_item.xml:
        TextView productNameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceDollarValueTextView = view.findViewById(R.id.price_dollar_value_text_view);
        final TextView stockOnHandQuantityTextView = view.findViewById(R.id.stock_on_hand_quantity_text_view);

        // Pulling book data from the DB:
        int currentBookDbIdIndex = cursor.getColumnIndex(BookEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceDollarValueColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int stockOnHandQuantityValueColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        final int currentBookDbId = cursor.getInt(currentBookDbIdIndex);
        final String productName = cursor.getString(productNameColumnIndex);
        final String priceDollarValue = cursor.getString(priceDollarValueColumnIndex);
        final int stockOnHandQuantityValue = cursor.getInt(stockOnHandQuantityValueColumnIndex);
        String strStockOnHandQuantityValue = Integer.toString(stockOnHandQuantityValue);
        final String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

        // Applying values to the TextViews:
        productNameTextView.setText(productName);
        priceDollarValueTextView.setText(priceDollarValue);
        stockOnHandQuantityTextView.setText(strStockOnHandQuantityValue);

        // We'll plug these in later, but bring them into variables for now so I
        // don't forget:
        Button saleButton = view.findViewById(R.id.sale_button);
        Button contactSupplierButton = view.findViewById(R.id.contact_supplier_button);

        // Need help with this feature. Not having much luck with decrementing the values
        // have engaged mentors for some assistance. I have used a direct DB call for now
        // via the DbHelper, I was unable to get it to work with a content loader within the
        // cursor adaptor however. I found the work around in this stackoverflow post:
        // https://stackoverflow.com/questions/44034208/updating-listview-with-cursoradapter-after-an-onclick-changes-a-value-in-sqlite
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCurrentQty = stockOnHandQuantityTextView.getText().toString();
                int intCurrentQty = Integer.parseInt(strCurrentQty);

                String strNewCurrentQty;
                if (intCurrentQty > 0) {
                    intCurrentQty--;
                    strNewCurrentQty = Integer.toString(intCurrentQty);
                    BookDbHelper mDbHelper = new BookDbHelper(context);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, strNewCurrentQty);
                    db.update(BookEntry.TABLE_NAME, values, BookEntry._ID + "=" + currentBookDbId, null);
                    stockOnHandQuantityTextView.setText(strNewCurrentQty);
                }
            }
        });

        contactSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String phoneNumberUri = "tel:" + supplierPhoneNumber;
                intent.setData(Uri.parse(phoneNumberUri));
                context.startActivity(intent);
            }
        });

    }
}
