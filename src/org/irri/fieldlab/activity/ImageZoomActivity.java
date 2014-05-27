package org.irri.fieldlab.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;





public class ImageZoomActivity extends Activity {
	private WebView webViewer;
	private String imgUrl;
	private ImageButton btnDelete;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagezoom);
		webViewer = (WebView) findViewById(R.id.webView);

		imgUrl = getIntent().getStringExtra("image_path");
		webViewer.loadUrl("file://"+imgUrl);
		webViewer.getSettings().setBuiltInZoomControls(true);
		//	    webViewer.getSettings().setDisplayZoomControls(true);
		webViewer.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webViewer.getSettings().setLoadWithOverviewMode(true);
		webViewer.getSettings().setUseWideViewPort(true);

		webViewer.setInitialScale(40);
		//	    WebSettings webSettings = webViewer.getSettings();
		//	    webSettings.setUseWideViewPort(true);
		//	 
		addListenerOnButton();
	}

	public void addListenerOnButton() {

		btnDelete = (ImageButton) findViewById(R.id.delete_Button);

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder adb=new AlertDialog.Builder(ImageZoomActivity.this);
				final File delFile = new File(imgUrl);
				adb.setTitle("Delete?");
				adb.setMessage("Are you sure you want to delete " + delFile.getName());
				
				adb.setNegativeButton("Cancel", null);

				adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delFile.delete();
						Bundle bundle = new Bundle();
				    	bundle.putBoolean("Delete", true);
						Intent mIntent = new Intent();
				    	mIntent.putExtras(bundle);
				    	setResult(RESULT_OK, mIntent);
						finish();
					}});
				adb.show();
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}



}
