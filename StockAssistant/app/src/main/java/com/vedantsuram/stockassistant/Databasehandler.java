package com.vedantsuram.stockassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Databasehandler extends SQLiteOpenHelper {



    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockAssistantTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private final SQLiteDatabase Database;
    private static final String SQL_CREATE_TABLE = "" +
            "CREATE TABLE " + TABLE_NAME + " ( " +
                            SYMBOL + " TEXT not null unique," +
                            COMPANY + " TEXT not null )" ;

    public Databasehandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        Database = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public ArrayList<Stock> loadStockList() {
        ArrayList<Stock> TempstockList = new ArrayList<>();
        Cursor cursor = Database.query(
                TABLE_NAME,
                new String[]{SYMBOL, COMPANY},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                Stock add = new Stock(cursor.getString(0), cursor.getString(1), 0.0, 0.0, 0.0 );
                TempstockList.add(add);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return TempstockList;
    }

    public void AddnewDB( String symbol, String CName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYMBOL, symbol);
        contentValues.put(COMPANY, CName);
        Database.insert(TABLE_NAME,null, contentValues);
    }

    public void deleteStock(String symbol) {
        Database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{symbol});
    }

    public void shutDown() {
        Database.close();
    }
}
