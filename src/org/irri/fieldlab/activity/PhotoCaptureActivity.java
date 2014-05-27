package org.irri.fieldlab.activity;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.irri.fieldlab.database.manager.FieldLabManager;
import org.irri.fieldlab.utility.FieldLabPath;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoCaptureActivity extends Activity 
{
	protected Button btnTakePhoto;
	private Button btnCloseCamera;
	protected ImageView _image;
	protected TextView _field;
	protected String photoFile;
	protected boolean _taken;
	private String databaseName;
	private String photoName;
	private int currentEditTextId;
	String STUDY_IMAGE_FOLDER;
	Calendar now;
	protected static final String PHOTO_TAKEN	= "photo_taken";
		
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.photocapture);
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			databaseName = extras != null ? extras.getString("DBNAME") : null;
			photoName = extras != null ? extras.getString("PHOTO_NAME") : null;
			currentEditTextId=extras.getInt("CURRENTTEXTID");
//			SNAME=extras.getString("SNAME");
		}
        
        _image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        btnTakePhoto = ( Button ) findViewById( R.id.button );
        btnTakePhoto.setOnClickListener( new ButtonClickHandler() );
    	STUDY_IMAGE_FOLDER=FieldLabPath.IMAGE_FOLDER+databaseName;
    	now = GregorianCalendar.getInstance();
        photoFile = STUDY_IMAGE_FOLDER + "/"+photoName+"_"+String.valueOf(now.getTimeInMillis())+".jpg";
        
        File file = new File( photoFile );
    	Uri outputFileUri = Uri.fromFile( file );
    	
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	startActivityForResult( intent, 0 );
    	
    	btnCloseCamera= (Button) findViewById(R.id.btnCloseCamera);
    	btnCloseCamera.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on clicks
				Bundle bundle = new Bundle();       	
				bundle.putInt("CURRENTTEXTID", currentEditTextId);
		    	Intent mIntent = new Intent();
		    	mIntent.putExtras(bundle);
		    	setResult(RESULT_OK, mIntent);
		    	finish();
			}
		});
 
        
    }
    
    public class ButtonClickHandler implements View.OnClickListener 
    {
    	public void onClick( View view ){
    		Log.i("MakeMachine", "ButtonClickHandler.onClick()" );
    		startCameraActivity();
    	}
    }
    
    protected void startCameraActivity()
    {
    	Log.i("MakeMachine", "startCameraActivity()" );
    	now = GregorianCalendar.getInstance();
        photoFile = STUDY_IMAGE_FOLDER + "/"+photoName+"_"+String.valueOf(now.getTimeInMillis())+".jpg";
    	File file = new File( photoFile );
    	Uri outputFileUri = Uri.fromFile( file );
    	
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	startActivityForResult( intent, 0 );
 
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	Log.i( "MakeMachine", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "MakeMachine", "User cancelled" );
    			break;
    			
    		case -1:
    			onPhotoTaken();
    			break;
    	}
    	
		Bundle bundle = new Bundle();       	
		bundle.putInt("CURRENTTEXTID", currentEditTextId);
    	Intent mIntent = new Intent();
    	mIntent.putExtras(bundle);
    	setResult(RESULT_OK, mIntent);
    	
    	//finish();
    }
    
    protected void onPhotoTaken()
    {
    	Log.i( "MakeMachine", "onPhotoTaken" );
    	
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
    	
    	Bitmap bitmap = BitmapFactory.decodeFile( photoFile, options );
    	
    	_image.setImageBitmap(bitmap);
    	
    	_field.setVisibility( View.GONE );
    }
    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){
    	Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( PhotoCaptureActivity.PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
    	outState.putBoolean( PhotoCaptureActivity.PHOTO_TAKEN, _taken );
    }
}