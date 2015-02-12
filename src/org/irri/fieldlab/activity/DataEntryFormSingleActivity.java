package org.irri.fieldlab.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.database.manager.ObservationManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.database.manager.SettingsManager;
import org.irri.fieldlab.utility.DataEntryUtil;
import org.irri.fieldlab.utility.FieldLabPath;
import org.irri.fieldlab.utility.ImageManager;
import org.irri.fieldlab.utility.Validation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.baracoda.android.baracodamanager.DataType;
import com.baracoda.android.baracodamanager.IBaracodaReaderService;
import com.baracoda.android.baracodamanager.IBaracodaReaderServiceCallback;

public class DataEntryFormSingleActivity extends Activity{
	private LinearLayout linearLayoutVariateDataEntry;
	private LinearLayout linearLayoutFactorEntry;
	private List<EditText> allEds = new ArrayList<EditText>();
	private ArrayList<String> observationColumnMetaData;
	private ArrayList<String> observationData;
	private Button btnClose;
	private LinearLayout linearLayoutFactorReference;
	private LinearLayout linearLayoutVariateReference;
	private Button btnNext;
	private Button btnTakePhoto;
	private String databaseName;
	private DatabaseConnect databaseConnect;
	private ObservationManager observationManager;
	private Cursor cursorObservationData;
	private int recId;
	private Button btnPrevious;
	private int totalRecord=0;
	private int request_CodeTrait=0;
	private int ACTIVITY_REMARKS=1;
	private Button btnSearch;
	private SettingsManager settingsManager;
	private DescriptionManager descriptionManager;
	private Validation validation;
	private ScoringManager scoringManager;

	protected int currentEditTextId;
	protected int oldEditTextId;
	protected boolean isValidRange=false;;
	protected boolean validScore=false;
	protected boolean hasScoring=false;
	private String barcodeReference;
	private FieldLabManager fieldLabManager;
	private String studyName;
	private static final int ACTIVITY_CALENDAR=3;
	private static final int PHOTO_CAPTURE=4;
	private static final int ACTIVITY_SCORING_MORPHO=6;
	private static final int AUDIO_CAPTURE_ACTIVITY=7;
	private String currentSelectedTrait;
	private String dateValue;
	private static int TEXT_SIZE=30;

	private int txtSearchIsFocus=1;
	private static final int ACTIVITY_CONNECT_DEVICE=5;
	private String currentDevice="";
	private int connetionState=0;
	private String valuePlotNo="";
	private static final Pattern numberPattern = Pattern.compile( "(-|\\+)?[0-9]+(\\.[0-9]+)?");
	private String oldTextValue;




	/**** Baracoda variables ****/
	// Messages received from the Baracoda service and forwarded to the application's handler
	// A new reader has been selected/connected
	private static final int MESSAGE_ON_READER_CHANGED = 1;
	// Reader connected/disconnected or connecting
	private static final int MESSAGE_ON_CONNECTION_STATE_CHANGED = 2;
	// Autoconnect activated/deactivated
	private static final int MESSAGE_ON_AUTOCONNECT_STATE_CHANGED = 3;
	// Data received
	private static final int MESSAGE_ON_DATA_READ = 4;

	// Debugging tag
	private static final String TAG = "BaracodaSDKExample";

	// Activity actions
	private static final int ACTION_REQUEST_SEARCH_DEVICES_RESULT = 1;
	private static final int ACTION_REQUEST_ENABLE_BT_RESULT = 2;
	private static final int DATA_ENTRY_SETTINGS = 3;

	// Return intent extra for SelectDeviceActivity
	public static String EXTRA_DEVICE = "device";
	private static DateFormat sdf;

	// Bluetooth
	private BluetoothAdapter bluetoothAdapter = null;
	private boolean btStackAtStartup = false;

	// Baracoda service to which we connect
	private IBaracodaReaderService baracodaService = null;

