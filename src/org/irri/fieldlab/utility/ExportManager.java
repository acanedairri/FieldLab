package org.irri.fieldlab.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.SyncFailedException;
import java.util.regex.Pattern;

import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.database.manager.ObservationManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.util.Log;
import android.widget.Toast;

public class ExportManager {

	private FieldLabManager fieldlabManager;
	private ObservationManager observationManager;
	int rowRef=7;
	private DescriptionManager descriptionManager;
	//	private static final Pattern numberPattern = Pattern.compile("-?\\d+"); 

	private static final Pattern numberPattern = Pattern.compile( "(-|\\+)?[0-9]+(\\.[0-9]+)?");


	public  ExportManager(SQLiteDatabase database){
		this.fieldlabManager =  new FieldLabManager(database);
		this.observationManager=fieldlabManager.getObservationManager();
		this.descriptionManager=fieldlabManager.getDescriptionManager();
	}


	public boolean exportToICISWorkbook(WritableWorkbook  workbook) throws SyncFailedException{

		try{
			Cursor cursor = this.observationManager.getAllData();
			String[] columnNames=cursor.getColumnNames();

			workbook.createSheet("Description", 0);
			workbook.createSheet("Observation", 1);
			WritableSheet descriptionSheet = workbook.getSheet(0);
			WritableSheet observationSheet = workbook.getSheet(1);

			createDescriptionData(descriptionSheet);
			createSpaceRow(descriptionSheet);

			createConditionFactorHeader(descriptionSheet);
			createConditionData(descriptionSheet);
			createSpaceRow(descriptionSheet);

			createFactorHeader(descriptionSheet);
			createFactorData(descriptionSheet);
			createSpaceRow(descriptionSheet);

			createConstantHeader(descriptionSheet);
			createConstantData(descriptionSheet);
			createSpaceRow(descriptionSheet);

			createVariateHeader(descriptionSheet);
			createVariateData(descriptionSheet);

			createObservationHeader(observationSheet, columnNames);
			createObservationData(observationSheet,cursor,columnNames);
			
			workbook.write();
			workbook.close();

			return true;

		}catch(SQLiteFullException e){
			return false;
		}catch (Exception e) {
			return false;
		}


	}

	private void createConstantData(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		Cursor cursor = descriptionManager.getConstant();

		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		int startRow=rowRef;
		for(int i=0;i<cursorCount;i++){
			for(int j=0;j<columnCount-7;j++){

				//				addCaption(descriptionSheet, j, i+startRow,cursor.getString(j+1),getDataFormat());
				String value=cursor.getString(j+1);

				if(!isNumber(value)){
					addCaption(descriptionSheet, j,i+startRow,value,getDataFormat());
				}else{
					if(value.contains(".")){
						addCaptionDouble(descriptionSheet, j,i+startRow,Double.valueOf(value));
					}else{
						addCaptionInteger(descriptionSheet, j, i+startRow,Integer.valueOf(value));
					}
				}
			}
			cursor.moveToNext();
			rowRef++;
		}
	}


	private void createConstantHeader(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		addCaption(descriptionSheet,0,rowRef,"CONSTANT",getVariateHeaderFormat());
		addCaption(descriptionSheet,1,rowRef,"DESCRIPTION",getVariateHeaderFormat());
		addCaption(descriptionSheet,2,rowRef,"PROPERTY",getVariateHeaderFormat());
		addCaption(descriptionSheet,3,rowRef,"SCALE",getVariateHeaderFormat());
		addCaption(descriptionSheet,4,rowRef,"METHOD",getVariateHeaderFormat());
		addCaption(descriptionSheet,5,rowRef,"DATA TYPE",getVariateHeaderFormat());
		addCaption(descriptionSheet,6,rowRef,"VALUE",getVariateHeaderFormat());
		addCaption(descriptionSheet,7,rowRef,"LABEL",getVariateHeaderFormat());

		rowRef++;

	}


	private void createVariateData(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		Cursor cursor = descriptionManager.getVariate();

		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		int startRow=rowRef;
		for(int i=0;i<cursorCount;i++){
			for(int j=0;j<columnCount-8;j++){
				addCaption(descriptionSheet, j, i+startRow,cursor.getString(j+1),getDataFormat());
			}
			cursor.moveToNext();
			rowRef++;
		}
	}

