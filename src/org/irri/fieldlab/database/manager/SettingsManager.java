package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsManager {

	private SQLiteDatabase database;

	public SettingsManager(SQLiteDatabase database ){
		this.database=database;
	}

	public Cursor getSettings(){

		String query="SELECT * FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void updateSettings(String barcodeReference,int dataentryview,int pagereccount,String autoPrompt,String missingDataValue,int rcbdOder) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.SETTINGS_BARCODE,barcodeReference);
		args.put(TableICIS.SETTINGS_DATAENTRYVIEW,dataentryview);
		args.put(TableICIS.SETTINGS_PAGERECCOUNT,pagereccount);
		args.put(TableICIS.SETTINGS_AUTOSCORE,autoPrompt);
		args.put(TableICIS.SETTINGS_MDV,missingDataValue);
		args.put(TableICIS.SETTINGS_RCDB_ORDER,rcbdOder);
		database.update(TableICIS.SETTINGS_TABLE, args, TableICIS.SETTINGS_ID+"="+1, null);
	}

	public void updateLastRecord(int lastrec) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.SETTINGS_LASTREC,lastrec);
		database.update(TableICIS.SETTINGS_TABLE, args, TableICIS.SETTINGS_ID+"="+1, null);
	}
	
	public int getLastRecord(){

		String query="SELECT lastrec FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getInt(0);
	}

	public int getTotalRecordPerPage() {
		String query="SELECT pagereccount FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getInt(0);
	}
	
	public boolean autoPromptScoring(){
		String query="SELECT autoscore FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
			if(cursor.getString(0).equals("Y")){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	public String getBarcodeReference() {
		// TODO Auto-generated method stub
		String query="SELECT barcode FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		return cursor.getString(0);
	}
	
	public int getRCBDoption() {
		// TODO Auto-generated method stub
		String query="SELECT rcbdorder FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getInt(0);
		}
		return cursor.getInt(0);
	}
	
	public String getMDV() {
		// TODO Auto-generated method stub
		String query="SELECT mdv FROM " + TableICIS.SETTINGS_TABLE;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		return cursor.getString(0);
	}
	
}
