package org.irri.fieldlab.activity;



import java.util.ArrayList;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.SettingsManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity {


	private String databaseName;
	private DatabaseConnect databaseConnect;
	private SettingsManager settingsManager;
	private Spinner spinnerDataEntryFormView;
	private EditText txtNumRecPerPage;
	private Button btnSaveSettings;
	private Button btnCancelSettings;
	private int dataEntryFormView;
	private int lastrec;
	private int pagereccount;
	private CheckBox chkAutoPrompt;
	private String autoPrompt;
	private Spinner spinnerDataEntrySearchReference;
	private DescriptionManager descriptionManager;
	private String barcodeReference;
	private int indexOfSearchReference=0;
	private Spinner spinnerRCBD;
	private CheckBox chkMissingValueData;
	private String missingDataValue="N";
	private int rcbOption=0;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}

//		Toast.makeText(getBaseContext(),"Database Name" + databaseName, Toast.LENGTH_SHORT).show();
		
		databaseConnect= new DatabaseConnect(SettingsActivity.this, databaseName);
		databaseConnect.openDataBase();
		settingsManager=new SettingsManager(databaseConnect.getDataBase());
		descriptionManager= new DescriptionManager(databaseConnect.getDataBase());

		spinnerDataEntrySearchReference = (Spinner) findViewById(R.id.spinnerBarcodeReference);
		ArrayAdapter<String> adapterSearchReference = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_item, getStudyFactor());
		adapterSearchReference.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDataEntrySearchReference.setAdapter(adapterSearchReference);
		spinnerDataEntrySearchReference.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{


			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		spinnerDataEntryFormView = (Spinner) findViewById(R.id.spinnerDataEntryView);
		ArrayAdapter<CharSequence> adapterFormView = ArrayAdapter.createFromResource(
				this, R.array.dataentryformsview,
				android.R.layout.simple_spinner_item);
		adapterFormView.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDataEntryFormView.setAdapter(adapterFormView);
		
		spinnerRCBD = (Spinner) findViewById(R.id.spinnerRCBD);
		ArrayAdapter<CharSequence> adapterRCBD = ArrayAdapter.createFromResource(
				this, R.array.serpentineOrder,
				android.R.layout.simple_spinner_item);
		adapterRCBD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRCBD.setAdapter(adapterRCBD);

		txtNumRecPerPage=(EditText) findViewById(R.id.txtNumRecord);

		chkAutoPrompt = (CheckBox) findViewById(R.id.chkAutoPromptLookup);

		chkAutoPrompt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked)
					autoPrompt="Y";
				else
					autoPrompt="N";
			}
		});
		
		chkMissingValueData = (CheckBox) findViewById(R.id.chkDisplayMissingValue);
		chkMissingValueData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked)
					missingDataValue="Y";
				else
					missingDataValue="N";
			}
		});


		btnSaveSettings= (Button) findViewById(R.id.btnSaveSettings);
		btnSaveSettings.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				saveSettings();
				
			}
		});

		btnCancelSettings= (Button) findViewById(R.id.btnCancelSetting);
		btnCancelSettings.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				databaseConnect.close();
				finish();
			}
		});

		init();

		spinnerDataEntrySearchReference.setSelection(indexOfSearchReference);
		spinnerRCBD.setSelection(rcbOption);

	}

//	private int getIndexOfBarcodeReference() {
//		// TODO Auto-generated method stub
//		int indexOfSearchReference=0;
//		Cursor cursor = traitManager.getStudyFactor();
//		//startManagingCursor(variateCursor);
//		int counter=0;
//		if(cursor.getCount() > 0){
//
//			if (cursor.moveToFirst()){
//				do {
//					if(cursor.getString(0).equals(barcodeReference)){
//						indexOfSearchReference=counter;
//						break;
//					}
//
//					counter++;
//				} while (cursor.moveToNext());
//			}
//		}
//		cursor.close();
//		return indexOfSearchReference;
//
//	}

	protected void saveSettings() {
		// TODO Auto-generated method stub
		int dataentryview=spinnerDataEntryFormView.getSelectedItemPosition();
		int rcpOption=spinnerRCBD.getSelectedItemPosition();
		
		barcodeReference=spinnerDataEntrySearchReference.getSelectedItem().toString();
		if(barcodeReference.equals("-Select-")){
			barcodeReference="";
		}
		settingsManager.updateSettings(barcodeReference,dataentryview,Integer.valueOf(txtNumRecPerPage.getText().toString()),autoPrompt,missingDataValue,rcpOption);
		Toast.makeText(getBaseContext(),"Settings Updated", Toast.LENGTH_SHORT).show();
		finish();
	}

	private void init() {
		// TODO Auto-generated method stub
		Cursor cursor=settingsManager.getSettings();
		autoPrompt= cursor.getString(2);
		lastrec=cursor.getInt(3);
		dataEntryFormView=cursor.getInt(4);
		pagereccount=cursor.getInt(5);
		missingDataValue=cursor.getString(6);
		rcbOption=cursor.getInt(7);
		
		
		if(dataEntryFormView==0){
			spinnerDataEntryFormView.setSelection(0);
		}else{
			spinnerDataEntryFormView.setSelection(1);
		}
		
		
		txtNumRecPerPage.setText(String.valueOf(pagereccount));


		if(autoPrompt.equals("Y")){
			chkAutoPrompt.setChecked(true);
		}else{
			chkAutoPrompt.setChecked(false);
		}
		
		if(missingDataValue.equals("Y")){
			chkMissingValueData.setChecked(true);
		}else{
			chkMissingValueData.setChecked(false);
		}
	}

	private ArrayList<String> getStudyFactor() {
		ArrayList<String> list= new ArrayList<String>();
		list.clear();
		Cursor cursorRef=settingsManager.getSettings();
		barcodeReference=cursorRef.getString(1);
		
		Cursor cursor = descriptionManager.getFactor();
		//startManagingCursor(variateCursor);
		int counter=1;
		if(cursor.getCount() > 0){

			if (cursor.moveToFirst()){
				list.add("-Select-");
				do {
					if(cursor.getString(1).equals(barcodeReference)){
						indexOfSearchReference=counter;
					}
					list.add(cursor.getString(1).toString());
					counter++;
				} while (cursor.moveToNext());
			}
		}else{
			list.add("");
		}
		cursor.close();
		return list;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}
}