	private void createVariateHeader(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {
		addCaption(descriptionSheet,0,rowRef,"VARIATE",getVariateHeaderFormat());
		addCaption(descriptionSheet,1,rowRef,"DESCRIPTION",getVariateHeaderFormat());
		addCaption(descriptionSheet,2,rowRef,"PROPERTY",getVariateHeaderFormat());
		addCaption(descriptionSheet,3,rowRef,"SCALE",getVariateHeaderFormat());
		addCaption(descriptionSheet,4,rowRef,"METHOD",getVariateHeaderFormat());
		addCaption(descriptionSheet,5,rowRef,"DATA TYPE",getVariateHeaderFormat());
		addCaption(descriptionSheet,6,rowRef,"VALUE",getVariateHeaderFormat());
		addCaption(descriptionSheet,7,rowRef,"LABEL",getVariateHeaderFormat());

		rowRef++;

	}


	private void createSpaceRow(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {
		int startRow=rowRef;
		for(int i=0;i<8;i++){
			addCaption(descriptionSheet, i,startRow,"",getSpaceDescriptionFormat());
		}

		rowRef++;
	}


	private void createFactorData(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {
		Cursor cursor = descriptionManager.getFactor();

		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		int startRow=rowRef;
		for(int i=0;i<cursorCount;i++){
			for(int j=0;j<columnCount-8;j++){
				//				addCaption(descriptionSheet, j, i+startRow,cursor.getString(j+1),getDataFormat());

				String value=cursor.getString(j+1);

				if(!isNumber(value)){
					addCaption(descriptionSheet, j,i+startRow,value,getDataFormat());
				}else{
					if(value.contains(".")){
						addCaptionDouble(descriptionSheet, j,i+startRow,Double.valueOf(value));
					}else{
						addCaptionInteger(descriptionSheet, j, i+startRow,Integer.valueOf(value));
					}
				}

			}
			cursor.moveToNext();
			rowRef++;
		}

	}


	private void createFactorHeader(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		addCaption(descriptionSheet,0,rowRef,"FACTOR",getFactorHeaderFormat());
		addCaption(descriptionSheet,1,rowRef,"DESCRIPTION",getFactorHeaderFormat());
		addCaption(descriptionSheet,2,rowRef,"PROPERTY",getFactorHeaderFormat());
		addCaption(descriptionSheet,3,rowRef,"SCALE",getFactorHeaderFormat());
		addCaption(descriptionSheet,4,rowRef,"METHOD",getFactorHeaderFormat());
		addCaption(descriptionSheet,5,rowRef,"DATA TYPE",getFactorHeaderFormat());
		addCaption(descriptionSheet,6,rowRef,"VALUE",getFactorHeaderFormat());
		addCaption(descriptionSheet,7,rowRef,"LABEL",getFactorHeaderFormat());

		rowRef++;

	}


	private void createConditionData(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		Cursor cursor = descriptionManager.getCondition();

		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		int startRow=rowRef;
		for(int i=0;i<cursorCount;i++){
			for(int j=0;j<columnCount-8;j++){
				//				addCaption(descriptionSheet, j, i+startRow,cursor.getString(j+1),getDataFormat());
				String value=cursor.getString(j+1);

				if(!isNumber(value)){
					addCaption(descriptionSheet, j,i+startRow,value,getDataFormat());
				}else{
					if(value.contains(".")){
						addCaptionDouble(descriptionSheet, j,i+startRow,Double.valueOf(value));
					}else{
						addCaptionInteger(descriptionSheet, j, i+startRow,Integer.valueOf(value));
					}
				}

			}
			cursor.moveToNext();
			rowRef++;
		}
	}


	private void createConditionFactorHeader(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		addCaption(descriptionSheet,0,rowRef,"CONDITION",getFactorHeaderFormat());
		addCaption(descriptionSheet,1,rowRef,"DESCRIPTION",getFactorHeaderFormat());
		addCaption(descriptionSheet,2,rowRef,"PROPERTY",getFactorHeaderFormat());
		addCaption(descriptionSheet,3,rowRef,"SCALE",getFactorHeaderFormat());
		addCaption(descriptionSheet,4,rowRef,"METHOD",getFactorHeaderFormat());
		addCaption(descriptionSheet,5,rowRef,"DATA TYPE",getFactorHeaderFormat());
		addCaption(descriptionSheet,6,rowRef,"VALUE",getFactorHeaderFormat());
		addCaption(descriptionSheet,7,rowRef,"LABEL",getFactorHeaderFormat());

		rowRef++;
	}

	private void createDescriptionData(WritableSheet descriptionSheet) throws RowsExceededException, WriteException {

		//		addCaption(descriptionSheet,0,0,"PROJECT",getStudyDetailFormat());
		addCaption(descriptionSheet,0,0,"STUDY",getStudyDetailFormat());
		addCaption(descriptionSheet,0,1,"TITLE",getStudyDetailFormat());
		addCaption(descriptionSheet,0,2,"PMKEY",getStudyDetailFormat());
		addCaption(descriptionSheet,0,3,"OBJECTIVE",getStudyDetailFormat());
		addCaption(descriptionSheet,0,4,"START DATE",getStudyDetailFormat());
		addCaption(descriptionSheet,0,5,"END DATE",getStudyDetailFormat());

		//		addCaption(descriptionSheet,1,0,cursor.getString(1),getDataFormat());
		addCaption(descriptionSheet,1,0,descriptionManager.getStudyName(),getDataFormat());
		addCaption(descriptionSheet,1,1,descriptionManager.getStudyTitle(),getDataFormat());
		addCaption(descriptionSheet,1,2,descriptionManager.getStudyPMKey(),getDataFormat());
		addCaption(descriptionSheet,1,3,descriptionManager.getStudyObjective(),getDataFormat());
		addCaption(descriptionSheet,1,4,descriptionManager.getStudyStartDate(),getDataFormat());
		addCaption(descriptionSheet,1,5,descriptionManager.getStudyEndDate(),getDataFormat());

		String studyType=descriptionManager.getStudyType();
		if(!studyType.equals("")){
			addCaption(descriptionSheet,0,6,"STUDY TYPE",getStudyDetailFormat());
			addCaption(descriptionSheet,1,6,descriptionManager.getStudyType(),getDataFormat());
		}else{
			rowRef--;
		}

	}
	public static boolean isNumber(String string) {
		return string != null && numberPattern.matcher(string).matches();
	}

	private void createObservationData(WritableSheet observationSheet, Cursor cursor,String[] columnNames) throws RowsExceededException, WriteException {
		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		
		
		for(int i=0;i<cursorCount;i++){
			for(int j=0;j<columnCount-1;j++){

				String value=cursor.getString(j+1);

				if(!isNumber(value)){
					addCaption(observationSheet, j, i+1,value,getDataFormat());
				}else{
					if(value.contains(".")){
						addCaptionDouble(observationSheet, j, i+1,Double.valueOf(value));
					}else{
						addCaptionInteger(observationSheet, j, i+1,Integer.valueOf(value));
					}
				}
			}
			cursor.moveToNext();
		}
	}


	private void createObservationHeader(WritableSheet sheet,String[] columnNames) throws WriteException {

		int columnIndex=1;

		int factorCount=descriptionManager.getFactor().getCount();

		for(int i=0;i<columnNames.length-1;i++){

			if(i< factorCount){
				addCaption(sheet, i, 0, columnNames[i+1],getFactorHeaderFormat());
			}else{
				addCaption(sheet, i, 0, columnNames[i+1],getVariateHeaderFormat());
			}
			columnIndex++;

		}

		addCaption(sheet, columnNames.length , 0, "ROWTAG",getRowTagHeaderFormat());

	}

	private void addCaption(WritableSheet sheet, int column, int row, String s, WritableCellFormat format)
	throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s,format);
		sheet.addCell(label);
		Log.d("CellValue", label.getString());
	}


	private void addCaptionDouble(WritableSheet sheet, int column, int row, double value)
	throws RowsExceededException, WriteException {
		WritableCellFormat floatFormat = new WritableCellFormat (NumberFormats.DEFAULT); 
		Number n= new Number(column, row, value,floatFormat);
		sheet.addCell(n);
		Log.d("CellValue", String.valueOf(n.getValue()));
	}

	private void addCaptionInteger(WritableSheet sheet, int column, int row, int value)
	throws RowsExceededException, WriteException {
		WritableCellFormat integerFormat = new WritableCellFormat (NumberFormats.INTEGER);
		Number n = new Number(column, row, value, integerFormat); 
		sheet.addCell(n);
		Log.d("CellValue", String.valueOf(n.getValue()));
	}

	private WritableCellFormat getFactorHeaderFormat() throws WriteException{
		WritableFont format = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		WritableCellFormat cellFormat = new WritableCellFormat(); //table cell format
		cellFormat.setBackground(Colour.DARK_GREEN) ; //Table background
		//	     tableFormatBackground.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK); //table border style
		cellFormat.setFont(format); //set the font
		cellFormat.setAlignment(Alignment.LEFT);// set alignment left

		return cellFormat;

	}

	private WritableCellFormat getStudyDetailFormat() throws WriteException{
		WritableFont format = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		WritableCellFormat cellFormat = new WritableCellFormat(); //table cell format

		cellFormat.setBackground(Colour.DARK_RED) ; //Table background
		//	     tableFormatBackground.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK); //table border style
		cellFormat.setFont(format); //set the font
		cellFormat.setAlignment(Alignment.LEFT);// set alignment left

		return cellFormat;

	}

	private WritableCellFormat getDataFormat() throws WriteException{
		WritableFont format = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
		WritableCellFormat cellFormat = new WritableCellFormat(); //table cell format
		cellFormat.setFont(format); //set the font
		cellFormat.setAlignment(Alignment.LEFT);// set alignment left

		return cellFormat;

	}

	private WritableCellFormat getVariateHeaderFormat() throws WriteException{
		WritableFont format = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		WritableCellFormat cellFormat = new WritableCellFormat(); //table cell format
		cellFormat.setBackground(Colour.DARK_BLUE) ; //Table background
		//	     tableFormatBackground.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK); //table border style
		cellFormat.setFont(format); //set the font
		cellFormat.setAlignment(Alignment.LEFT);// set alignment left

		return cellFormat;

	}

	private WritableCellFormat getRowTagHeaderFormat() throws WriteException{
		WritableFont format = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		WritableCellFormat cellFormat = new WritableCellFormat(); //table cell format
		//		cellFormat.setBackground(Color.rgb(150, 48, 0))
		//		int c = new Color().rgb(153, 51, 0);
		cellFormat.setBackground(Colour.DARK_RED) ;//Table background
		//	     tableFormatBackground.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK); //table border style
		cellFormat.setFont(format); //set the font
		cellFormat.setAlignment(Alignment.LEFT);// set alignment left

		return cellFormat;

	}

	private WritableCellFormat getSpaceDescriptionFormat() throws WriteException{
		WritableFont format = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		WritableCellFormat cellFormat = new WritableCellFormat(); //table cell format
		cellFormat.setBackground(Colour.GRAY_25) ; //Table background
		//	     tableFormatBackground.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK); //table border style
		cellFormat.setFont(format); //set the font
		cellFormat.setAlignment(Alignment.LEFT);// set alignment left

		return cellFormat;

	}


	public boolean exportAllObservationDataToCSV(String databaseName) throws IOException {


		Cursor cursor = this.observationManager.getAllData();
		String[] columnNames=cursor.getColumnNames();

		File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+"_observation"+".csv");
		// ##### Write a file to the external sd card #####
		FileOutputStream fOut = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fOut);

