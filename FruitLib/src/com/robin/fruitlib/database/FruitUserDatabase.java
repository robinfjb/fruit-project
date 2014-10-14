package com.robin.fruitlib.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FruitUserDatabase extends SQLiteOpenHelper{
	private static final String TAG = "FruitUserDatabase";
	private static final int DATABASE_VERSION = 1;

	interface Tables {
		String ADDRESS_TB = "address";
		String SHOP_TB = "fav_shop";
	}
	
	public interface AddressUrl {
		final String ID = "id";
		final String VALUE = "value";
		final String CREAT = 
				"CREATE TABLE IF NOT EXISTS " + Tables.ADDRESS_TB + " (" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ VALUE + " TEXT,"
				+ "UNIQUE (" + ID + ") ON CONFLICT REPLACE)";
		final int SIZE = 500;
	}
	
	public interface FavShopUrl {
		final String ID = "id";
		final String VALUE = "value";
		final String CREAT = 
				"CREATE TABLE IF NOT EXISTS " + Tables.SHOP_TB + " (" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ VALUE + " TEXT,"
				+ "UNIQUE (" + ID + ") ON CONFLICT REPLACE)";
		final int SIZE = 500;
	}
	
	public FruitUserDatabase(Context context, String name,CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public FruitUserDatabase(Context context, String dbName) {
		super(context, dbName, null, DATABASE_VERSION);
	}
	
	public FruitUserDatabase(Context context) {
		super(context, "fruituser", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(AddressUrl.CREAT);
		db.execSQL(FavShopUrl.CREAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		int version = oldVersion;
		if (version != DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + Tables.ADDRESS_TB);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SHOP_TB);
			onCreate(db);
		}	
	}

}
