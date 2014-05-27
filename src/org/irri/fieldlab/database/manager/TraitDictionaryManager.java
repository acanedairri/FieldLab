package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TraitDictionaryManager {
	
	private SQLiteDatabase database;
	
	public TraitDictionaryManager(SQLiteDatabase database ){
		this.database=database;
	}

	
//	public boolean deleteTrait() {
//		return database.delete(TableICIS.DICTIONARY_TRAIT_TABLE, null, null) > 0;
//	}
//	
//	public void insertTraitData(ContentValues cvalues) {
//		database.insert(TableICIS.DICTIONARY_TRAIT_TABLE, null, cvalues);
//	}
//
//
//	public Cursor getAllTrait() {
//		// TODO Auto-generated method stub
//		String query="SELECT * FROM " + TableICIS.DICTIONARY_TRAIT_TABLE;
//		Cursor cursor = database.rawQuery(query, null);
//
//		if (cursor != null) {
//			cursor.moveToFirst();
//		}
//		return cursor;
//	}
	
	
}