		String line="";
		for(int i=0;i<columnNames.length-1;i++){
			line+=columnNames[i+1]+",";
		}
		line=removeLastComma(line);
		osw.write(line+"\n");

		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		for(int i=0;i<cursorCount;i++){
			line="";
			for(int j=0;j<columnCount-1;j++){
				line+=cursor.getString(j+1)+",";
			}
			cursor.moveToNext();
			line=removeLastComma(line);
			osw.write(line+"\n");
		}

		osw.flush();
		osw.close();
		return true;
	}


	public boolean exportSpecificObservationDataCSV(String databaseName,String variateList, String barcodeRef) throws IOException {

		Cursor cursor = this.observationManager.getSpecificObservationData(variateList,barcodeRef);
		Log.d("ExportManager", String.valueOf(cursor.getCount()));
		String[] columnNames=cursor.getColumnNames();

		File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+"_by_trait"+".csv");
		// ##### Write a file to the external sd card #####
		FileOutputStream fOut = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fOut);

		String line="";
		for(int i=0;i<columnNames.length;i++){
			line+=columnNames[i]+",";
		}
		line=removeLastComma(line);
		osw.write(line+"\n");
		Log.d("ObservationColumn", line+"\n");

		int columnCount=cursor.getColumnCount();
		int cursorCount=cursor.getCount();

		for(int i=0;i<cursorCount;i++){
			line="";
			for(int j=0;j<columnCount;j++){
				line+=cursor.getString(j)+",";
			}
			line=removeLastComma(line);
			Log.d("ObservationData", line+"\n");
			cursor.moveToNext();
			osw.write(line+"\n");
		}

		osw.flush();
		osw.close();
		return true;
	}

	private String removeLastComma(String line){

		return line.substring(0, line.length()-1);
	}
}
