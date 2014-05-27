package org.irri.fieldlab.activity;

import java.io.IOException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.SettingsManager;
import org.irri.fieldlab.utility.DatabaseConnectionInstance;
import org.irri.fieldlab.utility.FieldLabPath;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StudyDataMenuActivity extends Activity implements MediaScannerConnectionClient{


	private ImageView btnObservationEntry;
	private String databaseName;
	private ImageView btnDescription;
	//	private View btnBrowse;
	//	private ImageView btnSetting;

	private SettingsManager settingsManager;
	private int lastrec;
	private int dataEntryFormView;
	private int pagereccount;
	private ImageView btnExportData;
	private ImageView btnImportData;
	private ImageView btnOpenStudy;
	private ImageView btnMaintenance;
//	private ImageView btnConnectReader;
	private ImageView btnBack;
	private ImageView btnSettings;
	private ImageView btnViewPhoto;
	private TextView lblStudy;
	private static final int ACTIVITY_CONNECT_STUDY=0;
	private static final int ACTIVITY_CONNECT_DEVICE=1;

	public String[] allFiles;
	private String SCAN_PATH ;
	private static final String FILE_TYPE = "images/*";
	private MediaScannerConnection conn;
	private String currentDevice="";
	private int connetionState=0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studydatamenu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");
		
		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}


		lblStudy=(TextView) findViewById(R.id.lblStudy);
		lblStudy.setText("Study : " + databaseName);

		

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}

		btnOpenStudy= (ImageView) findViewById(R.id.btnOpenStudy);
		btnOpenStudy.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
				Intent i = new Intent(StudyDataMenuActivity.this, FileChooserActivity.class);
				i.putExtra("folder", "study");
				startActivityForResult(i, ACTIVITY_CONNECT_STUDY);

			}
		});

		btnObservationEntry= (ImageView) findViewById(R.id.btnObservationEntry);
		btnObservationEntry.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				initSettings();
//				Toast.makeText(StudyDataMenuActivity.this,dataEntryFormView + " "+ lastrec+ " "+ databaseName , Toast.LENGTH_SHORT).show();
				if(dataEntryFormView==0){

					Intent i = new Intent(StudyDataMenuActivity.this, DataEntryFormSingleActivity.class);
					i.putExtra("DBNAME",databaseName);
					i.putExtra("LASTRECORD", String.valueOf(lastrec));
					startActivity(i);
				}else{
					Intent i2 = new Intent(StudyDataMenuActivity.this, DataEntryFormContinuousActivity.class);
					i2.putExtra("DBNAME",databaseName);
					i2.putExtra("LASTRECORD", String.valueOf(lastrec));
					startActivity(i2);
				}
				
				
			}
		});

		btnDescription= (ImageView) findViewById(R.id.btnDescription);
		btnDescription.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
//				Intent i = new Intent(StudyDataMenuActivity.this,TraitListActivity.class);
				Intent i = new Intent(StudyDataMenuActivity.this,StudyDescriptionTabMainActivity.class);
				i.putExtra("DBNAME", databaseName);
				startActivity(i);
				
		
			}
		});


		btnImportData= (ImageView) findViewById(R.id.btnImportData);
		btnImportData.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(StudyDataMenuActivity.this,ImportObservationActivity.class);
				i.putExtra("DBNAME", databaseName);
				startActivity(i);

			}
		});

		btnMaintenance= (ImageView) findViewById(R.id.btnMaintenance);
		btnMaintenance.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks

				try{

					Intent intentMaintenance = new Intent(StudyDataMenuActivity.this, MaintenanceActivity.class);
					intentMaintenance.putExtra("DBNAME", databaseName);
					startActivity(intentMaintenance);
				}catch(Exception e){
					Toast.makeText(StudyDataMenuActivity.this,"No active study set" , Toast.LENGTH_SHORT).show();
				}

			}
		});

//		btnConnectReader= (ImageView) findViewById(R.id.btnConnectReader);
//		btnConnectReader.setOnClickListener(new View.OnClickListener() {
//
//
//			public void onClick(View v) {
//				// Perform action on clicks
//				Intent i = new Intent(StudyDataMenuActivity.this,BarcodeScannerConnectActivity.class);
//				i.putExtra("CONNECTION_STATE", connetionState );
//				i.putExtra("CURRENT_DEVICE", currentDevice );
//				i.putExtra("DBNAME", databaseName);
//				startActivityForResult(i, ACTIVITY_CONNECT_DEVICE);
//			}
//		});

		btnBack= (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
				finish();
			}
		});


		btnExportData= (ImageView) findViewById(R.id.btnExportData);
		btnExportData.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
				Intent i = new Intent(StudyDataMenuActivity.this, ExportStudyActivity.class);
				i.putExtra("DBNAME",databaseName);
				startActivity(i);
			}
		});
		
		btnSettings= (ImageView) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
				Intent i = new Intent(StudyDataMenuActivity.this, SettingsActivity.class);
				i.putExtra("DBNAME",databaseName);
				startActivity(i);
			}
		});

