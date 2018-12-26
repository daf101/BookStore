package com.dafakamatt.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dafakamatt.bookstore.data.BooksContract.BookEntry;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Declaring Global Variables
     */
    // Assigning Loader ID:
    private static final int EXISTING_BOOK_LOADER_ID = 0;
    // Instantiating UI elements from xml:
    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mStockQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Hooking edit texts in activity_editor.xml:
        mProductNameEditText = findViewById(R.id.product_name_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mStockQuantityEditText = findViewById(R.id.stock_quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSupplierPhoneNumberEditText = findViewById(R.id.supplier_phone_number_edit_text);
        mSaveButton = findViewById(R.id.editor_save_button);

        // Hooking save button to saveProductLine(). This will be changed soon to
        // action bar:
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProductLine();
            }
        });

        // Readying up the loader:
        //getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID,null,this);
    }

    private void saveProductLine() {
        // Pulling Strings from user input:
        String productName = mProductNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String stockQuantity = mStockQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Preparing data for DB insertion with ContentValues object:
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME,productName);
        values.put(BookEntry.COLUMN_PRICE,price);
        values.put(BookEntry.COLUMN_QUANTITY,stockQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME,supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER,supplierPhoneNumber);

        try{
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI,values);
        }
        catch(Exception e) {
            Log.e("saveProductLine","Error Saving Product Line",e);
        }
    }

    /**
     * Loader methods here:
     * @param loaderId loader ID declared at the top of the class
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        String[] columnProjection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        switch (loaderId) {
            case EXISTING_BOOK_LOADER_ID:
                return new CursorLoader(
                        this,
                        null,
                        columnProjection,
                        null,
                        null,
                        null
                );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
