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
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.utility.ExportManager;
import org.irri.fieldlab.utility.FieldLabPath;
import org.irri.fieldlab.utility.MaintenanceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteFullException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ExportStudyActivity extends Activity {

	private static final int ACTIVITY_BROWSE = 0;
	private String fileName="";
	private EditText txtFilePath;
	private DatabaseConnect databaseConnect;
	private MaintenanceManager xlsFileManager;
	private String studyFileName;
	private Spinner spinnerExportOption;
	private Button btnExportStudy;
	private Button btnExportClose;
	private String databaseName;
	private FieldLabManager fieldLabManager;
	private String variateList;
	private String currentVariateCh;
	private EditText txtVariateSelected;
	private TextView lblMessageDoubleTap;
	private WritableWorkbook workbook;
	private ExportManager exportManager;
	private String barcodeRef;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exportstudy);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}

		init();

		spinnerExportOption = (Spinner) findViewById(R.id.spinnerExportOption);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.export_choices,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerExportOption.setAdapter(adapter);
		spinnerExportOption.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{

				if(spinnerExportOption.getSelectedItemPosition()==3){
					Bundle args = new Bundle();
					args.putString("variateList",getVariateList());
					args.putString("currentVariateCh",txtVariateSelected.getText().toString());

					removeDialog(0);
					showDialog(0, args);
					txtVariateSelected.setVisibility(0);
					lblMessageDoubleTap.setVisibility(0);
				}else{
					txtVariateSelected.setText("");
					txtVariateSelected.setVisibility(4);
					lblMessageDoubleTap.setVisibility(4);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});


		//		btnBrowse= (Button) findViewById(R.id.btnBrowse);
		//		btnBrowse.setOnClickListener(new View.OnClickListener() {
		//
		//			public void onClick(View v) {
		//				// Perform action on click
		//				Intent i = new Intent(ExportStudyActivity.this, FileChooserActivity.class);
		//				i.putExtra("folder", "workbook");
		//				startActivityForResult(i, ACTIVITY_BROWSE);
		//			}
		//		});



		btnExportStudy = (Button) findViewById(R.id.btnEportStudy);

		btnExportStudy.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				try {
					if(spinnerExportOption.getSelectedItemPosition()==0){
						exportDataToICISWorkbook();
					}else if(spinnerExportOption.getSelectedItemPosition()==1){
						exportAllObservationDataToCSVFormat();
					}else if(spinnerExportOption.getSelectedItemPosition()==3){
						if(txtVariateSelected.getText().length()>0){
							exportSpecificObservationDataToCSVFormat();
						}else{
							Toast.makeText(ExportStudyActivity.this,"No trait selected to export" , Toast.LENGTH_SHORT).show();
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BiffException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		

		});

		btnExportClose= (Button) findViewById(R.id.btnExportClose);
		btnExportClose.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("DBNAME", fileName);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});


		txtVariateSelected = (EditText) findViewById(R.id.txtVariateSelected);
		txtVariateSelected.setVisibility(4);

		final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

			public boolean onDoubleTap(MotionEvent e) {
				Bundle args = new Bundle();
				args.putString("variateList",getVariateList());
				args.putString("currentVariateCh",txtVariateSelected.getText().toString());

				removeDialog(0);
				showDialog(0, args);
				txtVariateSelected.setEnabled(true);
				return true;
			}
		});

		txtVariateSelected.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		});

		lblMessageDoubleTap=(TextView) findViewById(R.id.lblMessageDoubleTap);
		lblMessageDoubleTap.setVisibility(4);

	}


	protected void exportSpecificObservationDataToCSVFormat() {
		// TODO Auto-generated method stub
		TaskExportSpecificObservationDataCSV task = new TaskExportSpecificObservationDataCSV();
		task.execute(new String[] {databaseName});
	}


	protected void exportAllObservationDataToCSVFormat() {

		TaskExportCSV task = new TaskExportCSV();
		task.execute(new String[] {databaseName});

	}


	private void init() {
		databaseConnect= new DatabaseConnect(ExportStudyActivity.this, databaseName);
		databaseConnect.openDataBase();
		fieldLabManager=new FieldLabManager(databaseConnect.getDataBase());
		barcodeRef=fieldLabManager.getSettingsManager().getBarcodeReference();
		exportManager= new ExportManager(databaseConnect.getDataBase());

	}


	private String getVariateList() {

		Cursor cursor = fieldLabManager.getDescriptionManager().getVariate();
		String line="";
		do {
			line += cursor.getString(1)+",";
		} while (cursor.moveToNext());

		return line;
	}


	private void exportDataToICISWorkbook() throws IOException, BiffException, WriteException{

		File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+".xls");
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));
		workbook = Workbook.createWorkbook(file, wbSettings);
		TaskExportToWorkbook task = new TaskExportToWorkbook();
		task.execute(new String[] {databaseName});
	}





	private class TaskExportToWorkbook extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progress;
		protected void onPreExecute() {
			progress = ProgressDialog.show(ExportStudyActivity.this, "Export "+databaseName + " Workbook", "Please wait...");
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			try{
				if(result){
					progress.dismiss();
					Toast.makeText(ExportStudyActivity.this,"Successfully export the " + databaseName+" Workbook" , Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putString("DBNAME", fileName);
					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					finish();
				}
			}catch(SQLiteFullException s){
				File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+".xls");
				file.delete();
				progress.dismiss();
				Toast.makeText(ExportStudyActivity.this,"Encountered Problem during Export. Please check " + databaseName+" Workbook File" , Toast.LENGTH_SHORT).show();
			}

		}
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub

			boolean successExport=false;;
			try{
				successExport=exportManager.exportToICISWorkbook(workbook);
			} catch (SyncFailedException e) {
				// TODO Auto-generated catch block
				File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+".xls");
				file.delete();
				Toast.makeText(ExportStudyActivity.this,"Encountered Problem during export. Please check " + databaseName+" Workbook File" , Toast.LENGTH_SHORT).show();

			}
			return successExport;
		}

	}


	private class TaskExportCSV extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progress;
		protected void onPreExecute() {
			progress = ProgressDialog.show(ExportStudyActivity.this, "Export "+databaseName + " CSV", "Please wait...");
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			try{
				if(result){
					progress.dismiss();
					Toast.makeText(ExportStudyActivity.this,"Successfully export the " + databaseName+" CSV" , Toast.LENGTH_SHORT).show();
				}
			}catch(SQLiteFullException s){
//				File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+".xls");
//				file.delete();
				progress.dismiss();
				Toast.makeText(ExportStudyActivity.this,"Encountered Problem during Export. Please check " + databaseName+" Workbook File" , Toast.LENGTH_SHORT).show();
			}

		}
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub

			boolean successExport=false;;
			try {
				successExport=exportManager.exportAllObservationDataToCSV(databaseName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return successExport;
		}

	}
	
	
	private class TaskExportSpecificObservationDataCSV extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progress;
		protected void onPreExecute() {
			progress = ProgressDialog.show(ExportStudyActivity.this, "Export "+databaseName + " CSV", "Please wait...");
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			try{
				if(result){
					progress.dismiss();
					Toast.makeText(ExportStudyActivity.this,"Successfully export the " + databaseName+" CSV" , Toast.LENGTH_SHORT).show();
				}
			}catch(SQLiteFullException s){
//				File file = new File(FieldLabPath.EXPORTDATA_FOLDER+databaseName+".xls");
//				file.delete();
				progress.dismiss();
				Toast.makeText(ExportStudyActivity.this,"Encountered Problem during Export. Please check " + databaseName+" Workbook File" , Toast.LENGTH_SHORT).show();
			}

		}
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub

			boolean successExport=false;;
				try {
					successExport=exportManager.exportSpecificObservationDataCSV(databaseName,txtVariateSelected.getText().toString(),barcodeRef);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return successExport;
		}

	}

	private void openXlsFile(){
		try {
			String fileName=txtFilePath.getText().toString();
			Intent intent= xlsFileManager.getIntent(fileName);
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


	protected Dialog onCreateDialog(int id,Bundle args) {
		AlertDialog dialog = null;

		switch (id) {

		case 0:
			String currentVariateSelected=args.getString("currentVariateCh");
			final String[] itemsVariate = args.getString("variateList").split(",");
			final boolean[] itemsChecked= new boolean[itemsVariate.length];

			for(int i=0;i <itemsVariate.length;i++){
				if(currentVariateSelected.contains(itemsVariate[i].trim())){
					itemsChecked[i]=true;
				}
			}

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.info)
			.setTitle("Select Study Variate to Export")
			.setMultiChoiceItems(itemsVariate, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// TODO Auto-generated method stub
					//Toast.makeText(getBaseContext(), itemsRemarks[which] + (isChecked?"checked":"unchecked"),Toast.LENGTH_SHORT).show();
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				private String studyVariateSelected;

				public void onClick(DialogInterface dialog, int whichButton) {

					try{
						String studyVariateSelectDisplay="";
						studyVariateSelected="";
						/* User clicked Yes so do some stuff */
						for(int i=0;i < itemsVariate.length;i++){
							if(itemsChecked[i]==true){
								studyVariateSelected+="'"+itemsVariate[i]+"'" + ",";
								studyVariateSelectDisplay+=itemsVariate[i]+ ",";
							}else{

							}
						}
						studyVariateSelected= studyVariateSelected.substring(0, studyVariateSelected.length()-1);
						studyVariateSelectDisplay=studyVariateSelectDisplay.substring(0,studyVariateSelectDisplay.length()-1);
						txtVariateSelected.setText(studyVariateSelectDisplay);
						txtVariateSelected.requestFocus();
					}catch(Exception e){

					}
				}
			})

			.create();
		}
		return  dialog;

	}

}
