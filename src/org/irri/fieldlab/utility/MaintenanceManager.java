package org.irri.fieldlab.utility;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.irri.fieldlab.database.manager.RemarksManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MaintenanceManager {


	private SQLiteDatabase database;

	public MaintenanceManager(SQLiteDatabase database){
		this.database=database;
	}
	
	public MaintenanceManager(){
	}

	/* Open xls file
	 * @param: fileName - name of the xls file
	 * @return: intent
	 */
	public Intent getIntent(String fileName){
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.ms-excel");
		return intent;
	}

	
	public Intent getIntentPowerpoint(String fileName){
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.ms-powerpoint");
		return intent;
	}
	
	public void loadScoring(Sheet sheet){

		String variatecode = "";
		String score = "";
		String description = "";
		String filename = "";
		String folder = "";

		ScoringManager scoringManager=new ScoringManager(database);
		scoringManager.deleteScoring();
		for (int i = 0; i < sheet.getRows(); i++) {

			variatecode = sheet.getCell(0, i).getContents();
			score = sheet.getCell(1, i).getContents();
			description = sheet.getCell(2, i).getContents();
			filename = sheet.getCell(3, i).getContents();
			folder = sheet.getCell(4, i).getContents();

			ContentValues content = new ContentValues();
			content.put(TableICIS.SCORING_VARIATECODE, variatecode);
			content.put(TableICIS.SCORING_SCORE, score);
			content.put(TableICIS.SCORING_DESCRIPTION, description);
			content.put(TableICIS.SCORING_FILENAME, filename);
			content.put(TableICIS.SCORING_FOLDER, folder);

			scoringManager.insertScoring(content);
		}
	}

	public void loadRemarks(Sheet sheet){

		String variatecode;
		String text;

		RemarksManager remarksManager=  new RemarksManager(database);
		remarksManager.deleteRemarks();
		for (int i = 0; i < sheet.getRows(); i++) {
			variatecode = sheet.getCell(0, i).getContents();
			text = sheet.getCell(1, i).getContents();

			ContentValues content = new ContentValues();
			content.put(TableICIS.REMARKS_VARIATECODE, variatecode);
			content.put(TableICIS.REMARKS_TEXT, text);
			remarksManager.insertRemarksData(content);
		}
	}
/*
	public void loadTrait(Sheet sheet){

		String traitcode;
		String description;
		String property;
		String scale;
		String method;
		String datatype;
		String minimumValue;
		String maximumValue;

		TraitDictionaryManager traitManager=  new TraitDictionaryManager(database);
		traitManager.deleteTrait();
		for (int i = 0; i < sheet.getRows(); i++) {
			traitcode = sheet.getCell(0, i).getContents();
			description = sheet.getCell(1, i).getContents();
			property = sheet.getCell(2, i).getContents();
			scale = sheet.getCell(3, i).getContents();
			method = sheet.getCell(4, i).getContents();
			datatype = sheet.getCell(5, i).getContents();
			minimumValue = sheet.getCell(6, i).getContents();
			maximumValue = sheet.getCell(7, i).getContents();

			ContentValues content = new ContentValues();
			content.put(TableICIS.DICTIONARY_TRAIT_TRAITCODE, traitcode);
			content.put(TableICIS.DICTIONARY_TRAIT_DESCRIPTION, description);
			content.put(TableICIS.DICTIONARY_TRAIT_PROPERTY, property);
			content.put(TableICIS.DICTIONARY_TRAIT_SCALE, scale);
			content.put(TableICIS.DICTIONARY_TRAIT_METHOD, method);
			content.put(TableICIS.DICTIONARY_TRAIT_DATATYPE, datatype);
			content.put(TableICIS.DICTIONARY_TRAIT_MINIMUMVALUE, minimumValue);
			content.put(TableICIS.DICTIONARY_TRAIT_MAXIMUMVALUE, maximumValue);

			traitManager.insertTraitData(content);
		}
	}
*/
	public void loadMaintenanceData(String fileNamePath,int option){

		File inputWorkbook = new File(fileNamePath);
		Workbook w;
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			if(option==0){
				loadScoring(sheet);
				//viewScoring();

			}else if (option==1){
				loadRemarks(sheet);
				//viewRemarks();
			}else{
				//loadTrait(sheet);
				//viewTrait();
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}




