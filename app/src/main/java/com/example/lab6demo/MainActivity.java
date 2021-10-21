package com.example.lab6demo;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;



public class MainActivity extends AppCompatActivity {

    private static final String TABLE_PRODUCTS = "0";
    private static final String COLUMN_ID = "0";
    private static final String COLUMN_PRODUCTNAME = "0";
    private static final String COLUMN_SKU = "0";
    TextView idView;
    EditText productBox;
    EditText skuBox;

    public class MyDBHandler extends SQLiteOpenHelper{
        public MyDBHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "productDB.db";
        public static final String TABLE_PRODUCTS = "products";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PRODUCTNAME = "productname";
        public static final String COLUMN_SKU = "SKU";


        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                    TABLE_PRODUCTS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_PRODUCTNAME
                    + " TEXT," + COLUMN_SKU + " INTEGER" + ")";
            db.execSQL(CREATE_PRODUCTS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            onCreate(db);
        }

        public void addProduct(Product product){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCTNAME, product.getProductName());
            values.put(COLUMN_SKU, product.getSku());
            db.insert(TABLE_PRODUCTS, null, values);
            db.close();


        }

        public boolean deleteProduct(String productname) {

            boolean result = false;
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " +
                    COLUMN_PRODUCTNAME + " = \"" + productname + "\"";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                String idStr = cursor.getString(0);
                db.delete(TABLE_PRODUCTS, COLUMN_ID + " = " + idStr, null);
                cursor.close();
                result = true;

            }

            db.close();
            return result;
        }

        public Product findProduct(String productname) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " +
                    COLUMN_PRODUCTNAME + " = \"" + productname + "\"";
            Cursor cursor = db.rawQuery(query, null);

            Product product = new Product();
            if(cursor.moveToFirst()){

                product.setID(Integer.parseInt(cursor.getString(0)));
                product.setProductName(cursor.getString(1));
                product.setSku(Integer.parseInt(cursor.getString(2)));
                cursor.close();

            }else{
                product= null;
            }
            db.close();
            return product;

        }
    }


    public void newProduct (View view) {

        int sku = Integer.parseInt(skuBox.getText().toString());

        Product product = new Product(productBox.getText().toString(), sku);

        // TODO: add to database

        productBox.setText("");

        skuBox.setText("");

        MyDBHandler dbHandler = new MyDBHandler(this);
        dbHandler.addProduct(product);

    }



    public void lookupProduct (View view) {

        MyDBHandler dbHandler = new MyDBHandler(this);
        Product product = dbHandler.findProduct(productBox.getText().toString());


        if (product != null) {
            idView.setText(String.valueOf(product.getID()));
            skuBox.setText(String.valueOf(product.getSku()));
        } else {
            idView.setText("No Match Found");
        }
    }


    public void removeProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this);
        boolean result = dbHandler.deleteProduct(productBox.getText().toString());

        if (result) {
            idView.setText("Record Deleted");
            productBox.setText("");
            skuBox.setText("");
        }
        else
            idView.setText("No Match Found");
    }

    public void about(View view) {
        Intent aboutIntent = new Intent(this, About.class);
        startActivity(aboutIntent);
    }
}
