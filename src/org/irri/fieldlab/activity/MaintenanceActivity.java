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
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.RemarksManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.database.manager.TraitDictionaryManager;
import org.irri.fieldlab.model.TableICIS;
import org.irri.fieldlab.utility.FieldLabPath;
import org.irri.fieldlab.utility.MaintenanceManager;
import org.irri.fieldlab.utility.XLSManager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MaintenanceActivity extends Activity {

//	private static final int ACTIVITY_BROWSE = 0;
	private String databaseName = "";
	private Button btnLoad;
	private Button btnView;
	private Button btnClose;
	private EditText txtFilePath;
	private Spinner spinnerMaintenance;
	private DatabaseConnect databaseConnect;
	private MaintenanceManager maintenanceManager;
	private String DEFAULT_FILE_SCORING= FieldLabPath.MAINTENANCE_FOLDER+"scoring.xls";
	private String DEFAULT_FILE_REMARKS=FieldLabPath.MAINTENANCE_FOLDER+"remarks.xls";
	private String DEFAULT_FILE_TRAIT=FieldLabPath.MAINTENANCE_FOLDER+"traits.xls";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintenance);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		

		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}
		
		databaseConnect= new DatabaseConnect(MaintenanceActivity.this, databaseName);
		databaseConnect.openDataBase();
		maintenanceManager= new MaintenanceManager(databaseConnect.getDataBase());
		
		
		spinnerMaintenance = (Spinner) findViewById(R.id.spinnerMaintenance);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.maintenance_choices,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMaintenance.setAdapter(adapter);
		
	
		spinnerMaintenance.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				int position=spinnerMaintenance.getSelectedItemPosition();
				if(position==0){
					txtFilePath.setText(DEFAULT_FILE_SCORING);
				}else if(position==1){
					txtFilePath.setText(DEFAULT_FILE_REMARKS);
				}else{
					txtFilePath.setText(DEFAULT_FILE_TRAIT);
				}
				

			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		txtFilePath = (EditText) findViewById(R.id.txtFilePath);
		txtFilePath.setText(DEFAULT_FILE_SCORING);

		btnView= (Button) findViewById(R.id.btnView);

		btnView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				openXlsFile();

			}
		});

		btnLoad = (Button) findViewById(R.id.btnLoad);
		btnLoad.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				
				
				String fileNamePath=txtFilePath.getText().toString();
				if (fileNamePath != null
						&& !fileNamePath.equals("")
						&& !fileNamePath.contains(
						"Type the complete path here.")
						&& (fileNamePath.endsWith(".xls") || fileNamePath
								.endsWith(".XLS"))) {

					int choice = spinnerMaintenance.getSelectedItemPosition();
					if(choice==0){
						maintenanceManager.loadMaintenanceData(fileNamePath,0);
						Toast.makeText(
								MaintenanceActivity.this,
								"Scoring table updated...."
								,Toast.LENGTH_SHORT).show();

					}else if(choice==1){
						maintenanceManager.loadMaintenanceData(fileNamePath,1);
						Toast.makeText(
								MaintenanceActivity.this,
								"Remarks table updated.... "
								,Toast.LENGTH_SHORT).show();

					}else{
						maintenanceManager.loadMaintenanceData(fileNamePath,2);
						Toast.makeText(
								MaintenanceActivity.this,
								"Trait table updated.... "
								,Toast.LENGTH_SHORT).show();
					}

					

				} else {
					Toast.makeText(MaintenanceActivity.this,
							"Please enter a valid file!", Toast.LENGTH_LONG)
							.show();
				}

			}

		});
		
		btnClose= (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("DBNAME", databaseName);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();

			}
		});
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		super.onActivityResult(requestCode, resultCode, intent);
//		if (intent != null) {
//			Bundle extras = intent.getExtras();
//
//			switch(requestCode) {
//			case ACTIVITY_BROWSE:
//				if (extras != null) {
//					String path = extras.getString("FILEPATH");
//					fileName=extras.getString("FILENAME");
//					txtFilePath.setText(path);
//				}
//				break;
//			}
//		}
//
//	}


/*
	private void viewTrait() {
	// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		TraitManager traitManager= new TraitManager(databaseConnect.getDataBase());
		Cursor cursor = traitManager.getAllTrait();
		cursor.moveToFirst();
		
		do {
			String line = String.valueOf(cursor.getInt(7));
			Toast.makeText(MaintenanceActivity.this,line , Toast.LENGTH_SHORT).show();
		} while(cursor.moveToNext());
	
}

	private void viewRemarks() {
		// TODO Auto-generated method stub
		RemarksManager remarksManager= new RemarksManager(databaseConnect.getDataBase());
		Cursor cursor = remarksManager.getAllRemarks();
		cursor.moveToFirst();
		
		do {
			String line = cursor.getString(1)+ " "+ cursor.getString(2);
			Toast.makeText(MaintenanceActivity.this,line , Toast.LENGTH_SHORT).show();
		} while(cursor.moveToNext());
		
	}

	private void viewScoring() {
		// TODO Auto-generated method stub
		
		ScoringManager scoringManager= new ScoringManager(databaseConnect.getDataBase());
		Cursor cursor = scoringManager.getAllScoring();
		cursor.moveToFirst();
		
		do {
			String line = cursor.getString(0)+ " -  "+ cursor.getString(1) + "-"+cursor.getString(2);
			Toast.makeText(MaintenanceActivity.this,line , Toast.LENGTH_SHORT).show();
		} while (cursor.moveToNext());
		
		
	}
*/
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
		outState.putSerializable("DBNAME",databaseName);
	}

}
