package org.irri.fieldlab.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.utility.DatabaseConnectionInstance;
import org.irri.fieldlab.utility.FieldLabPath;
import org.irri.fieldlab.utility.FileManager;
import org.irri.fieldlab.utility.XLSManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	//	private final static String DB_ERROR = "ICISMobileDatabaseError";

	private String DBNAME = "";
	private static final int ACTIVITY_CONNECT=0;
	private static final int ACTIVITY_IMPORT=1;
	private DatabaseConnect dbHelper = new DatabaseConnect(this);


	private String databaseName;
	private DatabaseConnectionInstance databaseConnection;
	private ImageView btnExit;
	private ImageView btnOpenStudy;
	private ImageView btnStudyMenu;
	private ImageView btnImportWorkbook;
	private ImageView btnCreateFieldBook;
	private ImageView btnMaintenance;
	private ImageView btnHelp;
	private ImageView btnAbout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		FileManager fileManager= new FileManager();
		try {
			fileManager.createFieldLabFolders(getAssets());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		btnOpenStudy = (ImageView) findViewById(R.id.btnOpenStudy);
		btnStudyMenu = (ImageView) findViewById(R.id.btnStudyMenu);
		btnCreateFieldBook = (ImageView) findViewById(R.id.btnCreateFieldbook);
		btnImportWorkbook = (ImageView) findViewById(R.id.btnImportWorkbook);
		//		btnMaintenance = (ImageView) findViewById(R.id.btnMaintenance);
		btnHelp = (ImageView) findViewById(R.id.btnHelp);
		btnAbout = (ImageView) findViewById(R.id.btnAbout);
		btnExit = (ImageView) findViewById(R.id.btnExit);

		btnStudyMenu.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks

				openStudy();

			}
		});


		btnImportWorkbook.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks

				Intent intentImport = new Intent(MainActivity.this, ImportWorkbookActivity.class);
				startActivityForResult(intentImport, ACTIVITY_IMPORT);


			}
		});

				btnCreateFieldBook.setOnClickListener(new View.OnClickListener() {
		
					public void onClick(View v) {
						//startActivity(new Intent(MainActivity.this,CreateFieldBookStudyMainActivity.class));
					}
				});



		btnHelp.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks

//				startActivity(new Intent(MainActivity.this,HelpActivity.class));
//				startActivity(new Intent(MainActivity.this,AudioRecordTest.class));
				Intent intent=XLSManager.openPowerpoint(FieldLabPath.MAINTENANCE_FOLDER+"FieldLab Guide.ppt");
				startActivity(intent);
			}
		});
		btnHelp.setEnabled(true);

		btnAbout.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,AboutActivity.class));
			}
		});

		btnExit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
			    if (!mBluetoothAdapter.isEnabled()) {

			    }else{ 
			        mBluetoothAdapter.disable(); 
			    } 
				finish();
			}
		});


	}

	protected void openStudy() {
		Intent i = new Intent(MainActivity.this, StudyDataMenuActivity.class);
		try {
			getExistingDatabaseConnection();
			i.putExtra("DBNAME", getDBNAME());
			startActivity(i);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private boolean getExistingDatabaseConnection() throws FileNotFoundException {
		// TODO Auto-generated method stub
		try{
			FileInputStream fIn = null;
			databaseConnection = new DatabaseConnectionInstance();
			String databasePath;
			fIn = openFileInput("ConnectionProperties.txt");
			databasePath=databaseConnection.findPath(fIn);
			String dbFile[]=databasePath.split("/");
			databaseName=dbFile[dbFile.length-1];
			setDBNAME(databaseName);
			return true;
		}catch(FileNotFoundException e){
			//			setDBNAME("sample");
			return false;
		}catch(Exception e){
			return false;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent != null) {

			Bundle extras = intent.getExtras();

			switch(requestCode) {
			case ACTIVITY_CONNECT:
				setDBNAME(extras.getString("DBNAME"));
				String status = extras.getString("DBSTATUS");

				if (status.equals("OK")) setButtonsEnabled(true);
				else setButtonsEnabled(false);

				break;
			case ACTIVITY_IMPORT:
				try{
					setDBNAME(extras.getString("DBNAME"));
					if(extras.getString("STATUS").equals("OK")){
						openStudy();
					}
				}catch(Exception e){

				}

				break;
			}


		}

	}


	//	@Override
	//	protected void onSaveInstanceState(Bundle outState) {
	//		// Save the database name
	//		super.onSaveInstanceState(outState);
	//		outState.putSerializable("DBNAME", getDBNAME());
	//	}

	@Override
	public void finish() {
		dbHelper.close();
		super.finish();
	}

	private void setButtonsEnabled(boolean bool) {
		btnStudyMenu.setEnabled(bool);
	}

	public String getDBNAME() {
		return DBNAME;
	}

	public void setDBNAME(String dBNAME) {
		DBNAME = dBNAME;
	}





}