package org.irri.fieldlab.database.manager;

import java.util.ArrayList;

import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.TextView;

public class ObservationManager {

	private SQLiteDatabase database;

	public ObservationManager(SQLiteDatabase database ){
		this.database=database;
	}

	public void createObservationTable(){
		database.execSQL("CREATE TABLE observation(_id INTEGER PRIMARY KEY)");
	}

	public void alterTableObservation(String field){
		try{
			database.execSQL("ALTER TABLE observation ADD COLUMN "+field+" TEXT");
		}catch(SQLException e){
			database.execSQL("ALTER TABLE observation ADD COLUMN "+field+"_ TEXT");

		}
	}
	
	public void alterTableObservationDeleteColumn(String field){
		try{
			database.execSQL("ALTER TABLE observation DELETE COLUMN "+field+" TEXT");
		}catch(SQLException e){
			database.execSQL("ALTER TABLE observation DELETE COLUMN "+field+"_ TEXT");

		}
	}

	public void insertObservationData(String field, String data)throws SQLiteException {
		// TODO Auto-generated method stub
		try{
			String query="INSERT INTO observation("+field+") values ("+data+")";
			database.execSQL(query);

		}catch(SQLException e){

		}


	}


	
	public Cursor getFactorValue(String field,int id) {

		String query="SELECT "+field+" FROM " + TableICIS.OBSERVATION_TABLE + " where _id="+id;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}

	//	public void insertObservationColumnHeader(String cellValue) {
	//		// TODO Auto-generated method stub
	//			String query;
	//			boolean isFactor=false;
	//			Cursor cursor = database.rawQuery("select * from factor where traitcode='"+cellValue+"'",null );
	//			isFactor = (cursor.getCount() > 0);
	//			if(isFactor){
	//				query="INSERT INTO columnheader(header,type) values('"+cellValue+"','label')";
	//			}else{
	//				query="INSERT INTO columnheader(header,type) values('"+cellValue+"','textfield')";
	//			}
	//				
	//			database.execSQL(query);
	//	}

	public Cursor getAllObservationData(String fieldObservation,ArrayList<String> observationVariateActive,int recId,String mdv){
		String query="";
		if(mdv.equals("N")){
			query="SELECT _id,"+fieldObservation+" FROM " + TableICIS.OBSERVATION_TABLE +" where _id="+recId;
		}else{


			StringBuilder s= new StringBuilder();
			
			int obsSize=observationVariateActive.size();
			int counter=0;
			
			if(obsSize > 1){
				for(String s1:observationVariateActive){
					if(counter < obsSize-1)
						s.append("`"+s1+"`"+"=\"\" or ");
					else
						s.append("`"+s1+"`"+"=\"\"");
					counter++;
				}
				Log.d("obs",s.toString());
				
				query="SELECT _id,"+fieldObservation+" FROM " + TableICIS.OBSERVATION_TABLE +" where _id="+recId +" and "+"("+s.toString()+")";
				
			}else{
				query="SELECT _id,"+fieldObservation+" FROM " + TableICIS.OBSERVATION_TABLE +" where _id="+recId +" and "+observationVariateActive.iterator().next()+"=\"\"";
			}
		}

		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor getAllObservationDataAll(String fieldObservation){
		String query="SELECT _id,"+fieldObservation+" FROM " + TableICIS.OBSERVATION_TABLE ;
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor countAllObservationRecord() {
		// TODO Auto-generated method stub
		String query="SELECT count(*) FROM observation";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void updateRecord(int recId, String field, String value) {
		ContentValues args= new ContentValues();
		args.put("`"+field+"`",value);
		database.update(TableICIS.OBSERVATION_TABLE, args, TableICIS.DESCRIPTION_ID+"="+recId, null);
	}

	public int searchRecordByField(String field, String value) {

		try{
			String query="SELECT _id FROM " + TableICIS.OBSERVATION_TABLE +" where "+field+" like '%"+value+"%'";
			Cursor cursor = database.rawQuery(query, null);
		
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getInt(0);
			}else{
				return 0;
			}
		}catch(SQLiteException e){
			return 0;
		}
	}
	
	public String getSearchRecordByField(String field, String value) {

		try{
			String query="";
			if(field.toLowerCase().contains("barcode")){
				query="SELECT _id FROM " + TableICIS.OBSERVATION_TABLE +" where "+field+" like '%"+value+"%'";
			}else{
				query="SELECT "+field+" FROM " + TableICIS.OBSERVATION_TABLE +" where "+field+" like '%"+value+"%'";
			}
			Cursor cursor = database.rawQuery(query, null);
		
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getString(0);
			}else{
				return cursor.getString(0);
			}
		}catch(SQLiteException e){
			return "";
		}
	}
	
	
	

	public String getReferenceCode(String field, String value) {

		try{
			String query="SELECT " +field+" FROM " + TableICIS.OBSERVATION_TABLE +" where _id="+value;
			Cursor cursor = database.rawQuery(query, null);
		
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getString(0);
			}else{
				return "";
			}
		}catch(SQLiteException e){
			return "";
		}
	}
	

	public Cursor getAllData() {
		String query="SELECT * FROM observation";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getSpecificObservationData(String variateList, String barcodeRef) {
		try{
			
			String[] variate=variateList.split(",");
			String criteria="";
			for(int i=0;i< variate.length;i++){
				if(i < variate.length-1){
					criteria+=variate[i]+" <> '' OR ";
				}else{
					criteria+=variate[i]+" <> ''";
				}
			}
			
			String query="SELECT _id,"+barcodeRef+","+variateList+ " FROM " + TableICIS.OBSERVATION_TABLE +" where "+criteria ;
			System.out.println(query);
			
			Cursor cursor = database.rawQuery(query, null);
		
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor;
			}
		}catch(SQLiteException e){
		}
		return null;
	}

	public void saveRangeInput(int recId, String field, String value) {
		// TODO Auto-generated method stub
		ContentValues args= new ContentValues();
		args.put(field,value);
		database.update(TableICIS.OBSERVATION_TABLE, args, TableICIS.OBSERVATION_ID+"="+recId, null);
	}

	public void updateImportData(String data, String header, String id) {
		ContentValues args= new ContentValues();
		args.put(header,data);
		database.update(TableICIS.OBSERVATION_TABLE, args, TableICIS.OBSERVATION_ID+"="+Integer.valueOf(id), null);
		
	}

	public String getReferenceRow(int recId, String barcodeReference) {
		try{
			String query="SELECT " +barcodeReference+" FROM " + TableICIS.OBSERVATION_TABLE +" where _id="+recId;
			Cursor cursor = database.rawQuery(query, null);
		
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getString(0);
			}else{
				return "";
			}
		}catch(SQLiteException e){
			return "";
		}
		
	}


}
