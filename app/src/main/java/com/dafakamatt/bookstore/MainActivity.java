package com.dafakamatt.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.dafakamatt.bookstore.data.BookDbHelper;
import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    private BookDbHelper mDbHelper;
    BookCursorAdaptor mCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Calling our BookDBHelper that will create a Database, if required
        mDbHelper = new BookDbHelper(this);

        ListView bookListView = findViewById(R.id.list);

        mCursorAdaptor = new BookCursorAdaptor(this,null);

        bookListView.setAdapter(mCursorAdaptor);

        // Initializing loader:
        getLoaderManager().initLoader(BOOK_LOADER, null, this);


        // Floating action button code:
        FloatingActionButton newProductFab = findViewById(R.id.new_product_fab);
        newProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

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

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        String[] columnProjection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY
        };

        switch (loaderId) {
            case BOOK_LOADER:
                return new CursorLoader(
                        this,
                        BookEntry.CONTENT_URI,
                        columnProjection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdaptor.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdaptor.swapCursor(null);
    }
}
