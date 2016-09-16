package com.wvs.shoppercrux.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by root on 20/7/16.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "shopper_api";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_GOOGLE_USER="google_user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CUSTOMER_ID ="customer_id";
    private static final String KEY_GOOGLE_NAME = "firstname";

    public static String user_id;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_PHONE + " TEXT,"
                + KEY_PASSWORD +" TEXT,"
                + KEY_CUSTOMER_ID +" INTEGER"+")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_GOOGLE_LOGIN="CREATE TABLE " + TABLE_GOOGLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_CUSTOMER_ID +" INTEGER"+")";

        db.execSQL(CREATE_GOOGLE_LOGIN);

        Log.d("ShopperCrux", "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String customer_id ,String name, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONE, phone); // Phone
        values.put(KEY_PASSWORD,password); // password
        values.put(KEY_CUSTOMER_ID, customer_id); // customer id

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d("ShopperCrux", "New user inserted into sqlite: " + id);
    }

    public void addUserWithoutPassword(String customer_id ,String name, String email, String phone){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONE, phone); // Phone
        values.put(KEY_CUSTOMER_ID, customer_id); // customer id

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d("ShopperCrux", "New user inserted into sqlite: " + id);
    }

    public void addGoogleUser(String customer_id ,String name, String email ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CUSTOMER_ID, customer_id); // customer id

        // Inserting Row
        long id = db.insert(TABLE_GOOGLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d("ShopperCrux", "New user inserted into sqlite: " + id);
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("phone", cursor.getString(3));
            user.put("password", cursor.getString(4));
            user.put("customer_id",cursor.getString(5));
            user_id = cursor.getString(5);
        }
        //
        cursor.close();
        db.close();
        // return user
        Log.d("ShopperCrux", "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getGoogleUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_GOOGLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("customer_id",cursor.getString(3));
            user_id = cursor.getString(3);
        }
        //
        cursor.close();
        db.close();
        // return user
        Log.d("ShopperCrux", "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d("ShopperCrux", "Deleted all user info from sqlite");
    }

}
