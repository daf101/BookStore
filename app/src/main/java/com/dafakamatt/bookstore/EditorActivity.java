package com.dafakamatt.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    // Assigning Loader ID Constant:
    private static final int EXISTING_BOOK_LOADER_ID = 0;

    // Instantiating UI elements from xml:
    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mStockQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;

    // Text in Increment/Decrement buttons for Qty will
    // Always be the same:
    private static final String DECREMENT_BUTTON_TEXT = "-";
    private static final String INCREMENT_BUTTON_TEXT = "+";

    // Uri variable to use to check if we're creating a new product line
    // or editing an existing one:
    private Uri mCurrentBookUri;

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

        // Hooking edit buttons in activity_editor.xml:
        mProductNameEditText = findViewById(R.id.product_name_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mStockQuantityEditText = findViewById(R.id.stock_quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSupplierPhoneNumberEditText = findViewById(R.id.supplier_phone_number_edit_text);
        Button mStockDecrement = findViewById(R.id.stock_decrement_button);
        Button mStockIncrement = findViewById(R.id.stock_increment_button);
        Button phoneSupplier = findViewById(R.id.contact_supplier_edit_button);

        // Hooking up Decrement Button and applying onClickListener:
        mStockDecrement.setText(DECREMENT_BUTTON_TEXT);
        mStockDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockDecrement();
            }
        });

        // Hooking up Increment Button and applying onClickListener:
        mStockIncrement.setText(INCREMENT_BUTTON_TEXT);
        mStockIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockIncrement();
            }
        });

        phoneSupplier.setText(getString(R.string.phone_supplier));
        phoneSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String phoneNumberUri = "tel:" + supplierPhoneNumber;
                intent.setData(Uri.parse(phoneNumberUri));
                startActivity(intent);
            }
        });
    }

    // Decrementing the number shown in mStockQuantityEditText
    private void stockDecrement() {
        String strQuantity = mStockQuantityEditText.getText().toString();
        if (TextUtils.isEmpty(strQuantity)) {
            strQuantity = "0";
        }
        int intQuantity = Integer.parseInt(strQuantity);
        intQuantity--;
        if (intQuantity < 0) {
            Toast.makeText(this,
                    getString(R.string.stock_quantity_cant_be_less_than_zero),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String strNewQuantity = Integer.toString(intQuantity);
        mStockQuantityEditText.setText(strNewQuantity);
    }

    // Incrementing the number shown in mStockQuantityEditText
    private void stockIncrement() {
        String strQuantity = mStockQuantityEditText.getText().toString();
        if (TextUtils.isEmpty(strQuantity)) {
            strQuantity = "0";
        }
        int intQuantity = Integer.parseInt(strQuantity);
        intQuantity++;
        String strNewQuantity = Integer.toString(intQuantity);

        mStockQuantityEditText.setText(strNewQuantity);
    }

    private void saveProductLine() {
        // Pulling Strings from user input:
        String productName = mProductNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String stockQuantity = mStockQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Checking each field contains valid info, otherwise, display toast asking
        // user to check their fields:
        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(price) || TextUtils.isEmpty(stockQuantity) ||
                TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhoneNumber)) {
            Toast.makeText(this, getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparing data for DB insertion with ContentValues object:
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, stockQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        try {
            if (mCurrentBookUri == null) {
                // New Pet Mode:
                getContentResolver().insert(BookEntry.CONTENT_URI, values);
                Toast.makeText(this, getString(R.string.product_line_saved_successfully) + productName, Toast.LENGTH_SHORT).show();
            } else {
                // Edit Pet Mode:
                String selectionClause = UserDictionary.Words.LOCALE + " LIKE ?";
                String[] selectionArgs = {"en_%"};
                getContentResolver().update(mCurrentBookUri, values, selectionClause, selectionArgs);
            }

        } catch (Exception e) {
            Log.e("saveProductLine", "Error Saving Product Line", e);
            Toast.makeText(this, getString(R.string.error_saving_product_line), Toast.LENGTH_SHORT).show();
        } finally {
            finish();
        }
    }

    // Checks with the user if they're sure they want to delete the product
    // and if they confirm, delete
    private void confirmAndDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_alert));
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the alert dialog:
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // Helper method to take care of the row deletion:
    private void deleteProduct() {
        int rowsDeleted;
        rowsDeleted = getContentResolver().delete(
                mCurrentBookUri,
                null,
                null
        );
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_error), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_product_confirm), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Overridden options menu methods here:
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If this is a new book, hide the "Delete Product" item:
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User has clicked a button from the options menu:
        switch (item.getItemId()) {
            case R.id.action_delete:
                confirmAndDelete();
                return true;
            case R.id.action_save:
                saveProductLine();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

        // Setting Text for each Edit Text readying for the user to modify:
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
