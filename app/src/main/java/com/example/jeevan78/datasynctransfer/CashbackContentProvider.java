package com.example.jeevan78.datasynctransfer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by devil on 2/5/2018.
 */

public class CashbackContentProvider extends ContentProvider {

    private CashbackSQLiteOpenHelper sqLiteOpenHelper;

    private static final String CASHBACK_DBNAME = "zoftino_cashback";

    private static final String CASHBACK_TABLE = "cashback_t";

    private SQLiteDatabase cbDB;

    private static final String SQL_CREATE_CASHBACK = "CREATE TABLE " +
            CASHBACK_TABLE +
            "(" +
            "_id INTEGER PRIMARY KEY, " +
            "STORE TEXT, " +
            "CASHBACK TEXT)";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("com.zoftino.sync.cashback", CASHBACK_TABLE, 1);
    }
    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new CashbackSQLiteOpenHelper( getContext(), CASHBACK_DBNAME, SQL_CREATE_CASHBACK);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        String tableNme = "";
        switch(uriMatcher.match(uri)){
            case 1 :
                tableNme = CASHBACK_TABLE;
                break;
            default:
                return null;
        }

        cbDB = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = (SQLiteCursor) cbDB.query(tableNme, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        try {
            String tableNme = "";
            switch (uriMatcher.match(uri)) {
                case 1:
                    tableNme = CASHBACK_TABLE;
                    break;
                default:
                    return null;
            }

            cbDB = sqLiteOpenHelper.getWritableDatabase();
            long rowid = cbDB.insert(tableNme, null, contentValues);
            return getContentUriRow(rowid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs) {
        String tableNme = CASHBACK_TABLE;

        cbDB = sqLiteOpenHelper.getWritableDatabase();

        return cbDB.delete(tableNme, where, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] selectionArgs) {
        String tableNme = CASHBACK_TABLE;

        cbDB = sqLiteOpenHelper.getWritableDatabase();
        return cbDB.update(tableNme,contentValues,where,selectionArgs );
    }
    private Uri getContentUriRow(long rowid){
        return Uri.fromParts("com.zoftino.sync.cashback", CASHBACK_TABLE, Long.toString(rowid));
    }
    public class CashbackSQLiteOpenHelper extends SQLiteOpenHelper {

        private String sql;
        CashbackSQLiteOpenHelper(Context context, String dbName, String msql) {
            super(context, dbName, null, 1);
            sql = msql;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {  }
    }
}

