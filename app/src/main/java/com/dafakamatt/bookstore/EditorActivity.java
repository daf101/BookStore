package com.dafakamatt.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    // Uri variable to use to check if we're creating a new product line
    // or editing an existing one:
    private Uri mCurrentBookUri;

    // Other primitive global variables:
    private int rowsUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Working out EditorActivity Mode:
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            // New Product Line
            setTitle(getString(R.string.editor_title_new_book));
        } else {
            // Modifying existing:
            setTitle(getString(R.string.editor_title_edit_existing));
            // Readying up the loader to pull info into EditTexts for user to modify:
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);


        }


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
        values.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, stockQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        Uri newUri = null;

        try {
            if (mCurrentBookUri == null) {
                // New Pet Mode:
                newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                Toast.makeText(this, getString(R.string.product_line_saved_successfully) + productName, Toast.LENGTH_SHORT).show();
            } else {
                // Edit Pet Mode:
                String selectionClause = UserDictionary.Words.LOCALE + " LIKE ?";
                String[] selectionArgs = {"en_%"};
                rowsUpdated = getContentResolver().update(mCurrentBookUri,values,selectionClause,selectionArgs);
            }

        } catch (Exception e) {
            Log.e("saveProductLine", "Error Saving Product Line", e);
            Toast.makeText(this, getString(R.string.error_saving_product_line), Toast.LENGTH_SHORT).show();
        } finally {
            finish();
        }
    }

    /**
     * Loader methods here. Used to pull data out of the database and update
     * EditTexts in EditorActivity so the user can modify data if needed.
     *
     * @param loaderId loader ID declared at the top of the class
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        // Defining projection for what we want to display in the EditTexts:
        String[] columnProjection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Pulling data from Database using a CursorLoader
        switch (loaderId) {
            case EXISTING_BOOK_LOADER_ID:
                return new CursorLoader(
                        this,
                        mCurrentBookUri,
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
        String productName;
        String price;
        int quantity;
        String strQuantity;
        String supplierName;
        String supplierPhoneNumber;


        // If theres no data, bail out:
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToNext()) {
            // Find the column indices of the book attributes that we're interested in:
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extracting the values from the cursor by referencing the column indices:
            productName = cursor.getString(productNameColumnIndex);
            price = cursor.getString(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            strQuantity = Integer.toString(quantity);

            supplierName = cursor.getString(supplierNameColumnIndex);
            supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Applying the text pulled from DB into the edit texts. User can now edit them as they please:
            mProductNameEditText.setText(productName);
            mPriceEditText.setText(price);
            mStockQuantityEditText.setText(strQuantity);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mPriceEditText.setText("");
        mStockQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }
}
