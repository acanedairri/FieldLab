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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.utility.DatabaseConnectionInstance;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OpenStudyActivity extends Activity {

	private static final int ACTIVITY_BROWSE=0;
	private final static String DB_ERROR = "ICISMobileDatabaseError";
	private EditText txtDatabasePath;
//	private EditText txtNewDatabase;
	private DatabaseConnectionInstance databaseConnection;

	private String databaseName = "";
	private Button btnBrowseOpenStudy;
	private Button btnCreateOpenStudy;
	private Button btnCloseOpenStudy;
	private Button btnOpenStudy;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openstudy);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		databaseConnection = new DatabaseConnectionInstance();

		btnBrowseOpenStudy = (Button) findViewById(R.id.btnBrowseOpenStudy);
		btnCloseOpenStudy=(Button) findViewById(R.id.btnCloseOpenStudy);
//		btnCreateOpenStudy=(Button) findViewById(R.id.btnCreateOpenStudy);
		btnOpenStudy=(Button) findViewById(R.id.btnOpenStudy);

		txtDatabasePath = (EditText) findViewById(R.id.txtDatabasePath);
		

		if(txtDatabasePath.length() > 0){
			FileInputStream fIn = null;
			try {
				fIn = openFileInput("ConnectionProperties.txt");
				txtDatabasePath.setText(databaseConnection.findPath(fIn));
				String dbFile[]=txtDatabasePath.getText().toString().split("/");
				setDatabaseName(dbFile[dbFile.length-1]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		btnBrowseOpenStudy.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent(OpenStudyActivity.this, FileChooserActivity.class);
				i.putExtra("folder", "study");
				startActivityForResult(i, ACTIVITY_BROWSE);
			}
		});

//		btnCreateOpenStudy.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				// Perform action on click
//
//				txtNewDatabase = (EditText) findViewById(R.id.txtNewDatabase);
//				if(txtNewDatabase.getText().length() > 0){
//					FileManager fileManager= new FileManager();
//					boolean fileCreated=fileManager.createNewDatabaseFile(getAssets(), txtNewDatabase.getText().toString());
//
//					if(fileCreated){
//						Toast.makeText(OpenStudyActivity.this,"Study File Created" , Toast.LENGTH_SHORT).show();
//						txtDatabasePath.setText(FieldLabPath.STUDY_FOLDER+txtNewDatabase.getText());
//					}else{
//						Toast.makeText(OpenStudyActivity.this,"Study File already exist . . ." , Toast.LENGTH_SHORT).show();
//					}
//					
//				}else{
//					Toast.makeText(OpenStudyActivity.this,"No study name defined" , Toast.LENGTH_SHORT).show();
//				}
//
//			}
//		});

		btnOpenStudy.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click

				if (getDatabaseName() != null && !getDatabaseName().equals("") && !getDatabaseName().contains("Type the complete path here.") && !getDatabaseName().contains(".")) {
					DatabaseConnect databaseConnect = new DatabaseConnect(OpenStudyActivity.this, databaseName);
					String status = "OK";
					try {
						// Creates the icismobile database file in the SD Card if it does not exist.
						// If it does exist, it will open that database for querying.
						databaseConnect.createDataBase();
						databaseConnection.savePath(openFileOutput("ConnectionProperties.txt",MODE_WORLD_READABLE),txtDatabasePath.getText().toString());
						databaseConnect.close();

						//TODO: Progress bar
						Toast.makeText(OpenStudyActivity.this, "Study " + databaseName + " is now ready to use",
								Toast.LENGTH_SHORT).show();

					} catch (IOException e) {
						status = "FAIL";
						e.printStackTrace();
						Log.e(DB_ERROR, "IOException encountered during DB creation. Check access to SD Card!");
					}

					Bundle bundle = new Bundle();
					bundle.putString("DBNAME", databaseName);
					bundle.putString("DBSTATUS", status);

					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					finish();
				} else {
					Toast.makeText(OpenStudyActivity.this, "Please enter a valid file!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		btnCloseOpenStudy.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				String status = "OK";
				Bundle bundle = new Bundle();
				bundle.putString("DBNAME", databaseName);
				bundle.putString("DBSTATUS", status);

				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
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
					setDatabaseName(extras.getString("FILENAME"));
					txtDatabasePath.setText(path);
				}
				break;
			}
		}

	}


	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}


}
