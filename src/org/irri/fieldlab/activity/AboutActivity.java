package org.irri.fieldlab.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class AboutActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
			
	}
	
	
}