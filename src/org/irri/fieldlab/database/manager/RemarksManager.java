package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RemarksManager {
	
	private SQLiteDatabase database;
	
	public RemarksManager(SQLiteDatabase database ){
		this.database=database;
	}

	public void insertRemarksData(ContentValues cvalues) {
		database.insert(TableICIS.REMARKS_TABLE, null, cvalues);
	}
	
	public Cursor getScoringRemarks(String variatecode) {
		
		String query="SELECT * FROM " +  TableICIS.REMARKS_TABLE;
		query+=" WHERE ";
		query+=TableICIS.REMARKS_VARIATECODE + "='"+ variatecode +"'";
		
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}
	
	public boolean deleteRemarks() {
		return database.delete(TableICIS.REMARKS_TABLE, null, null) > 0;
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
