package com.dafakamatt.bookstore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dafakamatt.bookstore.data.BookDbHelper;
import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class MainActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Calling our BookDBHelper that will create a Database, if required
        mDbHelper = new BookDbHelper(this);

        FloatingActionButton newProductFab = findViewById(R.id.new_product_fab);
        newProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Display the table in a single TextView to validate the process.
        displayBooksTable();
    }

    // Method to insert sample data to validate DB functionality:
    private void insertDummyData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues dummyValues = new ContentValues();
        dummyValues.put(BookEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_product_name));
        dummyValues.put(BookEntry.COLUMN_PRICE, getString(R.string.dummy_price));
        // Sample quantity is stored in strings.xml. Converting to int:
        String strQuantity = getString(R.string.dummy_quantity);
        dummyValues.put(BookEntry.COLUMN_QUANTITY, Integer.parseInt(strQuantity));

        // Adding remaining values:
        dummyValues.put(BookEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        dummyValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, getString(R.string.dummy_supplier_phone_number));

        // Inserting new row:
        db.insert(BookEntry.TABLE_NAME, null, dummyValues);

        // Displaying updated books table:
        displayBooksTable();
    }

    private void displayBooksTable() {
        // Getting a readable instance of the db:
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Defining our column projection in a string array:
        String[] inventoryColumnProjection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        // Running SQL query to our DB:
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                inventoryColumnProjection,
                null,
                null,
                null,
                null,
                null);

        // Getting the TextView in activity_main.xml:
        TextView inventoryTextView = findViewById(R.id.inventory_text_view);

        // Updating the TextView with the current table:
        try {
            // Setting the first line:
            inventoryTextView.setText(getString(R.string.heading_books_table_contains) +
                    " " +
                    cursor.getCount() +
                    " " +
                    getString(R.string.strBooks) +
                    "\n\n");

            // Appending headers to make it easier to read:
            inventoryTextView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_PRICE + " - " +
                    BookEntry.COLUMN_QUANTITY + " - " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER +
                    "\n\n");

            // Gathering Column Indices:
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Appending the textview with the appropriate row text:
            while (cursor.moveToNext()) {
                // Getting our values:
                int currentId = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String strCurrentQuantity = Integer.toString(currentQuantity);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

                // Appending those values to the textview:
                inventoryTextView.append(currentId + " - " +
                        currentProductName + " - " +
                        currentPrice + " - " +
                        strCurrentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber + "\n");
            }

        } finally {
            cursor.close();
        }
    }

    // Created options menu to insert the dummy data. Why not considering we just learned how to do it? :)
    // Overriding the onCreateOptionsMenu to inflate the menu_catalog layout:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    // Overriding the onOptionsItemSelected method to call insertDummyData() when needed:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_option_item_insert_dummy_data:
                insertDummyData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
