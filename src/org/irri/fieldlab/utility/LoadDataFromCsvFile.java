package org.irri.fieldlab.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.database.manager.RemarksManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class LoadDataFromCsvFile {

	public LoadDataFromCsvFile(){

	}

	public void loadVariateScoring(String fileName,SQLiteDatabase database) throws IOException{

		// open the file for reading
		File file = new File(fileName);
		InputStream instream = new FileInputStream(file);

		// if file the available for reading
		if (instream != null) {
			// prepare the file for reading
			InputStreamReader inputreader = new InputStreamReader(
					instream);
			BufferedReader buffreader = new BufferedReader(
					inputreader);

			String line = "";
			String variatecode = "";
			String score = "";
			String description = "";

			ScoringManager scoringManager=new ScoringManager(database);
			while ((line = buffreader.readLine()) != null) {

				String[] rowData = line.split(",");
				variatecode = rowData[0];
				score = rowData[1];
				description = rowData[2];

				ContentValues content = new ContentValues();
				content.put(TableICIS.SCORING_VARIATECODE, variatecode);
				content.put(TableICIS.SCORING_SCORE, score);
				content.put(TableICIS.SCORING_DESCRIPTION, description);

				scoringManager.insertScoring(content);
			}
		}
		instream.close();
	}
	
	

	public void loadRemarkScoring(String fileName,SQLiteDatabase database) throws IOException{

		// open the file for reading
		File file = new File(fileName);
		InputStream instream = new FileInputStream(file);

		// if file the available for reading
		if (instream != null) {
			// prepare the file for reading
			InputStreamReader inputreader = new InputStreamReader(
					instream);
			BufferedReader buffreader = new BufferedReader(
					inputreader);

			String line = "";
			String variatecode = "";
			String text = "";
			RemarksManager remarksManager=  new RemarksManager(database);
			remarksManager.deleteRemarks();
			while ((line = buffreader.readLine()) != null) {
				String[] rowData = line.split(",");
				variatecode = rowData[0];
				text = rowData[1];

				ContentValues content = new ContentValues();
				content.put(TableICIS.REMARKS_VARIATECODE, variatecode);
				content.put(TableICIS.REMARKS_TEXT, text);
				remarksManager.insertRemarksData(content);
			}
			instream.close();
		}
	}
	
	public boolean loadObservationData(String fileName,FieldLabManager fieldlabManager) throws IOException{

		// open the file for reading
		File file = new File(fileName);
		InputStream instream = new FileInputStream(file);
//		
		String line = "";
//		// if file the available for reading
		if (instream != null) {
			// prepare the file for reading
			InputStreamReader inputreader = new InputStreamReader(
					instream);
			BufferedReader buffreader = new BufferedReader(
					inputreader);
			
			String[] header=buffreader.readLine().split(",");
			
			
			while ((line = buffreader.readLine()) != null) {
				String[] data=line.split(",");
					for(int i=2;i < data.length;i++){
						fieldlabManager.getObservationManager().updateImportData(data[i],header[i],data[0]);
						
					}
			}
		}
		instream.close();
		return true;
	}

}
