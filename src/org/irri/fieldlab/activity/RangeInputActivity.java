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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.database.manager.ObservationManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.database.manager.SettingsManager;
import org.irri.fieldlab.utility.DataEntryUtil;
import org.irri.fieldlab.utility.Validation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RangeInputActivity extends Activity {

	private static final int ACTIVITY_BROWSE = 0;
	private static final int ACTIVITY_CALENDAR=1;
	private String fileName="";
	private DatabaseConnect databaseConnect;

	private String databaseName;
	private FieldLabManager fieldLabManager;
	private Button btnRangeInputOk;
	private View btnRangeInputCancel;
	private EditText txtRangeVariateValue;
	private String traitcode;
	private String startRec;
	private EditText txtRecStart;
	private EditText txtRecEnd;
	private TextView lblTraitcode;
	protected int oldEditTextId;
	protected int currentEditTextId;
	private ScoringManager scoringManager;
	protected boolean hasScoring;
	protected SettingsManager settingsManager;
	protected String dType;
	protected boolean isValidRange=false;
	private Validation validation;
	protected boolean validScore;
	private DescriptionManager descriptionManager;
	private int totalRecord;
	private boolean validSavingRecord=true;
	private String recordNo;
	private ObservationManager observationManager;
	private String dateValue;
	private static DateFormat sdf;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rangeinput);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;

			traitcode=extras.getString("TRAITCODE");
			dType=extras.getString("DTYPE");
			startRec=extras.getString("STARTREC");
			totalRecord=Integer.valueOf(extras.getString("TOTALRECORD"));
			recordNo=extras.getString("RECORDNO");
		}

		init();


		btnRangeInputOk = (Button) findViewById(R.id.btnRangeInputOk);

		btnRangeInputOk.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				saverecord();
			}
		});

		btnRangeInputCancel= (Button) findViewById(R.id.btnRangeInputCancel);
		btnRangeInputCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});


		lblTraitcode= (TextView) findViewById(R.id.lblInputMessage);
		lblTraitcode.setText(traitcode);
		
		txtRangeVariateValue = (EditText) findViewById(R.id.txtRangeVariateValue);
		txtRecStart = (EditText) findViewById(R.id.txtRecStart);
		txtRecStart.setText(recordNo);
		txtRecEnd = (EditText) findViewById(R.id.txtRecEnd);
		txtRecEnd.requestFocus();



		txtRangeVariateValue.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {

				String editTextValue=String.valueOf(((EditText) v).getText());
				currentEditTextId=((EditText) v).getId();
				if(hasFocus) {
					((EditText) v).setTextSize(40);
					hasScoring=scoringManager.hasScoring(traitcode);
					if(hasScoring && settingsManager.autoPromptScoring()){
						showScoringDialog(DataEntryUtil.getScoring(traitcode, scoringManager), traitcode, currentEditTextId);
					}
					DataEntryUtil.setEditTextInputType(dType,v);
				}

			}
		});

		final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {



			private int ACTIVITY_CALENDAR;

			public boolean onDoubleTap(MotionEvent e) {

				boolean hasScoring=scoringManager.hasScoring(traitcode);

				if(hasScoring && (!dType.equals("D") || !dType.equals("CD"))){
					
					showScoringDialog(DataEntryUtil.getScoring(traitcode, scoringManager), traitcode, currentEditTextId);
				}
				
				if(dType.equals("D") || dType.equals("CD")){
					
					Intent i = new Intent(RangeInputActivity.this, CalendarViewActivity.class);
					//					Toast.makeText(getBaseContext(),"Value to PASS "+String.valueOf(currentEditTextId), Toast.LENGTH_SHORT).show();
					i.putExtra("CURRENTTEXTID",currentEditTextId);
					i.putExtra("DTYPE",dType);
					startActivityForResult(i, ACTIVITY_CALENDAR);
				}

				return true;
			}
		});

		txtRangeVariateValue.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == Activity.RESULT_OK) { 
			    Bundle extras = intent.getExtras();
				sdf = new SimpleDateFormat("MM/dd/yyyy"); 
//				int prevID=extras.getInt("CURRENT_EDITTEXT_ID");
				String prevDtype=extras.getString("DTYPE");
				Toast.makeText(getBaseContext(),"Current ID  " +prevDtype, Toast.LENGTH_SHORT).show();

//				EditText txtEditCurrent = (EditText) findViewById(prevID);
				dateValue=extras.getString("DATESELECTED");
				if(prevDtype.equals("CD")){
					txtRangeVariateValue.setText(dateValue);
				}else{
					String sDate=descriptionManager.getStudyStartDate();

					String d=sDate.substring(4, 6)+"/"+sDate.substring(6,8)+"/"+sDate.substring(0, 4);
					Date d1 = new Date(d);  
					Date d2 = new Date(dateValue); 

					Calendar cal1 = Calendar.getInstance();cal1.setTime(d1);  
					Calendar cal2 = Calendar.getInstance();cal2.setTime(d2);
					long noOfDays=daysBetween(cal1, cal2);
					txtRangeVariateValue.setText(String.valueOf(noOfDays));

				}
				txtRangeVariateValue.requestFocus();
				
			}
		
	}
	
	
	public static String now() {
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());

	}

	public static long daysBetween(Calendar startDate, Calendar endDate) {  
		Calendar date = (Calendar) startDate.clone();  
		long daysBetween = 0;  
		while (date.before(endDate)) {  
			date.add(Calendar.DAY_OF_MONTH, 1);  
			daysBetween++;  
		}  
		return daysBetween;  
	} 


	protected void saverecord() {
		// TODO Auto-generated method stub

		validSavingRecord=isValidInput();
		
		if(validSavingRecord){
			
			saveRecordAction();
			
		}

	}



	private void saveRecordAction() {
		Bundle bundle = new Bundle();

    	bundle.putString("STARTREC", txtRecStart.getText().toString());
    	bundle.putString("ENDREC", txtRecEnd.getText().toString());
    	bundle.putString("TRAITCODE", lblTraitcode.getText().toString());
    	bundle.putString("VALUE", txtRangeVariateValue.getText().toString());

    	Intent mIntent = new Intent();
    	mIntent.putExtras(bundle);
    	setResult(RESULT_OK, mIntent);
    	finish();
		
	}



	private boolean isValidInput() {

		if(txtRecStart.getText().toString().length() < 1  || txtRecEnd.getText().toString().length() < 1 || txtRangeVariateValue.getText().toString().length() < 1 || lblTraitcode.getText().toString().length() < 1){
			removeDialog(3);
			showDialog(3);
			return false;
		}else{

			int startRec=Integer.valueOf(txtRecStart.getText().toString());
			int endRec=Integer.valueOf(txtRecEnd.getText().toString());
			if((startRec < 1) || (endRec > totalRecord) || (startRec > endRec) || (txtRangeVariateValue.getText().length() < 0)){
				removeDialog(3);
				showDialog(3);
				return false;
			}
			
		}
		
		String c=txtRangeVariateValue.getText().toString().substring(0,1);
		if(dType.equals("N")){
			isValidRange=validation.isValidRange(txtRangeVariateValue.getText().toString(), traitcode, descriptionManager);
			
		}else{
			return true;
		}
	
		if(!isValidRange){
			String validRange=validation.getValidRange(traitcode, descriptionManager);
			Bundle args = new Bundle();
			args.putString("validRange",validRange);
			removeDialog(1);
			showDialog(1, args);
			return false;

		}

		return true;
	}



	protected void showScoringDialog(String scoring,String traitcode,int currentEditTextId) {
		// TODO Auto-generated method stub
		Bundle args = new Bundle();
		args.putString("scoring",DataEntryUtil.getScoring(traitcode, scoringManager));
		args.putString("traitcode",traitcode);
		args.putInt("currentEditTextId",currentEditTextId);
		removeDialog(2);
		showDialog(2,args);
		//		dialogScoring(args);
	}

	private void init() {
		databaseConnect= new DatabaseConnect(RangeInputActivity.this, databaseName);
		databaseConnect.openDataBase();
		fieldLabManager=new FieldLabManager(databaseConnect.getDataBase());
		scoringManager=fieldLabManager.getScoringManager();
		settingsManager=fieldLabManager.getSettingsManager();
		descriptionManager=fieldLabManager.getDescriptionManager();
		observationManager=fieldLabManager.getObservationManager();
		validation= new Validation();

	}

	protected Dialog onCreateDialog(int id,Bundle args) {

		AlertDialog dialog = null;
		switch (id) {
		case 0:
			String scoring=args.getString("scoring");
			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.warning)
			.setTitle("Input Warning Message")
			.setMessage("Invalid variate score value entered .\n" +
					"-------------------\n" +
					"Valid Scoring Value\n" +
					"-------------------\n" +scoring+
					"-------------------\n" +
			"Continue to save this entry? ")
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					EditText editText = (EditText) findViewById(currentEditTextId);
					editText.requestFocus();
				}
			})
			.setNegativeButton("Cancel", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					EditText editText = (EditText) findViewById(oldEditTextId);
					editText.setText("");
					editText.requestFocus();

				}
			})
			.create();

		case 1:
			String[] validRange=args.getString("validRange").split("-");
			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.warning)
			.setTitle("Warning Message")
			.setMessage("Value entered not within the range:\n" +
					"Minimum Value : " +validRange[0]+
					"\nMaximum Value : "+validRange[1] +
			"\nContinue to save this entry? ")
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {


				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					saveRecordAction();
				}
			})
			.setNegativeButton("Cancel", new
					DialogInterface.OnClickListener() {
				private int oldEditTextId;

				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					txtRangeVariateValue.setText("");

				}
			})
			.create();

		case 2:

			final String[] items = args.getString("scoring").split("\n");
			String traitcode=args.getString("traitcode");
			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.info)
			.setTitle(traitcode + " Scoring" )
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					EditText editText = (EditText) findViewById(currentEditTextId);
					editText.setText(items[item].split(":")[0].trim());
					editText.requestFocus();
				}
			})
			.create();

		case 3:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.info)
			.setTitle("Error Message")
			.setMessage("Has error in data input... ")
			.setPositiveButton("Close", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
				}
			})
			.create();
		}
		return  dialog;

	}



}
