package com.openiptv.code;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseActions extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseActions";

    public static Bundle activeAccount;


    private static final String ACTIVE_ACCOUNT_TABLE = "activeAccountTable";

    private static final String TABLE_NAME = "userDatabase";
    private static final String COL1 = "ID";
    private static final String COL2 = "username";
    private static final String COL3 = "password";
    private static final String COL4 = "hostname";
    private static final String COL5 = "port";
    private static final String COL6 = "clientName";


    public DatabaseActions(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseActions(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT, " + COL6 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);

        createTable = "CREATE TABLE " + ACTIVE_ACCOUNT_TABLE + " (" + COL1 + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + ACTIVE_ACCOUNT_TABLE);
        onCreate(sqLiteDatabase);

    }

    public void removeActiveAccount() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE " + ACTIVE_ACCOUNT_TABLE);

        String createTable = "CREATE TABLE " + ACTIVE_ACCOUNT_TABLE + " (" + COL1 + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);

    }

    public boolean setActiveAccount(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        removeActiveAccount();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, id);
        try {
            sqLiteDatabase.insertOrThrow(ACTIVE_ACCOUNT_TABLE, null, contentValues);

        } catch (Exception e) {
            return false;
        }

        activeAccount = this.accountToBundle(this.getAccountByID(id));

        return true;
    }

    public boolean syncActiveAccount() {
        activeAccount = this.accountToBundle(this.getAccountByID(this.getActiveAccount()));

        return true;
    }

    /**
     * @return
     */
    public String getActiveAccount() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + ACTIVE_ACCOUNT_TABLE;
        Cursor account = sqLiteDatabase.rawQuery(query, null);
        account.moveToFirst();
        return account.getString(0);
    }


    public Bundle accountToBundle(Cursor account) {
        account.moveToFirst();
        Bundle accountToBundle = new Bundle();

        accountToBundle.putString("id", account.getString(0));
        accountToBundle.putString("username", account.getString(1));
        accountToBundle.putString("password", account.getString(2));
        accountToBundle.putString("hostname", account.getString(3));
        accountToBundle.putString("port", account.getString(4));
        accountToBundle.putString("clientName", account.getString(5));

        return accountToBundle;

    }

    /**
     * Add and account to the database
     *
     * @param account
     * @return true on success, false on error thrown
     */
    public boolean addAccount(TVHeadendAccount account) {

        /**
         * If any entries are equal to "", return false
         */
        if (account.getUsername().equals("") || account.getPassword().equals("") ||
                account.getHostname().equals("") || account.getPort().equals("") ||
                account.getClientName().equals("")) {
            return false;
        }
        /**
         * Check port is a number if not return false
         */
        try {
            Integer.parseInt(account.getPort());
        } catch (NumberFormatException e) {
            return false;
        }


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, account.getUsername());
        contentValues.put(COL3, account.getPassword());
        contentValues.put(COL4, account.getHostname());
        contentValues.put(COL5, account.getPort());
        contentValues.put(COL6, account.getClientName());
        try {
            sqLiteDatabase.insertOrThrow(TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            return false;
        }

        // Get id of just added and set it to active account


        String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC";
        Cursor accountIDs = sqLiteDatabase.rawQuery(query, null);
        accountIDs.moveToFirst();
        setActiveAccount(accountIDs.getString(0));

        activeAccount = accountToBundle(accountIDs);

        return true;
    }

    /**
     * Return all accounts
     *
     * @return
     */
    public Cursor getAccounts() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor accounts = sqLiteDatabase.rawQuery(query, null);
        return accounts;


    }

    /**
     * Returns an account after being passed an ID
     *
     * @param name
     * @return
     */
    public Cursor getAccountByID(String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + name + "'";
        Cursor account = sqLiteDatabase.rawQuery(query, null);
        return account;
    }

    /**
     * Returns an account after being passed the client name
     *
     * @param name
     * @return
     */
    public Cursor getAccountByClientName(String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL6 + " = '" + name + "'";
        Cursor account = sqLiteDatabase.rawQuery(query, null);
        return account;
    }

    /**
     * Removes an account from the database. Requires the id and the client name.
     *
     * @param id
     * @param clientName
     */
    public void deleteAccount(int id, String clientName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + id + "' AND " + COL6 + " = '" + clientName + "'";

        sqLiteDatabase.execSQL(query);
    }

    /**
     * Remove all accounts with a certain name rather than just the first.
     * @param clientName
     */
    public void clearAccountsClientName(String clientName) {
        Cursor accounts = getAccountByClientName(clientName);

        while (accounts.moveToNext()) {
            deleteAccount(Integer.parseInt(accounts.getString(0)), accounts.getString(5));
        }
    }
}
