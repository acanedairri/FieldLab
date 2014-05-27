package org.irri.fieldlab.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.irri.fieldlab.adapter.AudioRecordArrayListAdapter;
import org.irri.fieldlab.utility.FieldLabPath;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class AudioRecordingActivity extends Activity{

	private MediaRecorder recorder;
	private String databaseName;
	private String audioName;
	private String STUDY_AUDIO_FOLDER;
	private Calendar now;
	private Button btnCancel;
	private int currentEditTextId;
	private String audioReferenceCode;
	private boolean recording=true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiorecording);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			databaseName = extras != null ? extras.getString("DBNAME") : null;
			audioName = extras != null ? extras.getString("AUDIO_NAME") : null;
			audioReferenceCode = extras != null ? extras.getString("AUDIOREFERENCECODE") : null;
			currentEditTextId=extras.getInt("CURRENTTEXTID");
		}

		STUDY_AUDIO_FOLDER=FieldLabPath.AUDIO_FOLDER+databaseName;
    	
		
		findViewById(R.id.btnRecordAudio).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
				final Button button = (Button) arg0;
				
				
				if(recording) {
					startRecording();
					recording=false;
					button.setBackgroundResource(R.drawable.stoprecord);
				}
				else{
					new Thread(new Runnable() {
					    @Override
					    public void run() {
					        try {
					            Thread.sleep(500);
					        } catch (InterruptedException e) {
					            e.printStackTrace();
					        }
					        runOnUiThread(new Runnable() {
					            @Override
					            public void run() {

									recording=true;
									button.setBackgroundResource(R.drawable.startrecord);
									stopRecording();
									populateList();
					            }
					        });
					    }
					}).start();
					
				}
			}
				
			});
		
		btnCancel= (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

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
		
		populateList();
	}
	@Override
	public void onResume() {
	 super.onResume();

	}
	public void populateList(){
		ListView lstAudio = (ListView) findViewById(R.id.listAudio);
		File file = new File(STUDY_AUDIO_FOLDER);
	
		File[] files = file.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		    	if(!name.contains(audioReferenceCode)) return false;
		        return (name.toLowerCase().endsWith("mp4")) ? true: false ;
		    }
		});
	
		ArrayList<File> arrdata = new ArrayList<File>();
		arrdata.addAll(Arrays.asList(files));
		AudioRecordArrayListAdapter adapter = new AudioRecordArrayListAdapter(this,arrdata);
		lstAudio.setAdapter(adapter);
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
		
		}
	};

	private void startRecording() {
//		Toast.makeText(getBaseContext(),getFilename(), Toast.LENGTH_SHORT).show();
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());

		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getFilename() {
		File file = new File(STUDY_AUDIO_FOLDER);
		now = GregorianCalendar.getInstance();
		return file.getAbsolutePath() + "/"+audioName+"_"+String.valueOf(now.getTimeInMillis())+".mp4";
	}
	private void stopRecording() {
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
		}
	}
}
