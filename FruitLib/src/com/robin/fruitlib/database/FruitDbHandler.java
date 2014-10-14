package com.robin.fruitlib.database;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robin.fruitlib.database.FruitUserDatabase.AddressUrl;
import com.robin.fruitlib.database.FruitUserDatabase.FavShopUrl;

public class FruitDbHandler {
static FruitDbHandler handler;
	
	public static FruitDbHandler instance() {
		if(handler == null) {
			handler = new FruitDbHandler();
		}
		return handler;
	}
	
	/**
	 * 
	 * @param mDb
	 * @param data
	 * @return
	 */
	public boolean saveAddress(Context context, SQLiteDatabase mDb, String data) {
		try {
			ArrayList<String> list = getAddress(mDb);
			if(list.contains(data)) {
				return true;
			}
			if(list.size() >= 5) {
				list.remove(0);
			}
			ContentValues cv = new ContentValues();
			cv.put(AddressUrl.VALUE, data);
			mDb.insertOrThrow(FruitUserDatabase.Tables.ADDRESS_TB, null, cv);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param mDb
	 * @return
	 */
	public ArrayList<String> getAddress(SQLiteDatabase mDb) {
		Cursor cur = null;
		ArrayList<String> monitorList = new ArrayList<String>();
		try {
			cur = mDb.query(FruitUserDatabase.Tables.ADDRESS_TB, null, null, null, null, null, null);
			while (cur.moveToNext()) {
				try {
					String monitorBean = cur.getString(cur.getColumnIndex(AddressUrl.VALUE));
					monitorList.add(monitorBean);
				} catch (Exception e) {
					continue;
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
				cur = null;
			}
		}
		return monitorList;
	}
	
	/**
	 * 
	 * @param mDb
	 * @param data
	 * @return
	 */
	public boolean saveFavShop(Context context, SQLiteDatabase mDb, String data) {
		try {
			ArrayList<String> list = getFavShop(mDb);
			if(list.contains(data)) {
				return true;
			}
			ContentValues cv = new ContentValues();
			cv.put(FavShopUrl.VALUE, data);
			mDb.insertOrThrow(FruitUserDatabase.Tables.SHOP_TB, null, cv);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param mDb
	 * @return
	 */
	public ArrayList<String> getFavShop(SQLiteDatabase mDb) {
		Cursor cur = null;
		ArrayList<String> monitorList = new ArrayList<String>();
		try {
			cur = mDb.query(FruitUserDatabase.Tables.SHOP_TB, null, null, null, null, null, null);
			while (cur.moveToNext()) {
				try {
					String monitorBean = cur.getString(cur.getColumnIndex(FavShopUrl.VALUE));
					monitorList.add(monitorBean);
				} catch (Exception e) {
					continue;
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
				cur = null;
			}
		}
		return monitorList;
	}
	
	public void removeFavShopItem(SQLiteDatabase mDb, String data) {
		try {
			mDb.delete(FruitUserDatabase.Tables.SHOP_TB, FavShopUrl.VALUE + "=?", new String[]{data});
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
