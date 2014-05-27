package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ImageManager {
	
	private SQLiteDatabase database;
	
	public ImageManager(SQLiteDatabase database ){
		this.database=database;
	}

	public void insertRemarksData(ContentValues cvalues) {
		database.insert(TableICIS.IMAGE_TABLE, null, cvalues);
	}
	
	public Cursor getImage(int obsid) {
		
		String query="SELECT * FROM " +  TableICIS.IMAGE_TABLE;
		query+=" WHERE ";
		query+=TableICIS.IMAGE_OBSID + "='"+ obsid +"'";
		
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}
	
	public boolean deleteImageRef() {
		return database.delete(TableICIS.IMAGE_TABLE, null, null) > 0;
	}

	public Cursor getAllRemarks() {
		// TODO Auto-generated method stub
		String query="SELECT * FROM " + TableICIS.REMARKS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
}