	// Initial service state
	private boolean applicationAckAtStartup = false;
	private boolean rawModeAtStartup = false;
	private EditText txtSearchValue;
	private String currentDataTypeSelected;
	private String displayOrder="ASC";
	private Button btnViewImage;
	private Button btnAudioFile;
	private TextView lblImageCount;
	private TextView txtTraitDescription;
	private int imageCount=0;
	private int audioCount=0;
	private Button btnAudioCapture;
	private TextView lblAudioCount;
	private int lastRecordId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataentryformsingle);

		//		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


		linearLayoutFactorEntry=(LinearLayout) findViewById(R.id.LinearLayoutFactorEntry);
		linearLayoutVariateDataEntry=(LinearLayout) findViewById(R.id.LinearLayoutDataEntry);
		

		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}

		init();

		recId=settingsManager.getLastRecord();
		totalRecord=getObservationTotalRec();
		observationColumnMetaData=getObsevationColumnActive(); 
		setObservationData(recId);

		btnClose = (Button)findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
				if (!mBluetoothAdapter.isEnabled()) {

				}else{ 
					mBluetoothAdapter.disable(); 
				} 
				finish();
			}
		});
		txtTraitDescription=(TextView) findViewById(R.id.txtTraitDescriptionSingle);
		txtSearchValue =(EditText) findViewById(R.id.txtSearchValue);
		txtSearchValue.setInputType(3);
		txtSearchValue.setHint(barcodeReference);
		txtSearchValue.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					txtSearchIsFocus=1;
					btnAudioCapture.setEnabled(false);
					btnTakePhoto.setEnabled(false);
					//					Toast.makeText(getBaseContext(),"has focus", Toast.LENGTH_SHORT).show();
				}else{
					txtSearchIsFocus=0;
					btnAudioCapture.setEnabled(true);
					btnTakePhoto.setEnabled(true);
					//					Toast.makeText(getBaseContext(),"has no focus", Toast.LENGTH_SHORT).show();
				}
			}
		});


		txtSearchValue.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if(txtSearchValue.length() > 0 ){
						searchRecord(txtSearchValue.getText().toString());
					}
					return true;
				}
				else {
					return false;
				}
			}
		});


		btnSearch = (Button)findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(barcodeReference.equals("")){
					showDialog(3, null);
				}else{
					searchRecord(txtSearchValue.getText().toString());
					//					txtSearchValue.setText(null);
				}
			}


		});


		btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!barcodeReference.equals("")){

					Calendar now = GregorianCalendar.getInstance();
					String photReferenceCode=observationManager.getReferenceCode(barcodeReference,String.valueOf(recId));
					saveRecord();
					Intent iPhoto = new Intent(DataEntryFormSingleActivity.this,PhotoCaptureActivity.class);
					iPhoto.putExtra("DBNAME", databaseName);
					String photoName=databaseName+"_"+photReferenceCode+"_"+currentSelectedTrait;
					iPhoto.putExtra("PHOTO_NAME",photoName );
					startActivityForResult(iPhoto, PHOTO_CAPTURE);
					//	Toast.makeText(getBaseContext(),photoName, Toast.LENGTH_SHORT).show();
				}else{
					//				Toast.makeText(getBaseContext(),"To use this features please first specify the Observation Sheet Unique Column in Setting Menu", Toast.LENGTH_SHORT).show();
					showDialog(4, null);
				}

			}


		});

		btnAudioCapture = (Button)findViewById(R.id.btnRecordAudio);
		btnAudioCapture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!barcodeReference.equals("")){
					String audioReferenceCode=observationManager.getReferenceCode(barcodeReference,String.valueOf(recId));
					saveRecord();
					Intent iAudio = new Intent(DataEntryFormSingleActivity.this,AudioRecordingActivity.class);
					iAudio.putExtra("DBNAME", databaseName);
					String audioName=databaseName+"_"+audioReferenceCode+"_"+currentSelectedTrait;
					iAudio.putExtra("AUDIO_NAME",audioName );
					iAudio.putExtra("CURRENTTEXTID",currentEditTextId );
					iAudio.putExtra("AUDIOREFERENCECODE",audioReferenceCode );
					startActivityForResult(iAudio, AUDIO_CAPTURE_ACTIVITY);
					//	Toast.makeText(getBaseContext(),photoName, Toast.LENGTH_SHORT).show();
				}else{
					//				Toast.makeText(getBaseContext(),"To use this features please first specify the Observation Sheet Unique Column in Setting Menu", Toast.LENGTH_SHORT).show();
					showDialog(4, null);
				}

			}
		});

		btnNext = (Button)findViewById(R.id.btnNext);
		btnNext.setOnClickListener(nextListener);
		//		btnNext.setOnFocusChangeListener(focuListener);

		btnPrevious = (Button)findViewById(R.id.btnPrevious);
		btnPrevious.setOnClickListener(previousListener);
		//		btnPrevious.setOnFocusChangeListener(focuListener);

		createVariateLayout(observationColumnMetaData.size());
		setTitle("Observation Data: "+studyName);


		btnViewImage = (Button)findViewById(R.id.btnViewImage);
		btnViewImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DataEntryFormSingleActivity.this, ImageListViewActivity.class);
				i.putExtra("FOLDER_PATH",FieldLabPath.IMAGE_FOLDER+databaseName);
				i.putExtra("PHOTOREFERENCECODE", valuePlotNo);
				i.putExtra("DBNAME", databaseName);
				i.putExtra("CURRENTTEXTID",currentEditTextId );
				startActivity(i);
			}


		});

		btnAudioFile = (Button)findViewById(R.id.btnAudioFile);
		btnAudioFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String audioReferenceCode=observationManager.getReferenceCode(barcodeReference,String.valueOf(recId));
				saveRecord();
				Intent iAudio = new Intent(DataEntryFormSingleActivity.this,AudioRecordingActivity.class);
				iAudio.putExtra("DBNAME", databaseName);
				String audioName=databaseName+"_"+audioReferenceCode+"_"+currentSelectedTrait;
				iAudio.putExtra("AUDIO_NAME",audioName );
				iAudio.putExtra("CURRENTTEXTID",currentEditTextId );
				iAudio.putExtra("AUDIOREFERENCECODE",audioReferenceCode );
				startActivityForResult(iAudio, AUDIO_CAPTURE_ACTIVITY);
			}


		});

		lblImageCount = (TextView)findViewById(R.id.lblImageCount);
		lblAudioCount=(TextView)findViewById(R.id.lblAudioCount);

		displayImageAndAudioButton();
	}

	private void displayImageAndAudioButton() {

		imageCount= new ImageManager().getImageCount(FieldLabPath.IMAGE_FOLDER+databaseName, valuePlotNo);
		audioCount=new ImageManager().getAudioCount(FieldLabPath.AUDIO_FOLDER+databaseName, valuePlotNo);

		if(imageCount > 0){
			btnViewImage.setVisibility(View.VISIBLE);
			lblImageCount.setVisibility(View.VISIBLE);
			lblImageCount.setText("( "+String.valueOf(imageCount)+" )");
		}else{
			btnViewImage.setVisibility(View.GONE);
			lblImageCount.setVisibility(View.GONE);
		}

		if(audioCount > 0){
			btnAudioFile.setVisibility(View.VISIBLE);
			lblAudioCount.setVisibility(View.VISIBLE);
			lblAudioCount.setText("( "+String.valueOf(audioCount)+" )");
		}else{
			btnAudioFile.setVisibility(View.GONE);
			lblAudioCount.setVisibility(View.GONE);
		}


	}

	private void startBarcodaService(){
		//		Toast.makeText(getBaseContext(),"Start Baracoda Service Start", Toast.LENGTH_SHORT).show();
		Intent baracodaServiceIntent = new Intent (IBaracodaReaderService.class.getName());
		// Start the service if not already running
		super.startService(baracodaServiceIntent);
		// Bind to the service
		super.bindService(baracodaServiceIntent, this.baracodaServiceConnection, Context.BIND_AUTO_CREATE);
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	protected void searchRecord(String valueBarcode) {
		//		Toast.makeText(getBaseContext(),"String search value  " +String.valueOf(valueBarcode.trim()), Toast.LENGTH_SHORT).show();
		saveRecord();
		String[] valueSearch=txtSearchValue.getText().toString().split(";");
		String searchString;
		if (valueSearch.length > 1){
			searchString=valueSearch[valueSearch.length-1];
		}else{
			searchString=valueSearch[0];
		}
		
		System.out.println(valueBarcode);
		int newRecId=observationManager.searchRecordByField(barcodeReference,searchString.trim());

		if(newRecId==0){
			txtSearchValue.setText(null);
			Toast.makeText(getBaseContext(),"Record Not Found...", Toast.LENGTH_SHORT).show();
		}else{
			recId=newRecId;
			updateDisplayEntry();
		}
		//		txtSearchValue.setText(null);
		txtSearchValue.requestFocus();
		txtSearchIsFocus=1;
	}

	private void init() {

		databaseConnect= new DatabaseConnect(DataEntryFormSingleActivity.this, databaseName);
		databaseConnect.openDataBase();
		fieldLabManager= new FieldLabManager(databaseConnect.getDataBase());
		observationManager=new ObservationManager(databaseConnect.getDataBase());
		settingsManager=new SettingsManager(databaseConnect.getDataBase());
		descriptionManager=new DescriptionManager(databaseConnect.getDataBase());
		scoringManager=new ScoringManager(databaseConnect.getDataBase());
		validation= new Validation();
		studyName=descriptionManager.getStudyName();
		barcodeReference=settingsManager.getBarcodeReference();
	}

	private void createVariateLayout(int count){

		if(allEds.size() > 0){
			allEds.clear();
		}

		linearLayoutFactorReference = new LinearLayout(this); // layout for dataentry
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
//		linearLayoutFactorReference.setLayoutParams(params);
//		linearLayoutFactorReference.setOrientation(LinearLayout.VERTICAL);

		linearLayoutFactorReference.addView(tableLayout(count,"Factor"));
		linearLayoutFactorEntry.addView(linearLayoutFactorReference);
		
		
		linearLayoutVariateReference = new LinearLayout(this); // layout for dataentry
//		ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams. WRAP_CONTENT);
//		linearLayoutVariateReference.setLayoutParams(params2);
		linearLayoutVariateReference.setOrientation(LinearLayout.VERTICAL);

		linearLayoutVariateReference.addView(tableLayout(count,"Variate"));
		linearLayoutVariateDataEntry.addView(linearLayoutVariateReference);
		
		linearLayoutFactorReference.invalidate();
		linearLayoutVariateReference.invalidate();

	}


	private View.OnClickListener nextListener = new View.OnClickListener() {
		public void onClick(View view) {
			if(recId==totalRecord && displayOrder.equals("ASC")){
				Toast.makeText(getBaseContext(),"End of record...", Toast.LENGTH_SHORT).show();
			}else{
				saveRecord();
				if(displayOrder.equals("ASC")){
					recId++;
				}else{
					if(recId!=1){
						recId--;
					}else{
						Toast.makeText(getBaseContext(),"Cannot go to any record, changed to Ascending Order", Toast.LENGTH_SHORT).show();
					}
				}
				updateDisplayEntry();
			}

		}
	};

	private View.OnClickListener previousListener = new View.OnClickListener() {
		public void onClick(View view) {

			if(recId == 1){
				Toast.makeText(getBaseContext(),"Beginning of record...", Toast.LENGTH_SHORT).show();
			}else{
				saveRecord();
				if(displayOrder.equals("ASC")){
					recId--;
				}else{
					recId++;
				}
				updateDisplayEntry();
			}
		}
	};




	// Using a TableLayout as it provides you with a neat ordering structure

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return MenuChoice(item);
	}



	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		return MenuChoice(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		observationColumnMetaData.clear();
		observationColumnMetaData=getObsevationColumnActive();
		init();
		updateDisplayEntry();
		int prevID;
		EditText txtEditCurrent;
		String score; 

		if (intent != null) {
			Bundle extras = intent.getExtras();

			switch (requestCode) {
			case 0:
				break;
			case 1:
				String remarks=extras.getString("REMARKS");
				int oldEditTextId=extras.getInt("CURRENT_EDITTEXT_ID");
				EditText editText = (EditText) findViewById(oldEditTextId);
				editText.setText(remarks);
				break;

			case ACTIVITY_CONNECT_DEVICE:
				databaseName=extras.getString("DBNAME");
				connetionState = extras.getInt("CONNECTION_STATE");
				currentDevice = extras.getString("CURRENT_DEVICE");
				startBarcodaService();
				//				Toast.makeText(DataEntryFormSingleActivity.this,String.valueOf(connetionState), Toast.LENGTH_SHORT).show();
				break;

			case ACTIVITY_CALENDAR:
				sdf = new SimpleDateFormat("yyyy/MM/dd"); 
				prevID=extras.getInt("CURRENT_EDITTEXT_ID");
				String prevDtype=extras.getString("DTYPE");
				//				Toast.makeText(getBaseContext(),"Current ID  " +String.valueOf(prevID), Toast.LENGTH_SHORT).show();

				txtEditCurrent = (EditText) findViewById(prevID);
				dateValue=extras.getString("DATESELECTED");
				if(prevDtype.equals("CD")){
					txtEditCurrent.setText(dateValue);
				}else{
					String sDate=descriptionManager.getStudyStartDate();

					String d=sDate.substring(4, 6)+"/"+sDate.substring(6,8)+"/"+sDate.substring(0, 4);
					Date d1 = new Date(d);  
					Date d2 = new Date(dateValue); 

					Calendar cal1 = Calendar.getInstance();cal1.setTime(d1);  
					Calendar cal2 = Calendar.getInstance();cal2.setTime(d2);
					long noOfDays=daysBetween(cal1, cal2);
					txtEditCurrent.setText(String.valueOf(noOfDays));

				}
				txtEditCurrent.requestFocus();
				break;


			case ACTIVITY_SCORING_MORPHO:

				prevID=extras.getInt("CURRENTTEXTID");
				score = extras.getString("SCORE_SELECTED");
				txtEditCurrent = (EditText) findViewById(prevID);
				txtEditCurrent.setText(score.trim());
				txtEditCurrent.requestFocus();
				break;

			case PHOTO_CAPTURE:

				txtEditCurrent = (EditText) findViewById(currentEditTextId);
				txtEditCurrent.requestFocus();
				break;
			}
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		saveRecord();
		settingsManager.updateLastRecord(recId);
		databaseConnect.close();
		finish();
	}

	private void CreateMenu(Menu menu)
	{
		MenuItem mnu1 = menu.add(0, 0, 0, "Factor and Variate");
		{
			mnu1.setIcon(R.drawable.trait_list_icon);
		}
		//		MenuItem mnu2 = menu.add(0, 1, 1, "Ranges Input");
		//		{
		//			mnu2.setIcon(R.drawable.edit);
		//		}

		MenuItem mnu3 = menu.add(0, 2, 1, "Sort ASC");
		{
			mnu3.setIcon(R.drawable.asc);
		}
		MenuItem mnu4 = menu.add(0, 3, 1, "Sort DESC");
		{
			mnu4.setIcon(R.drawable.desc);
		}

		//		MenuItem mnu5 = menu.add(0, 4, 1, "Take Photo");
		//		{
		//			mnu5.setIcon(R.drawable.camera);
		//		}

		MenuItem mnu6 = menu.add(0, 4, 1, "Connect To Reader");
		{
			mnu6.setIcon(R.drawable.baracoda);
		}

	}

	private boolean MenuChoice(MenuItem item)
	{
		switch (item.getItemId()) {
		case 0:
			saveRecord();
			Intent i1 = new Intent(DataEntryFormSingleActivity.this,DescriptionFactorVariateActivity.class);
			i1.putExtra("DBNAME", databaseName);
			startActivityForResult(i1, request_CodeTrait);
			return true;
		case 1:
			//			Intent i2 = new Intent(DataEntryStudyActivity.this, SettingsActivity.class);
			//			i2.putExtra("INPUT_REF", sBarCode + " Input");
			//			i2.putExtra("DBNAME", getDB_NAME());
			//			i2.putExtra("chkAutoLookup", autoPrompt);
			//			i2.putExtra("connectedDevice", connectedDevice);
			//			i2.putExtra("connectionLabel",connectionLabel);
			//			i2.putExtra("connectionState",connectionState);
			//			startActivityForResult(i2, request_Code);
			return true;

		case 2:
			displayOrder="ASC";
			saveRecord();
			allEds.clear();
			linearLayoutVariateDataEntry.removeAllViewsInLayout();
			linearLayoutFactorEntry.removeAllViewsInLayout();
			updateDisplayEntry();
			return true;

		case 3:
			displayOrder="DESC";
			saveRecord();
			allEds.clear();
			linearLayoutVariateDataEntry.removeAllViewsInLayout();
			linearLayoutFactorEntry.removeAllViewsInLayout();
			updateDisplayEntry();
			return true;

		case 4:
			Intent i = new Intent(DataEntryFormSingleActivity.this,BarcodeScannerConnectActivity.class);
			i.putExtra("CONNECTION_STATE", connetionState );
			i.putExtra("CURRENT_DEVICE", currentDevice );
			i.putExtra("DBNAME", databaseName);
			startActivityForResult(i, ACTIVITY_CONNECT_DEVICE);
			return true;
		}
		return false;
	}

	private TableRow createOneFullRow(int rowId,String category) {
		TableRow tableRow = new TableRow(this);
//		tableRow.setPadding(0, 10, 0, 0);

		String[] s = observationColumnMetaData.get(rowId).split(":");
		
		if(s[2].equals("textfield") && category.equals("Variate")){
			tableRow.setPadding(0, 10, 0, 0);
			tableRow.addView(labelVariate(s[0],s[2]));
			tableRow.addView(editTextVariate(rowId,s[1],observationData.get(rowId) ));

		}
		if(s[2].equals("label") && category.equals("Factor")){
			tableRow.setPadding(0, 10, 0, 0);
			tableRow.addView(labelVariate(s[0],s[2]));
			tableRow.addView(txtViewVariate(observationData.get(rowId),s[0]));
		}

		return tableRow;
	}

	// Using a TableLayout as it provides you with a neat ordering structure

	private TableLayout tableLayout(int count,String category) {
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setPadding(10, 5, 10, 5);
		tableLayout.setStretchAllColumns(true);
		//int noOfRows = count / 2;
		int noOfRows = count;
		for (int i = 0; i < noOfRows; i++) {
			int rowId = i;
			tableLayout.addView(createOneFullRow(rowId,category));
		}
		return tableLayout;
	}


	// Using a TableLayout as it provides you with a neat ordering structure

	private EditText editTextVariate(final int rowID,String hint,String value) {

		EditText editText = new EditText(DataEntryFormSingleActivity.this);
		allEds.add(editText);
		editText.setId(Integer.valueOf(rowID));
		//		editText.setHint(hint);
		editText.setText(value);
		editText.setTextSize(22);
		editText.setEms(2);
		editText.setSingleLine();
		editText.setPadding(10, 0, 0, 0);
		lastRecordId=rowID;

		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				EditText editText;
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					moveToNextEntry();
					return true;
				}
				else {
					return false;
				}

			}
		});

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				String[] s = observationColumnMetaData.get(rowID).split(":");
				String dtype=s[1];
				String traitcode=s[0];
				currentSelectedTrait=traitcode;
				currentEditTextId=((EditText) v).getId();
				String editTextValue=String.valueOf(((EditText) v).getText());
				EditText editTextPrevValue = (EditText) findViewById(oldEditTextId);
				txtSearchIsFocus=0;
				
				if(hasFocus) {
					oldTextValue=String.valueOf(((EditText) v).getText());
//					Toast.makeText(getBaseContext(),"OldTextValue"+ oldTextValue, Toast.LENGTH_SHORT).show();
					((EditText) v).setTextSize(40);
					hasScoring=scoringManager.hasScoring(traitcode);
					currentDataTypeSelected=dtype;
					if(hasScoring && settingsManager.autoPromptScoring()){
						showScoringDialog(DataEntryUtil.getScoring(traitcode, scoringManager), traitcode, currentEditTextId);
					}


					//					if(!hasScoring && settingsManager.autoPromptScoring() && (dtype.equals("D") || dtype.equals("CD"))){
					//							Intent intentCalendar = new Intent(DataEntryFormSingleActivity.this, CalendarViewActivity.class);
					//							startActivityForResult(intentCalendar, ACTIVITY_CALENDAR);
					//					}
					String traitDescription=descriptionManager.getDescription(currentSelectedTrait);
//					Toast.makeText(getBaseContext(),traitDescription, Toast.LENGTH_SHORT).show();
					txtTraitDescription.setText("Measuring : "+traitDescription);

					DataEntryUtil.setEditTextInputType(dtype,v);

				}else{
					try{
						((EditText) v).setTextSize(TEXT_SIZE);
						oldEditTextId=currentEditTextId;
						oldTextValue=oldTextValue;

						EditText editText = (EditText) findViewById(currentEditTextId);
						String newTextValue=editText.getText().toString();

						if(!isNumber(editTextValue) && dtype.equals("D")){
							EditText editTextPrev1 = (EditText) findViewById(oldEditTextId);
							editTextPrev1.setText("");
							editTextPrev1.requestFocus();
						}
						
						
						
						
//						if(!currentTextValue.equals("") && !currentTextValue.equals(newTextValue) ){
//							removeDialog(0);
//							Bundle args = new Bundle();
//							args.putString("currentTextValue",currentTextValue);
//							showDialog(5, args);
//						}
						
						if(!oldTextValue.equals(editTextValue) && oldTextValue.length() > 0)
						{
//							Toast.makeText(getBaseContext(),"OldValue: "+oldTextValue, Toast.LENGTH_SHORT).show();
							EditText editTextPrev1 = (EditText) findViewById(oldEditTextId);
							String currentTextValue2=editTextPrev1.getText().toString();
							Bundle args = new Bundle();
							args.putInt("currentEditTextId",currentEditTextId);
							args.putString("oldTextValue",oldTextValue);
							args.putString("currentTextValue",currentTextValue2);
							removeDialog(5);
							showDialog(5, args);
						}
						
						if(editTextValue.length() > 0 && !dtype.equals("C")){
							isValidRange=validation.isValidRange(editTextValue, traitcode, descriptionManager);
							validScore=validation.isValidScore(editTextValue, traitcode, scoringManager);
							hasScoring=scoringManager.hasScoring(traitcode);

							//Toast.makeText(getBaseContext(),"editTextValue: "+ editTextValue , Toast.LENGTH_SHORT).show();

							if(!validScore && hasScoring ){

								DataEntryUtil.setEditTextRed(editText);

								Bundle args = new Bundle();
								args.putString("scoring",DataEntryUtil.getScoring(traitcode, scoringManager));
								args.putInt("currentEditTextId",currentEditTextId);
								removeDialog(0);
								showDialog(0, args);
							}
							
							

							if(!isValidRange){
								DataEntryUtil.setEditTextRed(editText);

								String validRange=validation.getValidRange(traitcode, descriptionManager);
								Bundle args = new Bundle();
								args.putString("validRange",validRange);
								args.putInt("currentEditTextId",currentEditTextId);
								removeDialog(1);
								showDialog(1, args);
							}
							//						Toast.makeText(getBaseContext(),oldEditTextValue + " " +currentTextValue  , Toast.LENGTH_SHORT).show();
							//						if(!oldEditTextValue.equals("") && !oldEditTextValue.equals(currentTextValue) ){
							//							Toast.makeText(getBaseContext(),"Ok to edit "+ editTextValue , Toast.LENGTH_SHORT).show();
							//						}else{
							//							Toast.makeText(getBaseContext(),"Need to validate"+ editTextValue , Toast.LENGTH_SHORT).show();
							//						}

							if(isValidRange || validScore){
								DataEntryUtil.setEditTextBlack(editText);
							}
						}
					}catch(Exception e){

					}

				}
			}
		});

		final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

			public boolean onDoubleTap(MotionEvent e) {
				String[] s = observationColumnMetaData.get(rowID).split(":");
				String dtype=s[1];
				String traitcode=s[0];

				boolean hasScoring=scoringManager.hasScoring(traitcode);

				if(hasScoring){
					String scores=DataEntryUtil.getScoring(traitcode, scoringManager);

					if(scores.contains(".jpg")){
						saveRecord();
						Intent i = new Intent(DataEntryFormSingleActivity.this, ScoringMorphoActivity.class);
						//					Toast.makeText(getBaseContext(),"Value to PASS "+String.valueOf(currentEditTextId), Toast.LENGTH_SHORT).show();
						i.putExtra("CURRENTTEXTID",currentEditTextId);
						i.putExtra("SCORES",scores);
						i.putExtra("TRAITDESCRIPTION",txtTraitDescription.getText().toString().split(":")[1]);
						
						startActivityForResult(i, ACTIVITY_SCORING_MORPHO);
					}else{
						showScoringDialog(scores, traitcode, currentEditTextId);
					}
				}

				if(traitcode.toLowerCase().contains("remark") || traitcode.toLowerCase().contains("comment") ){
					saveRecord();
					EditText editText = (EditText) findViewById(currentEditTextId);
					Intent i1 = new Intent(DataEntryFormSingleActivity.this,RemarksActivity.class);
					i1.putExtra("DBNAME", databaseName);
					i1.putExtra("REMARKS", editText.getText().toString());
					i1.putExtra("CURRENTTEXTID",currentEditTextId);
					startActivityForResult(i1, ACTIVITY_REMARKS);
				}

				if(dtype.equals("D") || dtype.equals("CD")){
					saveRecord();
					Intent i = new Intent(DataEntryFormSingleActivity.this, CalendarViewActivity.class);
					//					Toast.makeText(getBaseContext(),"Value to PASS "+String.valueOf(currentEditTextId), Toast.LENGTH_SHORT).show();
					i.putExtra("CURRENTTEXTID",currentEditTextId);
					i.putExtra("DTYPE",dtype);
					startActivityForResult(i, ACTIVITY_CALENDAR);
				}
				return true;
			}
		});

		editText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		});

		return editText;
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


	// Using a TableLayout as it provides you with a neat ordering structure

	private TextView txtViewVariate(String txtValue,String refPlot){
		TextView txtView=new TextView(this);
		txtView.setTextColor(Color.BLACK);
		txtView.setText(txtValue);
		if(refPlot.equals(barcodeReference)){
			txtView.setTextSize(60);
			valuePlotNo=txtValue;
		}else{
			txtView.setTextSize(22);
		}
		txtView.setPadding(10, 0, 0, 0);
		return txtView;
	}

	// Using a TableLayout as it provides you with a neat ordering structure

	private TextView labelVariate(String txtValue,String inputType){
		TextView txtView=new TextView(this);

		if(txtValue.equals("REC #")){
			txtView.setTextColor(Color.BLACK);
			txtView.setText(" "+txtValue);
			txtView.setTextSize(22);
		}else{

			txtView.setTextColor(Color.WHITE);
			txtView.setText(" "+txtValue);
			if(inputType.equals("textfield")){
				txtView.setBackgroundColor(Color.rgb(0, 0, 128));
				txtView.setTextSize(22);

			}else{
				txtView.setBackgroundColor(Color.rgb(0, 100, 0));
				txtView.setTextSize(22);
			}

		}
		return txtView;
	}


	private void setObservationData(int recId) {
		// TODO Auto-generated method stub

		cursorObservationData= observationManager.getAllObservationData(getFieldObservation(),getObservationVariateActive(),recId,"N");
		cursorObservationData.moveToFirst();
		getObservationDataPerSingleRecord(cursorObservationData);
	}

	private int getObservationTotalRec() {
		Cursor cursorCount=observationManager.countAllObservationRecord();
		return Integer.valueOf(cursorCount.getString(0));
	}

	private ArrayList<String> getObsevationColumnActive() {

		Cursor cursorObservationFieldActive=descriptionManager.getObservationSingleView();

		observationColumnMetaData=new ArrayList<String>();
		observationColumnMetaData.add("REC #:C:label");

		cursorObservationFieldActive.moveToFirst();

		do {
			String line = cursorObservationFieldActive.getString(0)+ ":"+ cursorObservationFieldActive.getString(1) + ":"+cursorObservationFieldActive.getString(2);
			observationColumnMetaData.add(line);
		} while (cursorObservationFieldActive.moveToNext());

		return observationColumnMetaData;
	}

	private String getFieldObservation() {

		Cursor cursorObservationFieldActive=descriptionManager.getObservationSingleView();
		String fieldObservation="";

		cursorObservationFieldActive.moveToFirst();

		do {
			String line = "`"+cursorObservationFieldActive.getString(0)+"`"+ ":"+ cursorObservationFieldActive.getString(1) + ":"+cursorObservationFieldActive.getString(2);
			fieldObservation+="`"+cursorObservationFieldActive.getString(0)+"`"+","; // to know the field to query in the observation table
		} while (cursorObservationFieldActive.moveToNext());

		fieldObservation=fieldObservation.substring(0,fieldObservation.length()-1);
		//			Toast.makeText(getBaseContext(),fieldObservation, Toast.LENGTH_SHORT).show();

		return fieldObservation;
	}

	private ArrayList<String> getObservationVariateActive() {

		Cursor cursorObservationFieldActive=descriptionManager.getObservationVariateActive();
		ArrayList<String> ova=new ArrayList<String>();

		cursorObservationFieldActive.moveToFirst();

		do {
			ova.add(cursorObservationFieldActive.getString(0));
		} while (cursorObservationFieldActive.moveToNext());

		return ova;
	}


	private void getObservationDataPerSingleRecord(Cursor cursor) {

		observationData=new ArrayList<String>();
		observationData.add(String.valueOf(cursor.getInt(0)));

		for(int i=1;i < cursor.getColumnCount();i++){
			observationData.add(cursorObservationData.getString(i));
		}
	}

	private void saveRecord(){
		for(int i=0; i < allEds.size(); i++){
			String value=String.valueOf(allEds.get(i).getText());
			String[] tmp=observationColumnMetaData.get(allEds.get(i).getId()).split(":");
			String field=tmp[0];
			observationManager.updateRecord(recId,field,value);
		}
	}

	private void updateDisplayEntry(){
		isValidRange=false;
		validScore=false;
		hasScoring=false;
		linearLayoutVariateDataEntry.removeAllViewsInLayout();
		linearLayoutFactorEntry.removeAllViewsInLayout();
		setObservationData(recId);
		createVariateLayout(observationColumnMetaData.size());
		linearLayoutVariateDataEntry.refreshDrawableState();
		linearLayoutFactorEntry.refreshDrawableState();
		allEds.get(0).requestFocus();
		//		txtSearchValue.setText(null);

		//		imageCount= new ImageManager().getImageCount(FieldLabPath.IMAGE_FOLDER+databaseName, valuePlotNo);
		//		audioCount=new ImageManager().getAudioCount(FieldLabPath.AUDIO_FOLDER+databaseName, valuePlotNo);
		//		lblImageCount.setText("( "+String.valueOf(imageCount)+" )");
		//		lblAudioCount.setText("( "+String.valueOf(audioCount)+" )");
		displayImageAndAudioButton();

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
			"-------------------\n")
			.setPositiveButton("Close", new
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
			.setPositiveButton("Ok", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					EditText editText = (EditText) findViewById(currentEditTextId);
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
					DataEntryUtil.setEditTextBlack(editText);

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
					moveToNextEntry();
				}
			})
			.create();
		case 3:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.warning)
			.setTitle("Warning Message")
			.setMessage("To Search Record, please specify a unique column in the Setting Menu")
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{

				}
			})
			.create();
		case 4:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.warning)
			.setTitle("Warning Message")
			.setMessage("To Take Photo, please specify a unique column in the Setting Menu")
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{

				}
			})
			.create();
		case 5:
			
			EditText editText = (EditText) findViewById(oldEditTextId);
			final String oldTextValue1=args.getString("oldTextValue");
			final String currentTextValue=args.getString("currentTextValue");
			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.warning)
			.setTitle("Warning Message")
			.setMessage("Would you like to replace existing value  " +oldTextValue1 +" with "+  currentTextValue )
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					EditText editText = (EditText) findViewById(oldEditTextId);
					editText.setText(currentTextValue);
					editText.requestFocus();
					oldTextValue= editText.getText().toString();
					
				}
			})
			.setNegativeButton("Cancel", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					EditText editText = (EditText) findViewById(oldEditTextId);
					editText.setText(oldTextValue1);
					editText.requestFocus();
					oldTextValue= editText.getText().toString();
				}
			})
			.create();
		}
		return  dialog;

	}

	protected void moveToNextEntry() {
		EditText editText;
		if( currentEditTextId < lastRecordId){
			editText = (EditText) findViewById(currentEditTextId+1);
		}else{
			editText = (EditText) findViewById(currentEditTextId);
		}
		editText.requestFocus();

	}


	// Baracoda Function

	private final ServiceConnection baracodaServiceConnection = new ServiceConnection () {
		public void onServiceConnected(ComponentName className, IBinder service) {

			try {
				DataEntryFormSingleActivity.this.baracodaService = IBaracodaReaderService.Stub.asInterface(service);
				DataEntryFormSingleActivity.this.baracodaService.registerCallback(DataEntryFormSingleActivity.this.baracodaServiceCallback);

				// We are not using application ack in this example, let's save its state
				applicationAckAtStartup = DataEntryFormSingleActivity.this.baracodaService.getApplicationAck();
				// Disable application ack
				DataEntryFormSingleActivity.this.baracodaService.setApplicationAck(false);

				// We are not using raw mode in this example, let's save its state
				rawModeAtStartup = DataEntryFormSingleActivity.this.baracodaService.getRawMode();
				// Disable raw mode
				DataEntryFormSingleActivity.this.baracodaService.setRawMode(false);
			}
			catch (RemoteException e) {
				// nothing to do here
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			DataEntryFormSingleActivity.this.baracodaService = null;
		}
	};

	private final IBaracodaReaderServiceCallback.Stub baracodaServiceCallback = new IBaracodaReaderServiceCallback.Stub() {
		@Override
		public void onAutoConnectChanged() throws RemoteException{
			// We need to switch to our handler (to avoid threading issues with GUI)
			// Autoconnect state has changed


			DataEntryFormSingleActivity.this.messageHandler.obtainMessage(DataEntryFormSingleActivity.MESSAGE_ON_AUTOCONNECT_STATE_CHANGED,
					0, 0).sendToTarget();


		}

		@Override
		public void onConnectionStateChanged(int connectionState) throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			// Connection state has changed
			DataEntryFormSingleActivity.this.messageHandler.obtainMessage(DataEntryFormSingleActivity.MESSAGE_ON_CONNECTION_STATE_CHANGED,
					connectionState, 0).sendToTarget();
		}

		@Override
		public void onDataRead(int dataType, String dataText) throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			// Data has been received from the reader

			DataEntryFormSingleActivity.this.messageHandler.obtainMessage(
					DataEntryFormSingleActivity.MESSAGE_ON_DATA_READ,
					dataType,
					-1,
					dataText).sendToTarget();

		}

		@Override
		public void onImageRead(byte[] arg0) throws RemoteException {
		}

		@Override
		public void onPrefixChanged() throws RemoteException {
		}

		@Override
		public void onReaderChanged() throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			DataEntryFormSingleActivity.this.messageHandler.obtainMessage(DataEntryFormSingleActivity.MESSAGE_ON_READER_CHANGED).sendToTarget();
		}

		@Override
		public void onSeparatorChanged() throws RemoteException {
		}

		@Override
		public void onSuffixChanged() throws RemoteException {
		}

		@Override
		public void onApplicationAckChanged() throws RemoteException {
		}

		@Override
		public void onRawModeChanged() throws RemoteException {
		}

		@Override
		public void onDataReadWithApplicationAck(int dataType, int id, String dataText) throws RemoteException {
		}

		@Override
		public void onRawDataRead(byte[] buffer) throws RemoteException {
		}
	};

	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try{
				switch (msg.what) {
				case DataEntryFormSingleActivity.MESSAGE_ON_DATA_READ:

					if(msg.arg1 == DataType.BARCODE) {
						StringBuilder sb = new StringBuilder((String)msg.obj);
						//sb.trimToSize();
						//sb.replace(sb.lastIndexOf(""), sb.length(), "");
						//						txtSearchValue.setText(sb.toString());
						String valueBarcode=(String)msg.obj;
						//						Toast.makeText(getBaseContext(),"Is Search Focus  " +txtSearchIsFocus, Toast.LENGTH_SHORT).show();
						if(txtSearchIsFocus==1){
							txtSearchValue.requestFocus();
							txtSearchValue.setText(valueBarcode);
							//							Toast.makeText(getBaseContext(),"Value Baracoda Msg " +valueBarcode, Toast.LENGTH_SHORT).show();
							searchRecord(valueBarcode);
						}else{
							EditText editText = (EditText) findViewById(currentEditTextId);
							editText.setText(valueBarcode);
						}

					}
					//	            	if(msg.arg1 == DataType.BARCODE)
					//            			sb.insert(0, "Barcode: ");
					//            		else if(msg.arg1 == DataType.RFIDTAGID)
					//            			sb.insert(0, "RFID tag ID: ");
					break;
				}
			}finally{

			}
		}
	};
	public static boolean isNumber(String string) {
		return string != null && numberPattern.matcher(string).matches();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		imageCount= new ImageManager().getImageCount(FieldLabPath.IMAGE_FOLDER+databaseName, valuePlotNo);
		lblImageCount.setText("("+String.valueOf(imageCount)+")");
	}



}
