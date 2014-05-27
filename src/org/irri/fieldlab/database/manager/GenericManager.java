package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GenericManager {
	
	private SQLiteDatabase database;
	
	public GenericManager(SQLiteDatabase database ){
		this.database=database;
	}



	public boolean Exists(long _id, String tableName) {
		Cursor cursor = database.rawQuery("select * from " + tableName
				+ " where _id=%s", new String[] { String.valueOf(_id) });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}

	public boolean Exists(long _id, String tableName, String columnName) {
		Cursor cursor = database.rawQuery("select * from " + tableName
				+ " where "+ columnName +"=%s", new String[] { String.valueOf(_id) });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
}
