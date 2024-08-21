package com.example.ecommerce.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;

    // Products table
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_IMAGE = "image";
    public static final String COLUMN_PRODUCT_DISCOUNT = "discount";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_BRAND = "brand";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";

    // Orders table
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_DATE = "date";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_ORDER_STATUS = "status";

    // OrderItems table
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String COLUMN_ORDER_ITEM_ID = "order_item_id";
    public static final String COLUMN_ORDER_ID_ORDER_ITEMS = "order_id";
    public static final String COLUMN_PRODUCT_ID_ORDER_ITEMS = "product_id";
    public static final String COLUMN_QUANTITY = "quantity";

    // Cart Items table
    public static final String TABLE_CART_ITEMS = "cart_items";
    public static final String COLUMN_CART_ITEM_ID = "cart_item_id";
    public static final String COLUMN_PRODUCT_ID_CART_ITEMS = "product_id";
    public static final String COLUMN_QUANTITY_CART_ITEMS = "quantity";


    // SQL to create products table
    private static final String CREATE_TABLE_PRODUCTS = String.format(
            "CREATE TABLE %s ("
                    + "%s INTEGER PRIMARY KEY, "
                    + "%s TEXT, "
                    + "%s TEXT, "
                    + "%s REAL, "
                    + "%s TEXT, "
                    + "%s REAL, "
                    + "%s INTEGER, "
                    + "%s TEXT, "
                    + "%s TEXT);",
            TABLE_PRODUCTS,
            COLUMN_PRODUCT_ID,
            COLUMN_PRODUCT_NAME,
            COLUMN_PRODUCT_DESCRIPTION,
            COLUMN_PRODUCT_PRICE,
            COLUMN_PRODUCT_IMAGE,
            COLUMN_PRODUCT_DISCOUNT,
            COLUMN_PRODUCT_QUANTITY,
            COLUMN_PRODUCT_BRAND,
            COLUMN_PRODUCT_CATEGORY
    );

    // SQL to create orders table
    private static final String CREATE_TABLE_ORDERS = String.format(
            "CREATE TABLE %s ("
                    + "%s TEXT PRIMARY KEY, "
                    + "%s REAL, "
                    + "%s TEXT, "
                    + "%s TEXT);",
            TABLE_ORDERS,
            COLUMN_ORDER_ID,
            COLUMN_TOTAL_PRICE,
            COLUMN_ORDER_STATUS,
            COLUMN_ORDER_DATE
    );

    // SQL to create order_items table
    private static final String CREATE_TABLE_ORDER_ITEMS = "CREATE TABLE "
            + TABLE_ORDER_ITEMS + " ("
            + COLUMN_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ORDER_ID_ORDER_ITEMS + " TEXT, "
            + COLUMN_PRODUCT_ID_ORDER_ITEMS + " TEXT, "
            + COLUMN_QUANTITY + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_ORDER_ID_ORDER_ITEMS + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), "
            + "FOREIGN KEY(" + COLUMN_PRODUCT_ID_ORDER_ITEMS + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
            + ");";


    // SQL to create cart_items table
    private static final String CREATE_TABLE_CART_ITEMS = "CREATE TABLE "
            + TABLE_CART_ITEMS + " ("
            + COLUMN_CART_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PRODUCT_ID_CART_ITEMS + " TEXT, "
            + COLUMN_QUANTITY_CART_ITEMS + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_PRODUCT_ID_CART_ITEMS + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
            + ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_ORDERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_ORDER_ITEMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_CART_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        onCreate(sqLiteDatabase);
    }
}
