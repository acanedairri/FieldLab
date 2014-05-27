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

import java.io.IOException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.utility.LoadDataFromCsvFile;
import org.irri.fieldlab.utility.XLSManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteFullException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ImportObservationActivity extends Activity {

	private static final int ACTIVITY_BROWSE = 0;
	private String fileName="";
	private DatabaseConnect databaseConnect;
	private Button btnBrowseObservation;
	private Button btnViewObservation;
	private Button btnImportObservation;
	private FieldLabManager fieldLabManager;
	private EditText txtFilePathObservation;
	private String databaseName;
	public String fileNamePath;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.importobservation);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}


		btnBrowseObservation= (Button) findViewById(R.id.btnBrowseObservation);
		btnBrowseObservation.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent(ImportObservationActivity.this, FileChooserActivity.class);
				i.putExtra("folder", "export");
				startActivityForResult(i, ACTIVITY_BROWSE);
			}
		});

		txtFilePathObservation = (EditText) findViewById(R.id.txtFilePathObservation);
		btnViewObservation= (Button) findViewById(R.id.btnViewObservation);

		btnViewObservation.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				openXlsFile();

			}
		});

		btnImportObservation = (Button) findViewById(R.id.btnImportObservation);

		btnImportObservation.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click

				fileNamePath=txtFilePathObservation.getText().toString();
				if (fileNamePath != null
						&& !fileNamePath.equals("")
						&& !fileNamePath.contains(
						"Type the complete path here.")
						&& (fileNamePath.endsWith(".csv") || fileNamePath
								.endsWith(".csv")) && fileNamePath.contains(databaseName)) {


					try {
						loadObservationData();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Toast.makeText(ImportObservationActivity.this,
							"Please select a valid file/study!", Toast.LENGTH_LONG)
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
					txtFilePathObservation.setText(path);
				}
				break;
			}
		}

	}


	private void loadObservationData() throws IOException{

		boolean fileValid=true;
		fileNamePath=txtFilePathObservation.getText().toString();
		if(fileValid){

			databaseConnect= new DatabaseConnect(ImportObservationActivity.this, databaseName);
			databaseConnect.openDataBase();
			fieldLabManager= new FieldLabManager(databaseConnect.getDataBase());
			LongRunningTask task = new LongRunningTask();
			task.execute(new String[] {databaseName});

		}else{
			Toast.makeText(ImportObservationActivity.this,"Study File already exist . . ." , Toast.LENGTH_SHORT).show();
		}

	}



	private class LongRunningTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progress;
		protected void onPreExecute() {
			progress = ProgressDialog.show(ImportObservationActivity.this, "Importing "+fileName + " Observation Data", "Please wait...");
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			try{
				if(result){
					progress.dismiss();
					Toast.makeText(ImportObservationActivity.this,"Successfully imported the " + fileName+" Observation Data" , Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putString("DBNAME", fileName);
					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					finish();
				}
			}catch(SQLiteFullException s){
				progress.dismiss();
				Toast.makeText(ImportObservationActivity.this,"Encountered Problem during Import. Please check " + fileName+" Observation Data" , Toast.LENGTH_SHORT).show();
			}

		}
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			boolean successImport=false;;
			LoadDataFromCsvFile loader= new LoadDataFromCsvFile();
			try {
				successImport=loader.loadObservationData(fileNamePath, fieldLabManager);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return successImport;
		}

	}

	private void openXlsFile(){
		try {

			Intent intent=XLSManager.openXLS(txtFilePathObservation.getText().toString());
			startActivity(intent);
		} 
		catch (ActivityNotFoundException e) {
			// Toast.makeText(OpenPdf.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
		}
	}

	//	@Override
	//	protected void onSaveInstanceState(Bundle outState) {
	//		super.onSaveInstanceState(outState);
	//		outState.putSerializable("DBNAME",fileName);
	//	}

}
