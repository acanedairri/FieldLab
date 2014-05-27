/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.irri.fieldlab.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.SyncFailedException;

import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.utility.DatabaseConnectionInstance;
import org.irri.fieldlab.utility.FieldLabPath;
import org.irri.fieldlab.utility.FileManager;
import org.irri.fieldlab.utility.ImportWorkbookManager;
import org.irri.fieldlab.utility.MaintenanceManager;
import org.irri.fieldlab.utility.XLSManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteFullException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class ImportWorkbookActivity extends Activity {

	private static final int ACTIVITY_BROWSE = 0;
	private String fileName="";
	private Button btnBrowse;
	private Button btnClose;
	private EditText txtFilePath;
	private Spinner spinnerImportAction;
	private DatabaseConnect databaseConnect;
	private MaintenanceManager xlsFileManager;
	private Workbook workbook;
	private String studyFileName;
	private Button btnViewWorkbook;
	private MaintenanceManager maintenanceManager;
	private DatabaseConnectionInstance databaseConnection;
	private View btnImportWookbook;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.importworkbook);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		spinnerImportAction = (Spinner) findViewById(R.id.spinnerImportAction);
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//				this, R.array.import_choices,
//				android.R.layout.simple_spinner_item);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinnerImportAction.setAdapter(adapter);

		btnBrowse= (Button) findViewById(R.id.btnBrowse);
		btnBrowse.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent(ImportWorkbookActivity.this, FileChooserActivity.class);
				i.putExtra("folder", "workbook");
				startActivityForResult(i, ACTIVITY_BROWSE);
			}
		});

		txtFilePath = (EditText) findViewById(R.id.txtFilePath);

		btnViewWorkbook= (Button) findViewById(R.id.btnViewWorkbook);

		btnViewWorkbook.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				openXlsFile();

			}
		});

		btnImportWookbook = (Button) findViewById(R.id.btnImportWorkbook);

		btnImportWookbook.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click

				final String fileNamePath=txtFilePath.getText().toString();
				if (fileNamePath != null
						&& !fileNamePath.equals("")
						&& !fileNamePath.contains(
						"Type the complete path here.")
						&& (fileNamePath.endsWith(".xls") || fileNamePath
								.endsWith(".XLS"))) {


					int choice = 0;//spinnerImportAction.getSelectedItemPosition();
					if(choice==0){
						loadDataFromICISWorkbook(fileNamePath);
					}else if(choice==1){
						//loadData(fileNamePath,1);

					}else{
						//loadData(fileNamePath,2);
					}


				} else {
					Toast.makeText(ImportWorkbookActivity.this,
							"Please enter a valid file!", Toast.LENGTH_LONG)
							.show();
				}

			}

		});


	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (intent != null) {
			Bundle extras = intent.getExtras();

			switch(requestCode) {
			case ACTIVITY_BROWSE:
				if (extras != null) {
					String path = extras.getString("FILEPATH");
					fileName=extras.getString("FILENAME");
					txtFilePath.setText(path);
				}
				break;
			}
		}

	}


	private void loadDataFromICISWorkbook(String fileNamePath){


		try {
			File inputWorkbook = new File(fileNamePath);
			workbook = Workbook.getWorkbook(inputWorkbook);
			studyFileName=fileName.substring(0, fileName.length()-4);
			FileManager fileManager= new FileManager();

			boolean fileCreated=fileManager.createNewDatabaseFile(getAssets(), studyFileName);

			if(fileCreated){
				// run progress
				// set connection to the database newly created

				databaseConnect= new DatabaseConnect(ImportWorkbookActivity.this, studyFileName);
				databaseConnect.openDataBase();
				maintenanceManager= new MaintenanceManager(databaseConnect.getDataBase());
				LongRunningTask task = new LongRunningTask();
				task.execute(new String[] {studyFileName});

			}else{
				Toast.makeText(ImportWorkbookActivity.this,"Study File already exist . . ." , Toast.LENGTH_SHORT).show();
			}

		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	
	private class LongRunningTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progress;
		protected void onPreExecute() {
			progress = ProgressDialog.show(ImportWorkbookActivity.this, "Importing "+studyFileName + " Workbook", "Please wait...");
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			try{
				if(result){
					progress.dismiss();
					Toast.makeText(ImportWorkbookActivity.this,"Successfully imported the " + studyFileName+" Workbook" , Toast.LENGTH_SHORT).show();
					databaseConnection=new DatabaseConnectionInstance();
					databaseConnection.savePath(openFileOutput("ConnectionProperties.txt",MODE_WORLD_READABLE),FieldLabPath.STUDY_FOLDER+studyFileName.toString());
					File fImageData = new File(FieldLabPath.IMAGE_FOLDER+studyFileName);
					fImageData.mkdirs();
					
					File fAudioData = new File(FieldLabPath.AUDIO_FOLDER+studyFileName);
					fAudioData.mkdirs();
					
					Bundle bundle = new Bundle();
					bundle.putString("DBNAME", studyFileName);
					bundle.putString("STATUS","OK");

					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					finish();
					
				}else{
					progress.dismiss();
					Toast.makeText(ImportWorkbookActivity.this,"Encountered Problem during Import. Please check " + studyFileName+" Workbook File" , Toast.LENGTH_SHORT).show();
					File file = new File(FieldLabPath.STUDY_FOLDER+studyFileName);
					file.delete();
				}
			}catch(SQLiteFullException s){
				File file = new File(FieldLabPath.STUDY_FOLDER+studyFileName);
				file.delete();
				progress.dismiss();
				Toast.makeText(ImportWorkbookActivity.this,"Encountered Problem during Import. Please check " + studyFileName+" Workbook File" , Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			ImportWorkbookManager importWorkbookManager= new ImportWorkbookManager();
			boolean successImport=false;;
			try{
				successImport=importWorkbookManager.importICISWorkbook(workbook,databaseConnect.getDataBase());
				if(!successImport){
					File file = new File(FieldLabPath.STUDY_FOLDER+studyFileName);
					file.delete();
//					Toast.makeText(ImportWorkbookActivity.this,"Encountered Problem during Import. Please check " + studyFileName+" Workbook File" , Toast.LENGTH_SHORT).show();
					return false;
				}
			} catch (SyncFailedException e) {
				// TODO Auto-generated catch block
				File file = new File(FieldLabPath.STUDY_FOLDER+studyFileName);
				file.delete();
				Toast.makeText(ImportWorkbookActivity.this,"Encountered Problem during Import. Please check " + studyFileName+" Workbook File" , Toast.LENGTH_SHORT).show();

			}
			return successImport;
		}

	}

	private void openXlsFile(){
		try {
		
			Intent intent=XLSManager.openXLS(txtFilePath.getText().toString());
			startActivity(intent);
		} 
		catch (ActivityNotFoundException e) {
			// Toast.makeText(OpenPdf.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("DBNAME",fileName);
	}

}
