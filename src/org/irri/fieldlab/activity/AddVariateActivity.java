package org.irri.fieldlab.activity;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.database.manager.SettingsManager;
import org.irri.fieldlab.model.TableICIS;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddVariateActivity extends Activity {
	
	
	private EditText txtVariate;
	private EditText txtDescription;
	private EditText txtProperty;
	private EditText txtScale;
	private EditText txtMethod;
	private EditText txtValue;
	private EditText txtLabel;
	private Spinner spinnerDataType;
	private Button btnSave;
	private Button btnCancel;
	private String databaseName;
	private DatabaseConnect databaseConnect;
	private FieldLabManager fieldlabManager;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addvariate);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		init(savedInstanceState);
		
		txtVariate=(EditText) findViewById(R.id.txtVariate);
		txtDescription=(EditText) findViewById(R.id.txtDescription);
		txtProperty=(EditText) findViewById(R.id.txtProperty);
		txtScale=(EditText) findViewById(R.id.txtScale);
		txtMethod=(EditText) findViewById(R.id.txtMethod);
		txtValue=(EditText) findViewById(R.id.txtValue);
		txtLabel=(EditText) findViewById(R.id.txtLabel);
		txtVariate.requestFocus();
		
		spinnerDataType = (Spinner) findViewById(R.id.spinnerDataType);
		ArrayAdapter<CharSequence> adapterFormView = ArrayAdapter.createFromResource(
				this, R.array.datatype_choices,android.R.layout.simple_spinner_item);
		adapterFormView.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDataType.setAdapter(adapterFormView);
		
		btnSave= (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				if(txtVariate.getText().toString().length()>0){
					saveVariate();
					finish();
				}else{
					Toast.makeText(getBaseContext(),"Incomplete data. Input not save", Toast.LENGTH_SHORT).show();
				}

			}
		});

		btnCancel= (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
//				databaseConnect.close();
				finish();
			}
		});
			
	}


	private void init(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}
		
		databaseConnect= new DatabaseConnect(AddVariateActivity.this, databaseName);
		databaseConnect.openDataBase();
		fieldlabManager = new FieldLabManager(databaseConnect.getDataBase());
	}


	protected void saveVariate() {
		// TODO Auto-generated method stub
		
		String variate=txtVariate.getText().toString().trim();
		
		ContentValues contentVariate = new ContentValues();
		contentVariate.put(TableICIS.DESCRIPTION_TRAITCODE, variate);
		contentVariate.put(TableICIS.DESCRIPTION_DESCRIPTION, txtDescription.getText().toString());
		contentVariate.put(TableICIS.DESCRIPTION_PROPERTY, txtProperty.getText().toString());
		contentVariate.put(TableICIS.DESCRIPTION_SCALE, txtScale.getText().toString());
		contentVariate.put(TableICIS.DESCRIPTION_METHOD, txtMethod.getText().toString());
		contentVariate.put(TableICIS.DESCRIPTION_DATATYPE, spinnerDataType.getSelectedItem().toString());
		contentVariate.put(TableICIS.DESCRIPTION_VALUE, txtValue.getText().toString());
		contentVariate.put(TableICIS.DESCRIPTION_LABEL, txtLabel.getText().toString());
		contentVariate.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
		contentVariate.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
		contentVariate.put(TableICIS.DESCRIPTION_CATEGORY, "variate");
		contentVariate.put(TableICIS.DESCRIPTION_INPUTTYPE, "textfield");
		contentVariate.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R2");
		contentVariate.put(TableICIS.DESCRIPTION_VISIBLE, "Y");
		contentVariate.put(TableICIS.DESCRIPTION_LOCK, "N");
		fieldlabManager.getDescriptionManager().insertDescription(contentVariate);
		fieldlabManager.getObservationManager().alterTableObservation("`"+variate+"`");
		
	}



	
	
	
}