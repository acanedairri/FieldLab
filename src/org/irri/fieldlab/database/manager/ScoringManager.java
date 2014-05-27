package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ScoringManager {

	private SQLiteDatabase database;

	public ScoringManager(SQLiteDatabase database ){
		this.database=database;
	}

	public Cursor getScoringFilterByTraitcode(String traitcode){

		String query="SELECT * FROM " + TableICIS.SCORING_TABLE;
		query+=" WHERE ";
		query+=TableICIS.SCORING_VARIATECODE + " ='" + traitcode +"'";

		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void insertScoring(ContentValues cvalues) {
		database.insert(TableICIS.SCORING_TABLE, null, cvalues);
	}

	public boolean deleteScoring() {
		return database.delete(TableICIS.SCORING_TABLE,null, null) > 0;
	}

	public Cursor getAllScoring(){

		String query="SELECT * FROM " + TableICIS.SCORING_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public boolean isValidScore(String editTextValue, String traitcode) {
		String query="SELECT * FROM " + TableICIS.SCORING_TABLE + " where variatecode='"+traitcode+"' and score='"+editTextValue+"'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return true;
		}else{
			return false;
		}
		
	}

	public boolean hasScoring(String traitcode) {
		
		String query="SELECT * FROM " + TableICIS.SCORING_TABLE + " where variatecode='"+traitcode+"'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return true;
		}else{
			return false;
		}
		
	}

	public Cursor getScores(String traitcode) {
		String query="SELECT score,description,filename,folder FROM " + TableICIS.SCORING_TABLE;
		query+=" WHERE ";
		query+=TableICIS.SCORING_VARIATECODE + " ='" + traitcode +"'";

		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


}
