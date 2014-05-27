package org.irri.fieldlab.database.manager;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DescriptionManager {

	private static SQLiteDatabase database;

	public DescriptionManager(SQLiteDatabase database ){
		this.database=database;
	}

	// Study MetaData

	public void insertDescription(ContentValues cvalues) {
		database.insert(TableICIS.DESCRIPTION_TABLE, null, cvalues);
	}


	public Cursor getStudyDescription() {

		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='description'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}



	public String getStudyName() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='STUDY_NAME'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getString(0);
	}

	public String getStudyTitle() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='TITLE'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getString(0);
	}

	public String getStudyObjective() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='OBJECTIVE'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getString(0);
	}

	public String getStudyPMKey() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='PM_KEY'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getString(0);
	}

	public String getStudyStartDate() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='START_DATE'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		return "";
	}


	public String getStudyEndDate() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='END_DATE'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getString(0);
	}

	public String getStudyType() {
		// TODO Auto-generated method stub
		String query="SELECT value FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='STUDY_TYPE'" ;
		Cursor cursor = database.rawQuery(query, null);
		Log.d("QueryStudyTyype",query);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}else{
			return "";
		}
	}

	// Condition
	public Cursor getCondition() {
		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='condition'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void insertCondition(ContentValues contentCondition) throws SQLiteException{
		database.insert(TableICIS.DESCRIPTION_TABLE, null, contentCondition);
	}

	// Constant

	public Cursor getConstant() {
		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='constant'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getFactor() {
		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='factor'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getVariate() {
		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='variate'" ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	public Cursor getFactorVariate() {
		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='variate' or category='factor'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getActiveFactor() {
		String query="SELECT * FROM " + TableICIS.DESCRIPTION_TABLE +" where category='factor' and displayregion='R1' and visible='Y'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getObservationColumnActive() {

		String query="SELECT traitcode,datatype,inputtype FROM " + TableICIS.DESCRIPTION_TABLE  + " where visible ='Y' and displayregion='R2'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}

	public Cursor getObservationSingleView() {

		String query="SELECT traitcode,datatype,inputtype FROM " + TableICIS.DESCRIPTION_TABLE  + " where visible ='Y'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}



	public static Cursor getVariateRangeValue(String traitcode) {
		String query="SELECT minimumvalue,maximumvalue FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='"+traitcode+"'";
		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	public boolean updateVariateMinimumValue(long id, Double minimum) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.DESCRIPTION_MINIMUMVALUE,minimum);
		return database.update(TableICIS.DESCRIPTION_TABLE, args, TableICIS.DESCRIPTION_ID+"="+id, null) > 0;

	}

	public boolean updateVariateMaximumValue(long id, Double maximum) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.DESCRIPTION_MAXIMUMVALUE,maximum);
		return database.update(TableICIS.DESCRIPTION_TABLE, args, TableICIS.DESCRIPTION_ID+"="+id, null) > 0;
	}

	public boolean updateDescriptionVisible(long id) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.DESCRIPTION_VISIBLE,"Y");
		return database.update(TableICIS.DESCRIPTION_TABLE, args, TableICIS.DESCRIPTION_ID+"="+id, null) > 0;

	}

	public boolean updateDescriptionInVisible(long id) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.DESCRIPTION_VISIBLE,"N");
		return database.update(TableICIS.DESCRIPTION_TABLE, args, TableICIS.DESCRIPTION_ID+"="+id, null) > 0;

	}


	public boolean updateVariateDatatype(long id, String datatype) {
		ContentValues args= new ContentValues();
		args.put(TableICIS.DESCRIPTION_DATATYPE,datatype);
		return database.update(TableICIS.DESCRIPTION_TABLE, args, TableICIS.DESCRIPTION_ID+"="+id, null) > 0;

	}

	public Cursor getObservationVariateActive() {
		String query="SELECT traitcode FROM " + TableICIS.DESCRIPTION_TABLE +" where category='variate' and visible='Y'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	public boolean deleteAddedVariate(long id) {
		return database.delete(TableICIS.DESCRIPTION_TABLE, TableICIS.DESCRIPTION_ID+"="+id, null) > 0;
	}

	public String getDescription(String traitcode) {
		String query="SELECT description FROM " + TableICIS.DESCRIPTION_TABLE +" where traitcode='"+traitcode+"'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		return "";
	}

	public  int getVisibleCount() {
		String query="SELECT traitcode FROM " + TableICIS.DESCRIPTION_TABLE +" where category='variate' and visible='Y'";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getCount();
		
	}


}
