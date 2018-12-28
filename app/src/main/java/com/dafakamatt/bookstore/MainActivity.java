package com.dafakamatt.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Assigning a book loader ID constant:
    private static final int BOOK_LOADER = 0;

    // Creating custom cursor adaptor object:
    BookCursorAdaptor mCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up our ListView showing our book product lines
        // and applying a custom cursor adaptor:
        ListView bookListView = findViewById(R.id.list);
        mCursorAdaptor = new BookCursorAdaptor(this, null);
        bookListView.setAdapter(mCursorAdaptor);

        // When user clicks a book item, we start the editor activity in a "Edit" mode:
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        // Creating empty view for when there's no list items:
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

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

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        // Defining column projection for required product fields here:
        String[] columnProjection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        // We only need to make one call to SQL via our Cursor Loader:
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
