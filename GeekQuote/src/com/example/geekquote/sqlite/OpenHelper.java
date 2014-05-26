package com.example.geekquote.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "geekquote.db";
	private static final int DATABASE_VERSION = 2;
	
	public static final String COL_ID = "id";
	public static final String NAME_COL_1 = "textQuote";
	public static final String NAME_COL_2 = "date";
	public static final String NAME_COL_3 = "rating";
	
	public static final String TABLE_NAME = "quotes";
	private static final String TABLE_CREATE =
	       "CREATE TABLE " + TABLE_NAME + " (" +
	       COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
	       NAME_COL_1+" TEXT NOT NULL,"+
	       NAME_COL_2+" VARCHAR(255),"+
	       NAME_COL_3+" INTEGER);";

	public OpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}

}
