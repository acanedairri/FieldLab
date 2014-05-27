package org.irri.fieldlab.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baracoda.android.baracodamanager.DataType;
import com.baracoda.android.baracodamanager.IBaracodaReaderService;
import com.baracoda.android.baracodamanager.IBaracodaReaderServiceCallback;

public class DataEntryFormContinuousActivity extends Activity{


	//Layout 
	private LinearLayout linearLayoutDataEntry;
	private LinearLayout linearLayoutColumnHeader;
	private HorizontalScrollView horizontalScrollViewData;
	private HorizontalScrollView horizontalScrollViewColumnHeader;
	private int scrollInitialPosition;
	private int scrollDelay = 100;
	private TableLayout tableFactorLayout;
	private Runnable scrollerTask;
	private OnScrollStoppedListener onScrollStoppedListener;

	//Buttons
	private Button btnClose;
	private Button btnNext;
	private Button btnPrevious;
	private Button btnSearch;
	private Button btnImage;
	private Button btnTakePhoto;
	private Button btnAudioCapture;
	private Button btnViewImage;

	//Paging
	private int recId;
	private int txtFieldId=0;
	private int numRec=10;
	private int startLimit=1;
	private int endLimit=numRec;
	private int totalRecord;
	private int page=1;
	private int lastRecordId;
	private int newRecId;
	private int totalPage;
	private int currentpage=1;
	private int recordNo;


	//DataEntry Order
	private String displayOrder="ASC";
	private String mdv="N";
	private int rcbOption=0;

	//TextView/Label Field
	private TextView labelRecordRef;
	private TextView txtFieldPageNumber;
	private TextView txtFieldSearchValue;
	private TextView labelPage;
	private String studyName;
	private TextView labelImageCount;
	private TextView txtReference;
	private TextView txtTraitDescription;

	//Boolean
	private boolean isValidRange=false;;
	private boolean isValidScore=false;
	private boolean hasScoring=false;
	private boolean firstSearch=false;
	private boolean isTextFieldSearchFocus=true;

	//Manager/Database
	private ObservationManager observationManager;
	private SettingsManager settingsManager;
	private DescriptionManager descriptionManager;
	private Validation validationManager;
	private ScoringManager scoringManager;
	private FieldLabManager fieldLabManager;
	private String databaseName;
	private DatabaseConnect databaseConnect;
	private Cursor cursorObservationData;

	//Cursor/Data
	private ArrayList<String> observationColumnMetaData;
	private List<EditText> allDataEntryEditTextField = new ArrayList<EditText>();
	private HashMap dataEntryMap;
	private HashMap<Integer, String> recNoOfRecId= new HashMap();
	private HashMap<String, Integer> recIdOfRecNo= new HashMap();
	private int currentDataEntryID;
	private int previousDataEntryId;
	private int currentRecordSelected;
	private int tableRowIndex=0;
	private int txtViewSearchId=0;
	private int barcodeReferenceColumn=0;
	private String barcodeReference="";
	private String currentTraitCodeSelected="";
	private String currentDataTypeSelected="";
	private String dataType;
	private String dateValue;
	private String photoReferenceCode;

	private int imageCount;
	private String valuePlotNo;
	private String valueOfSearchRecord;
	private String oldTextValue;


	//Menu 
	private static final int TRAIT_DISPLAY=0;
	private static final int RANGES_INPUT_ACTIVITY=1;
	private static final int REMARKS_ACTIVITY=2;
	private static final int ACTIVITY_CALENDAR=3;
	private static final int PHOTO_CAPTURE_ACTIVITY=4;
	private static final int BARACODA_CONNECTION_ACTIVITY=5;
	private static final int SCORING_MORPHO_ACTIVITY=6;
	private static final int AUDIO_CAPTURE_ACTIVITY=7;
	private static final int IMAGE_CAPTURE_ACTIVITY=8;

	//Baracoda
	private String currentDevice="";
	private int connetionState=0;
	private static final int MESSAGE_ON_READER_CHANGED = 1;
	private static final int MESSAGE_ON_CONNECTION_STATE_CHANGED = 2;
	private static final int MESSAGE_ON_AUTOCONNECT_STATE_CHANGED = 3;
	private static final int MESSAGE_ON_DATA_READ = 4;
	private static final String TAG = "BaracodaSDKExample";
	private static final int ACTION_REQUEST_SEARCH_DEVICES_RESULT = 1;
	private static final int ACTION_REQUEST_ENABLE_BT_RESULT = 2;
	private static final int DATA_ENTRY_SETTINGS = 3;
	public static String EXTRA_DEVICE = "device";
	private IBaracodaReaderService baracodaService = null;
	private BluetoothAdapter bluetoothAdapter = null;
	private boolean btStackAtStartup = false;
	private boolean applicationAckAtStartup = false;
	private boolean rawModeAtStartup = false;


	//Settings
	private static int TEXT_SIZE=27;
	private static int FACTOR_TEXT_WIDTH=100;
	private static int VARIATE_TEXT_WIDTH=120;
	private static final Pattern numberPattern = Pattern.compile( "(-|\\+)?[0-9]+(\\.[0-9]+)?");
	private static DateFormat dateFormat;


