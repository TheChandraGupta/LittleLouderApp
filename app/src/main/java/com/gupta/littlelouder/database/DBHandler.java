package com.gupta.littlelouder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gupta.littlelouder.bean.User;

/**
 * Created by GUPTA on 14-May-16.
 */
public class DBHandler extends SQLiteOpenHelper{

    // LOG Tag
    private static final String TAG = "DBHandler";

    // Database Version. This Should be increment after every changes in database.
    private static final int DATABASE_VERSION = 1;

    // Database name. This should end with .db extension
    private static final String DATABASE_NAME = "louder.db";

    // Table Names
    public static final String TABLE = "user";

    // Attributes Names
    public static final String KEY_ID = "userid";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String TYPE = "type";
    public static final String DOJ = "doj";
    public static final String REMEMBER = "remember";

    // Create Tables
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            EMAIL + " TEXT NOT NULL, " +
            PHONE + " TEXT NOT NULL, " +
            PASSWORD + " TEXT NOT NULL, " +
            TYPE + " TEXT NOT NULL, " +
            DOJ + " TEXT NOT NULL, " +
            REMEMBER + " TEXT NOT NULL " +
            ");";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "Creating Table......");

        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "USER TABLE CREATED");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXIST " + TABLE);
        Log.d(TAG, "TABLE_EXPENSE TABLE DELETED");

        onCreate(db);

    }

    /*
    OPERATION
     */

    public boolean isUserLoggedIn() {

        SQLiteDatabase db = getWritableDatabase();

        boolean check = false;
        String query = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        //Cursor points to the results
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast()) {

            check = true;

            c.moveToNext();

        }

        db.close();

        Log.d(TAG, "Row Fetched from TABLE");

        return check;

    }


    /*
    USER TABLE CRUD OPERATION
     */

    public void addNewUser(User user) {

        ContentValues values = new ContentValues();

        values.put(KEY_ID, user.getUserId());
        values.put(NAME, user.getName());
        values.put(EMAIL, user.getEmail());
        values.put(PHONE, user.getPhone());
        values.put(PASSWORD, user.getPassword());
        values.put(TYPE, user.getType());
        values.put(DOJ, user.getdOJ());
        values.put(REMEMBER, user.getRemember());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE, null, values);
        Log.d(TAG, "Row Inserted in TABLE");

        db.close();


    }


    public User getUserDetail() {

        SQLiteDatabase db = getWritableDatabase();

        User user = new User();

        String query = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        //Cursor points to the results
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast()) {

            user.setUserId(c.getInt(c.getColumnIndex(KEY_ID)));
            user.setName(c.getString(c.getColumnIndex(NAME)));
            user.setEmail(c.getString(c.getColumnIndex(EMAIL)));
            user.setPhone(c.getString(c.getColumnIndex(PHONE)));
            user.setPassword(c.getString(c.getColumnIndex(PASSWORD)));
            user.setType(c.getString(c.getColumnIndex(TYPE)));
            user.setdOJ(c.getString(c.getColumnIndex(DOJ)));
            user.setRemember(c.getString(c.getColumnIndex(REMEMBER)));

            c.moveToNext();

        }

        db.close();

        Log.d(TAG, "Row Fetched from TABLE");

        return user;

    }

    public void deleteUser(int userId) {

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE + " WHERE " + KEY_ID + "=" + userId + ";");

        Log.d(TAG, "Row Deleted in TABLE");

    }

}