//		btnViewPhoto= (ImageView) findViewById(R.id.btnViewPhoto);
//		btnViewPhoto.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				File folder = new File(Environment.getExternalStorageDirectory().toString()+FieldLabPath.IMAGE_PATH+databaseName+"/");
//				allFiles=null;
//				allFiles = folder.list();
//				if(allFiles.length>0){
//					startScan();
//				}else{
//					Toast.makeText(StudyDataMenuActivity.this,"This features is temporarily disabled. You can access the image by under the images folder of the application" , Toast.LENGTH_SHORT).show();
//				}
//			}
//		});


	}

	private  SQLiteDatabase connectToDatabase(){
		DatabaseConnectionInstance databaseConnection = new DatabaseConnectionInstance();
		DatabaseConnect databaseConnect= new DatabaseConnect(StudyDataMenuActivity.this, databaseName);
		databaseConnect.openDataBase();
		return databaseConnect.getDataBase();
		
	}
	private void startScan()
	{
		if(conn!=null)
		{
			conn.disconnect();
		}
		conn = new MediaScannerConnection(this,this);
		conn.connect();


	}

	private void initSettings() {
		settingsManager=new SettingsManager(connectToDatabase());
		Cursor cursor=settingsManager.getSettings();
		lastrec=cursor.getInt(3);
		dataEntryFormView=cursor.getInt(4);
		pagereccount=cursor.getInt(5);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent != null) {

			Bundle extras = intent.getExtras();

			switch(requestCode) {
			case ACTIVITY_CONNECT_STUDY:
				if (extras != null) {
					String path = extras.getString("FILEPATH");
					databaseName=extras.getString("FILENAME");
					setDatabaseConnection(path);
					lblStudy.setText("Study : " + databaseName);
//					initSettings();
//					Toast.makeText(StudyDataMenuActivity.this,path + " "+databaseName, Toast.LENGTH_SHORT).show();
				}
				break;
			case ACTIVITY_CONNECT_DEVICE:
				databaseName=extras.getString("DBNAME");
				connetionState = extras.getInt("CONNECTION_STATE");
				currentDevice = extras.getString("CURRENT_DEVICE");
				Toast.makeText(StudyDataMenuActivity.this,String.valueOf(connetionState), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	@Override
	public void onMediaScannerConnected() {
		// TODO Auto-generated method stub
//		SCAN_PATH=Environment.getExternalStorageDirectory().toString()+FieldLabPath.IMAGE_PATH+databaseName+"/"+allFiles[0];
		SCAN_PATH=FieldLabPath.IMAGE_FOLDER+databaseName+"/"+allFiles[0];
		conn.scanFile(SCAN_PATH, FILE_TYPE);  
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		// TODO Auto-generated method stub
		try {
			if (uri != null) 
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);
				startActivity(intent);
			}
		} finally 
		{
			conn.disconnect();
			conn = null;
		}
	}
	
	private void setDatabaseConnection(String path){
		if (databaseName!= null && !databaseName.equals("") && !databaseName.contains("Type the complete path here.") && !databaseName.contains(".")) {
			DatabaseConnectionInstance databaseConnection= new DatabaseConnectionInstance();
			DatabaseConnect databaseConnect = new DatabaseConnect(StudyDataMenuActivity.this, databaseName);
			String status = "OK";
			try {
				// Creates the icismobile database file in the SD Card if it does not exist.
				// If it does exist, it will open that database for querying.
				databaseConnect.createDataBase();
				databaseConnection.savePath(openFileOutput("ConnectionProperties.txt",MODE_WORLD_READABLE),path);
				databaseConnect.close();

				//TODO: Progress bar
				Toast.makeText(StudyDataMenuActivity.this, "Study " + databaseName + " is now ready to use",
						Toast.LENGTH_SHORT).show();

			} catch (IOException e) {
				status = "FAIL";
				e.printStackTrace();
				
			}

		} else {
			Toast.makeText(StudyDataMenuActivity.this, "Please enter a valid file!",
					Toast.LENGTH_LONG).show();
		}
	}

}