	@SuppressWarnings({ "unused", "unused" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");
		setContentView(R.layout.dataentryformcontinuous);


		linearLayoutColumnHeader=(LinearLayout) findViewById(R.id.LinearLayoutColumnHeader);
		linearLayoutDataEntry=(LinearLayout) findViewById(R.id.LinearLayoutDataEntry);
		tableFactorLayout = (TableLayout) findViewById(R.id.tableFactors);
		labelRecordRef=(TextView) findViewById(R.id.lblRecordRef);
		txtFieldSearchValue =(TextView) findViewById(R.id.txtSearchValue);
		txtReference=(TextView) findViewById(R.id.txtReference);

		txtFieldSearchValue.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Toast.makeText(getBaseContext(),"Barcode Read", Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		txtFieldSearchValue.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					isTextFieldSearchFocus=true;
					if(barcodeReference.toLowerCase().equals("barcode")){
						txtFieldSearchValue.setInputType(1);
					}else{
						txtFieldSearchValue.setInputType(3);
					}
					//					Toast.makeText(getBaseContext(),"has focus", Toast.LENGTH_SHORT).show();
					btnAudioCapture.setEnabled(false);
					btnTakePhoto.setEnabled(false);
				}else{
					isTextFieldSearchFocus=false;
					btnAudioCapture.setEnabled(true);
					btnTakePhoto.setEnabled(true);
					//					Toast.makeText(getBaseContext(),"has no focus", Toast.LENGTH_SHORT).show();
				}
			}
		});




		txtFieldSearchValue.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if(txtFieldSearchValue.length() > 0 || txtFieldPageNumber.length() > 0){
						if(txtFieldSearchValue.getText().length() > 0){
							searchRecord(txtFieldSearchValue.getText().toString());
						}else{
							searchPage();
							txtFieldPageNumber.setText("");
						}
					}
					return true;
				}
				else {
					return false;
				}
			}
		});

		txtFieldPageNumber=(TextView) findViewById(R.id.txtPage);
		txtFieldPageNumber.setInputType(3);

		txtFieldPageNumber.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if(txtFieldSearchValue.length() > 0 || txtFieldPageNumber.length() > 0){
						if(txtFieldSearchValue.getText().length() > 0){
							searchRecord(txtFieldSearchValue.getText().toString());
						}else{
							searchPage();
							txtFieldPageNumber.setText("");
						}
					}
					return true;
				}
				else {
					return false;
				}
			}
		});

		labelRecordRef.setTextColor(Color.YELLOW);
		labelRecordRef.setBackgroundColor(Color.BLACK);

		labelPage=(TextView) findViewById(R.id.lblPage);
		labelPage.setTextColor(Color.YELLOW);
		labelPage.setBackgroundColor(Color.BLACK);


		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}

		btnClose = (Button)findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//saveRecord();
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
				if (!mBluetoothAdapter.isEnabled()) {

				}else{ 
					mBluetoothAdapter.disable(); 
				} 
				finish();

			}
		});

		btnSearch = (Button)findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(txtFieldSearchValue.length() > 0 || txtFieldPageNumber.length() > 0){
					if(txtFieldSearchValue.getText().length() > 0){
						searchRecord(txtFieldSearchValue.getText().toString());
					}else{
						searchPage();
						txtFieldPageNumber.setText("");
					}
				}else{
					showDialog(3, null);
				}
			}
		});

		btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!barcodeReference.equals("")){
					settingsManager.updateLastRecord(startLimit);
					String[] ref= dataEntryMap.get(currentDataEntryID).toString().split(":");
					Calendar now = GregorianCalendar.getInstance();
					photoReferenceCode=observationManager.getReferenceCode(barcodeReference,ref[0]);
					saveAllDataEntry();
					Intent iPhoto = new Intent(DataEntryFormContinuousActivity.this,PhotoCaptureActivity.class);
					iPhoto.putExtra("DBNAME", databaseName);
					String photoName=databaseName+"_"+photoReferenceCode+"_"+currentTraitCodeSelected;
					iPhoto.putExtra("PHOTO_NAME",photoName );
					iPhoto.putExtra("CURRENTTEXTID",currentDataEntryID );
					startActivityForResult(iPhoto, PHOTO_CAPTURE_ACTIVITY);
					//	Toast.makeText(getBaseContext(),photoName, Toast.LENGTH_SHORT).show();
				}else{
					//				Toast.makeText(getBaseContext(),"To use this features please first specify the Observation Sheet Unique Column in Setting Menu", Toast.LENGTH_SHORT).show();
					showDialog(4, null);
				}

			}
		});


		btnAudioCapture = (Button)findViewById(R.id.btnRecordAudio);
		btnAudioCapture.setOnClickListener(new View.OnClickListener() {
			private String audioReferenceCode;

			@Override
			public void onClick(View v) {
				if(!barcodeReference.equals("")){
					settingsManager.updateLastRecord(startLimit);
					String[] ref= dataEntryMap.get(currentDataEntryID).toString().split(":");
					audioReferenceCode=observationManager.getReferenceCode(barcodeReference,ref[0]);
					saveAllDataEntry();
					Intent iAudio = new Intent(DataEntryFormContinuousActivity.this,AudioRecordingActivity.class);
					iAudio.putExtra("DBNAME", databaseName);
					String audioName=databaseName+"_"+audioReferenceCode+"_"+currentTraitCodeSelected;
					iAudio.putExtra("AUDIO_NAME",audioName );
					iAudio.putExtra("CURRENTTEXTID",currentDataEntryID );
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

		btnPrevious = (Button)findViewById(R.id.btnPrevious);
		btnPrevious.setOnClickListener(previousListener);

		initDataEntry();
		displayColumnHeader();
		initRecordDisplay(settingsManager.getLastRecord());
		setTitle("Observation Data: "+studyName);

		txtFieldSearchValue.requestFocus();
		txtFieldSearchValue.setHint(barcodeReference);

		btnViewImage = (Button)findViewById(R.id.btnViewImage);
		btnViewImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DataEntryFormContinuousActivity.this, ImageListViewActivity.class);
				i.putExtra("folder_path",FieldLabPath.IMAGE_FOLDER+databaseName);
				i.putExtra("plot_name",photoReferenceCode );
				startActivity(i);
			}
		});

		labelImageCount = (TextView)findViewById(R.id.lblImageCount);

		horizontalScrollViewData=(HorizontalScrollView) findViewById(R.id.HorizontalScrollViewData);
		horizontalScrollViewColumnHeader=(HorizontalScrollView) findViewById(R.id.HorizontalScrollViewColumnHeader);
		horizontalScrollViewData.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					this.startScrollerTask();
				}

				return false;
			}

			private void startScrollerTask() {
				// TODO Auto-generated method stub
				scrollInitialPosition = horizontalScrollViewData.getScrollX();
				horizontalScrollViewData.postDelayed(scrollerTask, scrollDelay);
			}
		});

		scrollerTask = new Runnable() {

			public void run() {

				int newPosition =horizontalScrollViewData.getScrollX();
				Log.d("x",String.valueOf(newPosition));
				if(scrollInitialPosition - newPosition == 0){//has stopped

					if(onScrollStoppedListener!=null){
						onScrollStoppedListener.onScrollStopped();
					}
				}else{
					scrollInitialPosition = horizontalScrollViewData.getScrollX();
					horizontalScrollViewData.postDelayed(scrollerTask, scrollDelay);
				}

				horizontalScrollViewColumnHeader.scrollTo(newPosition, 0);
			}
		};
		txtTraitDescription= (TextView) findViewById(R.id.txtTraitDesciption);

	}


	private void displayFactorInfo(int recId,String photoReferenceCode) {

		tableFactorLayout.removeAllViews();
		String referenceRow = "";
		// search for factor display region equal r1
		Cursor cursorFactorActive=descriptionManager.getActiveFactor();

		// display the reference row set
		if(!barcodeReference.equals("select")){
			referenceRow=observationManager.getReferenceRow(recId,barcodeReference);
			//			txtReference.setText(referenceRow);
		}

		TableRow tableRowRef = new TableRow(this);
		TextView txtViewLabelRef=new TextView(this);
		txtViewLabelRef.setText(barcodeReference);
		txtViewLabelRef.setTextSize(20);
		txtViewLabelRef.setGravity(Gravity.LEFT);
		txtViewLabelRef.setMaxLines(1);
		txtViewLabelRef.setTextColor(Color.WHITE);
		tableRowRef.addView(txtViewLabelRef);

		TextView txtViewValueRef=new TextView(this);
		txtViewValueRef.setText(referenceRow);
		txtViewValueRef.setPadding(50, 0, 0, 0);
		txtViewValueRef.setTextSize(40);
		//			txtViewValue.setGravity(Gravity.LEFT);
		txtViewValueRef.setTextColor(Color.WHITE);
		tableRowRef.addView(txtViewValueRef);

		tableFactorLayout.addView(tableRowRef);


		for(int i=0;i<cursorFactorActive.getCount();i++){

			TableRow tableRow = new TableRow(this);
			String field=cursorFactorActive.getString(1);
			TextView txtViewLabel=new TextView(this);
			txtViewLabel.setText(field);
			txtViewLabel.setTextSize(20);
			txtViewLabel.setGravity(Gravity.LEFT);
			txtViewLabel.setMaxLines(1);
			txtViewLabel.setTextColor(Color.WHITE);
			tableRow.addView(txtViewLabel);

			Cursor cursorFactorValue=observationManager.getFactorValue(field,recId);
			//			Toast.makeText(getBaseContext(),recId + " "+barcodeReference, Toast.LENGTH_SHORT).show();


			TextView txtViewValue=new TextView(this);
			int lengthText=cursorFactorValue.getString(0).length();

			if(lengthText > 80){
				txtViewValue.setWidth(500);
				txtViewValue.setLines(3);
			}
			txtViewValue.setText(cursorFactorValue.getString(0));
			txtViewValue.setPadding(50, 0, 0, 0);
			txtViewValue.setTextSize(20);
			//			txtViewValue.setGravity(Gravity.LEFT);
			txtViewValue.setTextColor(Color.WHITE);
			tableRow.addView(txtViewValue);

			tableFactorLayout.addView(tableRow);
			cursorFactorActive.moveToNext();
		}	

		//		imageCount= new ImageManager().getImageCount(FieldLabPath.IMAGE_FOLDER+databaseName, photoReferenceCode);
		//		if(imageCount>0){
		//			btnViewImage.setVisibility(View.VISIBLE);
		//			labelImageCount.setVisibility(View.VISIBLE);
		//			labelImageCount.setText("( "+String.valueOf(imageCount)+" )");
		//		}else{
		//			btnViewImage.setVisibility(View.GONE);
		//			labelImageCount.setVisibility(View.GONE);
		//		}

	}

	private void initDataEntry() {

		databaseConnect= new DatabaseConnect(DataEntryFormContinuousActivity.this, databaseName);
		databaseConnect.openDataBase();
		fieldLabManager= new FieldLabManager(databaseConnect.getDataBase());
		observationManager=fieldLabManager.getObservationManager();
		settingsManager=fieldLabManager.getSettingsManager();
		descriptionManager=fieldLabManager.getDescriptionManager();
		scoringManager=fieldLabManager.getScoringManager();
		validationManager= new Validation();

		barcodeReference=settingsManager.getBarcodeReference();
		rcbOption=settingsManager.getRCBDoption();
		mdv=settingsManager.getMDV();
		numRec=settingsManager.getTotalRecordPerPage();
		totalRecord=getObservationTotalRec();
		studyName=descriptionManager.getStudyName();

		if(numRec > totalRecord){
			numRec=1;
		}

		endLimit=numRec;
		totalPage=totalRecord/numRec;

		int tmpTotalPage=totalPage*numRec;

		if(totalRecord > tmpTotalPage){
			totalPage++;
		}

		observationColumnMetaData=getObsevationColumnActive(); 
		dataEntryMap= new HashMap();
	}


	private void initRecordDisplay(int lastRec) {
		// TODO Auto-generated method stub
		newRecId=lastRec;
		allDataEntryEditTextField.clear();
		dataEntryMap.clear();
		lastRecordId=0;

		if(newRecId < numRec){
			startLimit=1;
			endLimit=numRec;
		}else{
			int remainder=(newRecId%numRec);
			if(remainder==0)
				startLimit=(newRecId-numRec)+1;
			else{
				startLimit=((newRecId/numRec)*numRec)+1;
			}

			endLimit=(startLimit+numRec)-1;
			page=endLimit/numRec;

			if(endLimit > totalRecord){
				endLimit=totalRecord;
				page=totalPage;
			}
		}
		linearLayoutDataEntry.removeAllViewsInLayout();
		displayRecord();
	}

	protected void searchPage() {
		int pageValueInput=Integer.valueOf(txtFieldPageNumber.getText().toString());
		if(pageValueInput > totalPage || pageValueInput < 1 ){

		}else if(pageValueInput==totalPage){
			startLimit=((pageValueInput-1) * numRec)+1;
			endLimit=totalRecord;
			page=totalPage;
			saveAllDataEntry();
			linearLayoutDataEntry.removeAllViewsInLayout();
			displayRecord();
		}else{
			startLimit=((pageValueInput* numRec) - numRec)+ 1;
			endLimit=(startLimit + numRec)-1;
			page=endLimit/numRec;
			saveAllDataEntry();
			linearLayoutDataEntry.removeAllViewsInLayout();
			displayRecord();
		}

	}

	protected void searchRecord(String valueBarcode) {

		if(barcodeReference.equals("")){
			Toast.makeText(getBaseContext(),"Please set barcode column reference on setting menu ", Toast.LENGTH_SHORT).show();
		}else{
			newRecId=observationManager.searchRecordByField(barcodeReference,valueBarcode.trim());
			if(newRecId==0){
				Toast.makeText(getBaseContext(),"Record not found " +barcodeReference + " "+ valueBarcode, Toast.LENGTH_SHORT).show();
			}else{
				//			Toast.makeText(getBaseContext(),String.valueOf(newRecId) + " "+String.valueOf(numRec) , Toast.LENGTH_SHORT).show();
				valueOfSearchRecord = observationManager.getSearchRecordByField(barcodeReference,valueBarcode.trim());
				firstSearch=false;
				if(newRecId < numRec){
					startLimit=1;
					endLimit=numRec;
				}else{
					int remainder=(newRecId%numRec);

					if(remainder==0)
						startLimit=(newRecId-numRec)+1;
					else{
						startLimit=((newRecId/numRec)*numRec)+1;
					}

					if((startLimit + numRec) > totalRecord){
						endLimit=totalRecord;

					}else{
						endLimit=(startLimit+numRec)-1;
					}

				}
				//			
				saveAllDataEntry();
				linearLayoutDataEntry.removeAllViewsInLayout();
				page=endLimit/numRec;
				displayRecord();	

			}
			txtFieldSearchValue.requestFocus();
			txtFieldSearchValue.setText(null);
			isTextFieldSearchFocus=true;
			EditText txtEditCurrent = (EditText) findViewById(txtViewSearchId);
			txtEditCurrent.requestFocus();

		}
	}

	private void displayColumnHeader() {
		// TODO Auto-generated method stub
		linearLayoutColumnHeader.addView(createDataEntryColumnHeaderTable(observationColumnMetaData.size()), 0);
		linearLayoutColumnHeader.invalidate();
	}

	private void displayRecord(){

		ArrayList<ArrayList<String>> obsData= new ArrayList<ArrayList<String>>();
		if(rcbOption==2){
			if((page%2)==1){
				displayOrder="DESC";
			}else{
				displayOrder="ASC";
			}
		}else if(rcbOption==1){
			if((page%2)==1){
				displayOrder="ASC";
			}else{
				displayOrder="DESC";
			}
		}else if(rcbOption==0 && displayOrder.equals("DESC")){
			displayOrder="DESC";
		}else if(rcbOption==0 && displayOrder.equals("ASC")){
			displayOrder="ASC";
		}

		if(displayOrder.equals("ASC")){
			recordNo=startLimit;
			for(int i=startLimit;i <= endLimit;i++){
				ArrayList<String> obsRow=new ArrayList<String>();
				obsRow=getObservationDataPerRecId(i);
				if(obsRow.size() > 0){
					obsData.add(obsRow);
				}else{

				}
				obsRow=null;
			}
		}

		if(displayOrder.equals("DESC")){
			recordNo=startLimit;
			for(int i=endLimit;i >= startLimit;i--){
				ArrayList<String> obsRow=new ArrayList<String>();
				obsRow=getObservationDataPerRecId(i);
				if(obsRow.size() > 0){
					obsData.add(obsRow);
				}else{

				}
				obsRow=null;
			}
		}

		Log.d("data",obsData.toString());
		txtFieldId=0;
		createVariateLayout(observationColumnMetaData.size(),obsData);
		linearLayoutDataEntry.refreshDrawableState();

		labelPage.setText("  Page: " + String.valueOf(page) +" of " + String.valueOf(totalPage));
		labelRecordRef.setText("  Record: "+startLimit + " - "+ endLimit +" of "+totalRecord );
		newRecId=0;
		txtFieldSearchValue.setHint(barcodeReference);
	}

	private int getObservationTotalRec() {
		Cursor cursorCount=observationManager.countAllObservationRecord();
		return Integer.valueOf(cursorCount.getString(0));
	}

	// retreived single record from the database
	private  ArrayList<String> getObservationDataPerRecId(int recId) {
		// TODO Auto-generated method stub
		ArrayList<String> observationData=new ArrayList<String>();

		Cursor cursorObservationData= observationManager.getAllObservationData(getFieldObservation(),getObservationVariateActive(),recId,mdv);

		if(cursorObservationData.getCount() > 0){

			cursorObservationData.moveToFirst();

			observationData.add(String.valueOf(cursorObservationData.getInt(0)));

			for(int i=1;i < cursorObservationData.getColumnCount();i++){
				observationData.add(cursorObservationData.getString(i));
			}

		}
		return observationData;
	}

	private ArrayList<String> getObsevationColumnActive() {

		Cursor cursorObservationFieldActive=descriptionManager.getObservationColumnActive();

		observationColumnMetaData=new ArrayList<String>();
		observationColumnMetaData.add("REC#:C:label");

		cursorObservationFieldActive.moveToFirst();

		do {
			String line = cursorObservationFieldActive.getString(0)+ ":"+ cursorObservationFieldActive.getString(1) + ":"+cursorObservationFieldActive.getString(2);
			observationColumnMetaData.add(line);
		} while (cursorObservationFieldActive.moveToNext());

		return observationColumnMetaData;
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

	private String getFieldObservation() {

		Cursor cursorObservationFieldActive=descriptionManager.getObservationColumnActive();
		String fieldObservation="";
		cursorObservationFieldActive.moveToFirst();

		do {
			fieldObservation+="`"+cursorObservationFieldActive.getString(0)+"`,"; // to know the field to query in the observation table
		} while (cursorObservationFieldActive.moveToNext());

		fieldObservation=fieldObservation.substring(0,fieldObservation.length()-1);
		return fieldObservation;
	}



	private void createVariateLayout(int fieldCount,ArrayList<ArrayList<String>> obsData){

		LinearLayout linearLayoutVariate = new LinearLayout(this); // layout for dataentry
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams. WRAP_CONTENT);
		linearLayoutVariate.setLayoutParams(params);
		linearLayoutVariate.setOrientation(LinearLayout.VERTICAL);

		for(int i=0;i< obsData.size();i++){
			linearLayoutVariate.addView(createDataEntryTable(fieldCount,obsData.get(i)));
			Log.d("datas",obsData.get(i).toString());
		}
		linearLayoutDataEntry.addView(linearLayoutVariate);
		linearLayoutVariate.invalidate();
	}


	private View.OnClickListener nextListener = new View.OnClickListener() {
		public void onClick(View view) {

			if((startLimit + numRec) > totalRecord){
				Toast.makeText(getBaseContext(),"End of Pages", Toast.LENGTH_SHORT).show();
			}else{
				startLimit=endLimit+1;
				endLimit=startLimit+numRec-1;

				if(endLimit > totalRecord){
					endLimit=totalRecord;
				}
				linearLayoutDataEntry.removeAllViewsInLayout();
				if(page < totalPage){
					page++;
				}
				saveAllDataEntry();
				displayRecord();

			}
		}
	};

	private View.OnClickListener previousListener = new View.OnClickListener() {
		public void onClick(View view) {

			startLimit=startLimit-numRec;
			endLimit=startLimit+numRec-1;

			if(startLimit < 1){
				startLimit=1;
				endLimit=numRec;
			}
			linearLayoutDataEntry.removeAllViewsInLayout();
			if(page > 1){
				page--;
			}
			saveAllDataEntry();
			displayRecord();
		}
	};
	private int audioCount;

	// Using a TableLayout as it provides you with a neat ordering structure

	private TableLayout createDataEntryTable(int fieldCount,ArrayList<String> obs) {
		TableLayout tableLayout = new TableLayout(this);
		//tableLayout.setPadding(1, 0, 0, 1);
		tableLayout.setStretchAllColumns(true);
		//int noOfRows = count / 2;
		int noOfColumn = fieldCount;
		tableLayout.addView(createDataEntryRow(noOfColumn,obs));
		return tableLayout;
	}

	private TableLayout createDataEntryColumnHeaderTable(int fieldCount) {
		TableLayout tableLayout = new TableLayout(this);
		//tableLayout.setPadding(1, 0, 0, 1);
		tableLayout.setStretchAllColumns(true);
		//int noOfRows = count / 2;
		tableLayout.addView(createDataEntryColumnHeaderRow(fieldCount));
		TableRow tableRow = new TableRow(this);
		return tableLayout;
	}


	private TableRow createDataEntryRow(int noOfColumn,ArrayList<String>obs) {
		TableRow tableRow = new TableRow(this);
		String recordId=obs.get(0);
		boolean addImageCount=true;

		for (int column = 0; column < noOfColumn; column++) {
			String[] s = observationColumnMetaData.get(column).split(":");
			if(s[2].equals("textfield")){
				tableRow.addView(createDataEntryTextField(txtFieldId,column,s[1],obs.get(column),recordId,s[1]));
				txtFieldId++;

			}else{
				tableRow.addView(createDataEntryLabel(obs.get(column),column,recordId,txtFieldId));


				if(addImageCount){
					String ref=settingsManager.getBarcodeReference();
					valuePlotNo=observationManager.getReferenceCode(ref, obs.get(column));
					//					Toast.makeText(DataEntryFormContinuousActivity.this,valuePlotNo, Toast.LENGTH_SHORT).show();
					imageCount =new ImageManager().getImageCount(FieldLabPath.IMAGE_FOLDER+databaseName, valuePlotNo);
					audioCount =new ImageManager().getAudioCount(FieldLabPath.AUDIO_FOLDER+databaseName, valuePlotNo);

					//					Toast.makeText(DataEntryFormContinuousActivity.this,String.valueOf(imageCount) + " "+String.valueOf(imageCount), Toast.LENGTH_SHORT).show();

					Button btnImage=createImageButton(valuePlotNo);
					if(imageCount > 0){
						btnImage.setVisibility(View.VISIBLE);
					}else{
						btnImage.setVisibility(View.INVISIBLE);
					}
					tableRow.addView(btnImage);

					TextView txtSpace= new TextView(this);
					txtSpace.setText("      ");
					tableRow.addView(txtSpace);

					Button btnAudio=createAudioButton(valuePlotNo);
					if(audioCount > 0){
						btnAudio.setVisibility(View.VISIBLE);
					}else{
						btnAudio.setVisibility(View.INVISIBLE);
					}
					tableRow.addView(btnAudio);
					TextView txtSpace2= new TextView(this);
					txtSpace2.setText("      ");
					tableRow.addView(txtSpace2);
					addImageCount=false;

				}
			}
		}

		if(tableRowIndex%2==0){
			tableRow.setBackgroundColor(Color.rgb(245, 245, 245));
		}else{
			tableRow.setBackgroundColor(Color.rgb(230, 230, 230));
		}
		tableRowIndex++;

		return tableRow;
	}

	// Create Column Header

	private TableRow createDataEntryColumnHeaderRow(int noOfColumn) {
		TableRow tableRow = new TableRow(this);


		for (int column = 0; column < noOfColumn; column++) {

			String[] s = observationColumnMetaData.get(column).split(":");
			if(s[0].equals(barcodeReference)){
				barcodeReferenceColumn=column;
			}
			tableRow.addView(labelDataEntryColumnHeader(s[0],s[2]));
		}

		return tableRow;
	}


	private EditText createDataEntryTextField(final int editTextId,final int indexId,String hint,String value,String recordId,String datatype) {
		EditText dataEntryTextField = new EditText(DataEntryFormContinuousActivity.this);
		allDataEntryEditTextField.add(dataEntryTextField);
		dataEntryTextField.setId(Integer.valueOf(editTextId));
		//		editText.setHint(hint);
		dataEntryTextField.setText(value);
		dataEntryTextField.setTextSize(TEXT_SIZE);
		dataEntryTextField.setEms(2);
		dataEntryTextField.setSingleLine();
		
		if(datatype.equals("CD")){
			dataEntryTextField.setWidth(160);
		}else{
			dataEntryTextField.setWidth(VARIATE_TEXT_WIDTH);
		}
		final String[] s = observationColumnMetaData.get(indexId).split(":");
		final String dtype=s[1];
		final String traitcode=s[0];

		String editTextMedataData=recordId+":"+traitcode+":"+dtype;

		dataEntryMap.put(editTextId, editTextMedataData);
		lastRecordId=editTextId;

		dataEntryTextField.setOnEditorActionListener(new OnEditorActionListener() {
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

		dataEntryTextField.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {

				String editTextValue=String.valueOf(((EditText) v).getText());
				currentDataEntryID=((EditText) v).getId();
				isTextFieldSearchFocus=false;

				if(hasFocus) {
		/*			
					if(currentDataTypeSelected.equals("CD")){
						((EditText) v).setTextSize(TEXT_SIZE);
					}else{
						((EditText) v).setTextSize(40);
					}*/

					//					Toast.makeText(getBaseContext(),"currentTextId: "+ String.valueOf(currentEditTextId) + String.valueOf(lastRecordId) , Toast.LENGTH_SHORT).show();
					oldTextValue=String.valueOf(((EditText) v).getText());
					hasScoring=scoringManager.hasScoring(traitcode);
					currentTraitCodeSelected=traitcode;
					currentRecordSelected=indexId;
					currentDataTypeSelected=dtype;

					String traitDescrition=descriptionManager.getDescription(currentTraitCodeSelected);
					//					Toast.makeText(getBaseContext(),traitDescrition, Toast.LENGTH_SHORT).show();

					String[] ref= dataEntryMap.get(currentDataEntryID).toString().split(":");

					if(hasScoring && settingsManager.autoPromptScoring()){
						showScoringDialog(DataEntryUtil.getScoring(traitcode, scoringManager), traitcode, currentDataEntryID);
					}

					DataEntryUtil.setEditTextInputType(dtype,v);
					int recordId=Integer.valueOf(ref[0]);

					photoReferenceCode=observationManager.getReferenceCode(barcodeReference,ref[0]);
					imageCount= new ImageManager().getImageCount(FieldLabPath.IMAGE_FOLDER+databaseName, photoReferenceCode);
					displayFactorInfo(recordId,photoReferenceCode);
					txtTraitDescription.setText("Measuring: "+traitDescrition);
					horizontalScrollViewColumnHeader.scrollTo(horizontalScrollViewData.getScrollX(), 0);
				}else{

					try{
					/*	((EditText) v).setTextSize(TEXT_SIZE);*/
						previousDataEntryId=currentDataEntryID;
						EditText dataEntryTextFieldCurrent = (EditText) findViewById(currentDataEntryID);

						if(!oldTextValue.equals(editTextValue) && oldTextValue.length() > 0)
						{
//							Toast.makeText(getBaseContext(),"OldValue: "+oldTextValue, Toast.LENGTH_SHORT).show();
							EditText editTextPrev1 = (EditText) findViewById(previousDataEntryId);
							String currentTextValue2=editTextPrev1.getText().toString();
							Bundle args = new Bundle();
							args.putInt("currentEditTextId",currentDataEntryID);
							args.putString("oldTextValue",oldTextValue);
							args.putString("currentTextValue",currentTextValue2);
							removeDialog(5);
							showDialog(5, args);
						}
						
						if(!isNumber(editTextValue) && dtype.equals("D")){
							EditText dataEntryTextFieldPreviou = (EditText) findViewById(previousDataEntryId);
							dataEntryTextFieldPreviou.setText("");
						}

						if(editTextValue.length() > 0 && !dtype.equals("C")){
							isValidRange=validationManager.isValidRange(editTextValue, traitcode, descriptionManager);
							isValidScore=validationManager.isValidScore(editTextValue, traitcode, scoringManager);
							hasScoring=scoringManager.hasScoring(traitcode);

							if(!isValidScore && hasScoring ){

								DataEntryUtil.setEditTextRed(dataEntryTextFieldCurrent);
								Bundle args = new Bundle();
								args.putString("scoring",DataEntryUtil.getScoring(traitcode, scoringManager));
								args.putInt("currentEditTextId",currentDataEntryID);
								removeDialog(0);
								showDialog(0, args);
							}

							if(!isValidRange){
								DataEntryUtil.setEditTextRed(dataEntryTextFieldCurrent);

								String validRange=validationManager.getValidRange(traitcode, descriptionManager);
								Bundle args = new Bundle();
								args.putString("validRange",validRange);
								args.putInt("currentEditTextId",currentDataEntryID);
								removeDialog(1);
								showDialog(1, args);
							}

							if(isValidRange || isValidScore){
								DataEntryUtil.setEditTextBlack(dataEntryTextFieldCurrent);
							}
						}
					}catch(Exception e){

					}
					horizontalScrollViewColumnHeader.scrollTo(horizontalScrollViewData.getScrollX(), 0);
				}

			}
		});

		final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

			public boolean onDoubleTap(MotionEvent e) {
				String[] s = observationColumnMetaData.get(indexId).split(":");
				String dtype=s[1];
				String traitcode=s[0];
				currentDataTypeSelected=dtype;

				boolean hasScoring=scoringManager.hasScoring(traitcode);

				if(hasScoring){
					String scores=DataEntryUtil.getScoring(traitcode, scoringManager);
					if(scores.contains(".jpg")){
						Intent i = new Intent(DataEntryFormContinuousActivity.this, ScoringMorphoActivity.class);
						//					Toast.makeText(getBaseContext(),"Value to PASS "+String.valueOf(currentEditTextId), Toast.LENGTH_SHORT).show();
						i.putExtra("CURRENTTEXTID",currentDataEntryID);
						i.putExtra("SCORES",scores);
						i.putExtra("TRAITDESCRIPTION",txtTraitDescription.getText().toString().split(":")[1]);
						i.putExtra("CURRENT_TRAITCODE_SELECTED",currentTraitCodeSelected);
						startActivityForResult(i, SCORING_MORPHO_ACTIVITY);
					}else{
						showScoringDialog(DataEntryUtil.getScoring(traitcode, scoringManager), traitcode, currentDataEntryID);
					}
				}

				if(traitcode.toLowerCase().contains("remark") || traitcode.toLowerCase().contains("comment") ){
					saveAllDataEntry();
					EditText dataEntryTextFieldCurrent = (EditText) findViewById(currentDataEntryID);
					Intent i1 = new Intent(DataEntryFormContinuousActivity.this,RemarksActivity.class);
					i1.putExtra("DBNAME", databaseName);
					i1.putExtra("REMARKS", dataEntryTextFieldCurrent.getText().toString());
					i1.putExtra("CURRENTTEXTID",currentDataEntryID);
					startActivityForResult(i1, REMARKS_ACTIVITY);
				}

				if(dtype.equals("D") || dtype.equals("CD")){
					Intent i = new Intent(DataEntryFormContinuousActivity.this, CalendarViewActivity.class);
					i.putExtra("CURRENTTEXTID",currentDataEntryID);
					i.putExtra("DTYPE",dtype);
					startActivityForResult(i, ACTIVITY_CALENDAR);
				}
				return true;
			}
		});

		dataEntryTextField.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		});
		return dataEntryTextField;
	}


	protected void moveToNextEntry() {

		EditText editText;
		//					Toast.makeText(getBaseContext(),"Key Pressed currentTextId: "+ String.valueOf(currentEditTextId) + String.valueOf(lastRecordId) , Toast.LENGTH_SHORT).show();
		if( currentDataEntryID < lastRecordId ){
			editText = (EditText) findViewById(currentDataEntryID+1);
		}else{
			editText = (EditText) findViewById(currentDataEntryID);
		}

		editText.requestFocus();
		horizontalScrollViewColumnHeader.scrollTo(horizontalScrollViewData.getScrollX(), 0);
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


	private TextView createDataEntryLabel(String txtValue,int column,String recordId,int textFieldId){
		TextView txtView=new TextView(this);

		txtView.setText(txtValue);
		txtView.setTextSize(TEXT_SIZE);
		txtView.setGravity(Gravity.LEFT);
		txtView.setMaxLines(1);
		txtView.setWidth(FACTOR_TEXT_WIDTH);

		if(column==0){
			txtView.setTextColor(Color.BLACK);
			txtView.setBackgroundColor(Color.LTGRAY);
			txtView.setText(String.valueOf(recordNo));
			txtView.setWidth(70);
			txtView.setTextSize(20);
			recNoOfRecId.put(recordNo,txtValue);
			recIdOfRecNo.put(txtValue, recordNo);

			recordNo++;

		}else{
			txtView.setTextColor(Color.BLACK);
		}

		if(newRecId > 0){
			//			Toast.makeText(getBaseContext(),String.valueOf(searchRecordByFieldValue) + " "+ firstSearch, Toast.LENGTH_SHORT).show();
			if(barcodeReference.toLowerCase().contains("barcode")){
				column=0;
			}
			if(txtValue.equals(String.valueOf(valueOfSearchRecord))&& !firstSearch && column!=0){
				txtView.setTextColor(Color.YELLOW);
				txtView.setBackgroundColor(Color.BLACK);
				firstSearch=true;
				displayFactorInfo(Integer.valueOf(recordId),String.valueOf(valueOfSearchRecord));
				txtViewSearchId=textFieldId;

			}else{
				if(txtValue.equals(String.valueOf(valueOfSearchRecord))&& !firstSearch && column==0){
					txtView.setTextColor(Color.YELLOW);
					txtView.setBackgroundColor(Color.BLACK);
					firstSearch=true;
					displayFactorInfo(Integer.valueOf(recordId),String.valueOf(valueOfSearchRecord));
					txtViewSearchId=textFieldId;
				}
			}

		}
		return txtView;
	}


	// Column Header Display
	private TextView labelDataEntryColumnHeader(String txtValue,String type){
		int txtwidth=0;
		TextView txtView=new TextView(this);
		txtView.setText(txtValue);
		txtView.setTextSize(22);

		if(type.equals("textfield")){
			txtView.setBackgroundColor(Color.rgb(0, 0, 128));
			txtView.setGravity(Gravity.CENTER_HORIZONTAL);
			txtView.setWidth(VARIATE_TEXT_WIDTH);
		}else{
			txtView.setBackgroundColor(Color.rgb(0, 100, 0));	
			txtView.setGravity(Gravity.LEFT);
			txtView.setWidth(FACTOR_TEXT_WIDTH);
		}


		if(txtValue.contains("REC#")){
			txtView.setTextColor(Color.GRAY);
			txtView.setWidth(180);
		}else{
			txtView.setTextColor(Color.WHITE);
		}

		return txtView;
	}


	private Button createImageButton(final String photoReferenceCode){
		Button btn=new Button(this);
		btn.setBackgroundResource(R.drawable.imagecapture);
		btn.setPadding(20, 0, 20, 0);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				settingsManager.updateLastRecord(startLimit);
				saveAllDataEntry();
				Intent i = new Intent(DataEntryFormContinuousActivity.this, ImageListViewActivity.class);
				i.putExtra("DBNAME", databaseName);
				i.putExtra("FOLDER_PATH",FieldLabPath.IMAGE_FOLDER+databaseName);
				i.putExtra("PHOTOREFERENCECODE",photoReferenceCode );
				i.putExtra("CURRENTTEXTID",currentDataEntryID );
				startActivityForResult(i,IMAGE_CAPTURE_ACTIVITY);
			}
		});
		return btn;
	}

	private Button createAudioButton(final String audioReferenceCode){

		Button btn=new Button(this);
		btn.setBackgroundResource(R.drawable.sound);
		btn.setPadding(0, 0, 20, 0);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				settingsManager.updateLastRecord(startLimit);
				saveAllDataEntry();
				Intent iAudio = new Intent(DataEntryFormContinuousActivity.this,AudioRecordingActivity.class);
				iAudio.putExtra("DBNAME", databaseName);
				String audioName=databaseName+"_"+audioReferenceCode+"_"+getTraitCodeSelected();
				iAudio.putExtra("AUDIO_NAME",audioName );
				iAudio.putExtra("CURRENTTEXTID",currentDataEntryID );
				iAudio.putExtra("AUDIOREFERENCECODE",audioReferenceCode );
				startActivityForResult(iAudio, AUDIO_CAPTURE_ACTIVITY);
			}
		});
		return btn;
	}

	protected String getTraitCodeSelected() {
		// TODO Auto-generated method stub
		return currentTraitCodeSelected;
	}


	// Menu

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

	private void CreateMenu(Menu menu)
	{
		MenuItem mnu1 = menu.add(0, 0, 0, "Factor and Variate");
		{
			mnu1.setIcon(R.drawable.trait_list_icon);
		}
		MenuItem mnu2 = menu.add(0, 1, 1, "Ranges Input");
		{
			mnu2.setIcon(R.drawable.edit);
		}
		MenuItem mnu3 = menu.add(0, 2, 1, "Sort ASC");
		{
			mnu3.setIcon(R.drawable.asc);
		}
		MenuItem mnu4 = menu.add(0, 3, 1, "Sort DESC");
		{
			mnu4.setIcon(R.drawable.desc);
		}
		MenuItem mnu6 = menu.add(0, 4, 1, "Settings");
		{
			mnu6.setIcon(R.drawable.settings_icon);
		}

		MenuItem mnu7 = menu.add(0, 5, 1, "Connect To Reader");
		{
			mnu7.setIcon(R.drawable.baracoda);
		}


	}


	private boolean MenuChoice(MenuItem item)
	{
		switch (item.getItemId()) {
		case 0:
			settingsManager.updateLastRecord(startLimit);
			saveAllDataEntry();
			Intent i1 = new Intent(DataEntryFormContinuousActivity.this,DescriptionFactorVariateActivity.class);
			i1.putExtra("DBNAME", databaseName);
			startActivityForResult(i1, TRAIT_DISPLAY);
			return true;
		case 1:
			if(currentTraitCodeSelected.length() > 0){
				String[] ref= dataEntryMap.get(currentDataEntryID).toString().split(":");
				Intent i2 = new Intent(DataEntryFormContinuousActivity.this, RangeInputActivity.class);
				i2.putExtra("DBNAME",databaseName);
				i2.putExtra("DTYPE",ref[2]);
				i2.putExtra("TOTALRECORD",String.valueOf(totalRecord));
				i2.putExtra("RECORDNO",recIdOfRecNo.get(ref[0]).toString());
				i2.putExtra("TRAITCODE",currentTraitCodeSelected);
				startActivityForResult(i2, RANGES_INPUT_ACTIVITY);
				return true;

			}else{
				Toast.makeText(getBaseContext(),"No trait selected...", Toast.LENGTH_SHORT).show();
			}
			return false;
		case 2:
			displayOrder="ASC";
			saveAllDataEntry();
			allDataEntryEditTextField.clear();
			dataEntryMap.clear();
			linearLayoutDataEntry.removeAllViewsInLayout();
			displayRecord();
			return true;

		case 3:
			displayOrder="DESC";
			saveAllDataEntry();
			allDataEntryEditTextField.clear();
			dataEntryMap.clear();
			linearLayoutDataEntry.removeAllViewsInLayout();
			displayRecord();
			return true;
		case 4:
			settingsManager.updateLastRecord(startLimit);
			saveAllDataEntry();
			Intent intentSettings = new Intent(DataEntryFormContinuousActivity.this,DataEntrySettingsActivity.class);
			intentSettings.putExtra("DBNAME", databaseName);
			startActivityForResult(intentSettings, DATA_ENTRY_SETTINGS);
			return true;
		case 5:
			Intent i = new Intent(DataEntryFormContinuousActivity.this,BarcodeScannerConnectActivity.class);
			i.putExtra("CONNECTION_STATE", connetionState );
			i.putExtra("CURRENT_DEVICE", currentDevice );
			i.putExtra("DBNAME", databaseName);
			startActivityForResult(i, BARACODA_CONNECTION_ACTIVITY);
			return true;
		}
		return false;

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		saveAllDataEntry();
		//		Toast.makeText(getBaseContext(),"Start " +String.valueOf(startLimit), Toast.LENGTH_SHORT).show();
		resetLayout();
		initDataEntry();
		displayColumnHeader();
		initRecordDisplay(startLimit);
		EditText txtEditCurrent;
		String score; 
		int oldEditTextId;

		if (intent != null) {
			Bundle extras = intent.getExtras();

			switch (requestCode) {
			case 0:
				break;
			case REMARKS_ACTIVITY:
				String remarks=extras.getString("REMARKS");
				oldEditTextId=extras.getInt("CURRENT_EDITTEXT_ID");
				EditText editText = (EditText) findViewById(oldEditTextId);
				editText.setText(remarks);
				editText.requestFocus();
				break;
			case RANGES_INPUT_ACTIVITY:

				int startRec = Integer.valueOf(extras.getString("STARTREC"));
				int endRec= Integer.valueOf(extras.getString("ENDREC"));
				String traitcode=extras.getString("TRAITCODE");
				String value=extras.getString("VALUE");

				for(int i=startRec;i<=endRec;i++){
					int recId=Integer.valueOf(recNoOfRecId.get(i).toString());
					observationManager.updateRecord(recId,traitcode,value);
				}

				resetLayout();
				initDataEntry();
				displayColumnHeader();
				initRecordDisplay(startRec);
				break;
			case ACTIVITY_CALENDAR:
				dateFormat = new SimpleDateFormat("yyyy/MM/dd"); 
				int prevID=extras.getInt("CURRENT_EDITTEXT_ID");
				String prevDtype=extras.getString("DTYPE");
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
			case BARACODA_CONNECTION_ACTIVITY:
				databaseName=extras.getString("DBNAME");
				connetionState = extras.getInt("CONNECTION_STATE");
				currentDevice = extras.getString("CURRENT_DEVICE");
				startBarcodaService();
				//				Toast.makeText(DataEntryFormSingleActivity.this,String.valueOf(connetionState), Toast.LENGTH_SHORT).show();
				break;
			case PHOTO_CAPTURE_ACTIVITY:
				oldEditTextId=extras.getInt("CURRENTTEXTID");
				txtEditCurrent = (EditText) findViewById(oldEditTextId);
				txtEditCurrent.requestFocus();
				break;

			case SCORING_MORPHO_ACTIVITY:

				prevID=extras.getInt("CURRENTTEXTID");
				score = extras.getString("SCORE_SELECTED");
				txtEditCurrent = (EditText) findViewById(prevID);
				txtEditCurrent.setText(score.trim());
				moveToNextEntry();
				break;

			case AUDIO_CAPTURE_ACTIVITY:
				oldEditTextId=extras.getInt("CURRENTTEXTID");
				txtEditCurrent = (EditText) findViewById(oldEditTextId);
				txtEditCurrent.requestFocus();
				break;

			case IMAGE_CAPTURE_ACTIVITY:
				oldEditTextId=extras.getInt("CURRENTTEXTID");
				txtEditCurrent = (EditText) findViewById(oldEditTextId);
				txtEditCurrent.requestFocus();
				break;
			}


		}
	}

	private void saveAllDataEntry(){
		Set set = dataEntryMap.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			String editTextvalue=String.valueOf(allDataEntryEditTextField.get(Integer.valueOf(String.valueOf(me.getKey()))).getText());
			String[] field=String.valueOf(me.getValue()).split(":");
			//			Toast.makeText(getBaseContext(),Integer.valueOf(field[0])+":"+field[1]+":"+editTextvalue, Toast.LENGTH_SHORT).show();
			observationManager.updateRecord(Integer.valueOf(field[0]),field[1],editTextvalue);
		} 
		dataEntryMap.clear();
		allDataEntryEditTextField.clear();
	}

	private void resetLayout() {
		linearLayoutDataEntry.removeAllViewsInLayout();
		linearLayoutColumnHeader.removeViewAt(0);
		linearLayoutColumnHeader.invalidate();
		linearLayoutColumnHeader.refreshDrawableState();
		observationColumnMetaData=getObsevationColumnActive(); 

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
					EditText editText = (EditText) findViewById(previousDataEntryId);
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
					EditText editText = (EditText) findViewById(currentDataEntryID);
					editText.setTextColor(Color.BLACK);
					editText.requestFocus();
				}
			})

			.setNegativeButton("Cancel", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					EditText editText = (EditText) findViewById(previousDataEntryId);
					editText.setText("");
					editText.requestFocus();


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
					EditText editText = (EditText) findViewById(currentDataEntryID);
					editText.setText(items[item].split(":")[0].trim());
					moveToNextEntry();
				}
			})
			.create();
		case 3:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.warning)
			.setTitle("Warning Message")
			.setMessage("To Search Record, please specify a unique column in Setting Menu")
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
			.setMessage("To Take Photo, please specify a unique column in Setting Menu")
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{

				}
			})
			.create();

		case 5:

			EditText editText = (EditText) findViewById(previousDataEntryId);
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
					EditText editText = (EditText) findViewById(previousDataEntryId);
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
					EditText editText = (EditText) findViewById(previousDataEntryId);
					editText.setText(oldTextValue1);
					editText.requestFocus();
					oldTextValue= editText.getText().toString();
				}
			})
			.create();
		}
			return  dialog;

		}


		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			settingsManager.updateLastRecord(startLimit);
			saveAllDataEntry();
			databaseConnect.close();
			finish();
		}



		// Baracoda Function

		private void startBarcodaService(){
			//		Toast.makeText(getBaseContext(),"Start Baracoda Service Start", Toast.LENGTH_SHORT).show();
			Intent baracodaServiceIntent = new Intent (IBaracodaReaderService.class.getName());
			// Start the service if not already running
			super.startService(baracodaServiceIntent);
			// Bind to the service
			super.bindService(baracodaServiceIntent, this.baracodaServiceConnection, Context.BIND_AUTO_CREATE);
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}

		private final ServiceConnection baracodaServiceConnection = new ServiceConnection () {
			public void onServiceConnected(ComponentName className, IBinder service) {

				try {
					DataEntryFormContinuousActivity.this.baracodaService = IBaracodaReaderService.Stub.asInterface(service);
					DataEntryFormContinuousActivity.this.baracodaService.registerCallback(DataEntryFormContinuousActivity.this.baracodaServiceCallback);

					// We are not using application ack in this example, let's save its state
					applicationAckAtStartup = DataEntryFormContinuousActivity.this.baracodaService.getApplicationAck();
					// Disable application ack
					DataEntryFormContinuousActivity.this.baracodaService.setApplicationAck(false);

					// We are not using raw mode in this example, let's save its state
					rawModeAtStartup = DataEntryFormContinuousActivity.this.baracodaService.getRawMode();
					// Disable raw mode
					DataEntryFormContinuousActivity.this.baracodaService.setRawMode(false);
				}
				catch (RemoteException e) {
					// nothing to do here
				}
			}

			public void onServiceDisconnected(ComponentName className) {
				DataEntryFormContinuousActivity.this.baracodaService = null;
			}
		};

		private final IBaracodaReaderServiceCallback.Stub baracodaServiceCallback = new IBaracodaReaderServiceCallback.Stub() {
			@Override
			public void onAutoConnectChanged() throws RemoteException{
				// We need to switch to our handler (to avoid threading issues with GUI)
				// Autoconnect state has changed
				DataEntryFormContinuousActivity.this.messageHandler.obtainMessage(DataEntryFormContinuousActivity.MESSAGE_ON_AUTOCONNECT_STATE_CHANGED,
						0, 0).sendToTarget();
			}

			@Override
			public void onConnectionStateChanged(int connectionState) throws RemoteException {
				// We need to switch to our handler (to avoid threading issues with GUI)
				// Connection state has changed
				DataEntryFormContinuousActivity.this.messageHandler.obtainMessage(DataEntryFormContinuousActivity.MESSAGE_ON_CONNECTION_STATE_CHANGED,
						connectionState, 0).sendToTarget();
			}

			@Override
			public void onDataRead(int dataType, String dataText) throws RemoteException {
				// We need to switch to our handler (to avoid threading issues with GUI)
				// Data has been received from the reader

				DataEntryFormContinuousActivity.this.messageHandler.obtainMessage(
						DataEntryFormContinuousActivity.MESSAGE_ON_DATA_READ,
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
				DataEntryFormContinuousActivity.this.messageHandler.obtainMessage(DataEntryFormContinuousActivity.MESSAGE_ON_READER_CHANGED).sendToTarget();
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

					case DataEntryFormContinuousActivity.MESSAGE_ON_DATA_READ:
						if(msg.arg1 == DataType.BARCODE) {
							StringBuilder sb = new StringBuilder((String)msg.obj);
							String valueBarcode=(String)msg.obj;

							if(isTextFieldSearchFocus){
								txtFieldSearchValue.requestFocus();
								txtFieldSearchValue.setText(valueBarcode);
								searchRecord(valueBarcode);
							}else{

								EditText editText = (EditText) findViewById(currentDataEntryID);
								editText.setText(valueBarcode);
								isTextFieldSearchFocus=false;
							}
						}
						break;
					}
				}finally{

				}
			}
		};

		public static boolean isNumber(String string) {
			return string != null && numberPattern.matcher(string).matches();
		}

		public static String now() {
			Calendar cal = Calendar.getInstance();
			return dateFormat.format(cal.getTime());

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


		//Scroll 
		public interface OnScrollStoppedListener{
			void onScrollStopped();
		}

	}
