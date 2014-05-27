/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.irri.fieldlab.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RemarksActivity extends Activity {

	private static final int ACTIVITY_BROWSE=0;

	private String databaseName = "";
	private Button btnOkRemarks;
	private Button btnCancelRemarks;

	private String remarks;

	private EditText editTextAddRemarks;

	private int currentEditTextId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remarks);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		databaseName = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("databaseName");

		if (databaseName == null) {
			Bundle extras = getIntent().getExtras();
			databaseName = extras != null ? extras.getString("DBNAME") : null;
			remarks=extras != null ? extras.getString("REMARKS") : "";
			currentEditTextId=extras.getInt("CURRENTTEXTID");
		}
		
		editTextAddRemarks = (EditText) findViewById(R.id.txtAddRemarks);
		editTextAddRemarks.setText(remarks);

		btnOkRemarks = (Button) findViewById(R.id.btnOkRemarks);
		btnCancelRemarks = (Button) findViewById(R.id.btnCancelRemarks);

		btnOkRemarks.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle bundle = new Bundle();
		    	bundle.putString("REMARKS", editTextAddRemarks.getText().toString());
		       	bundle.putInt("CURRENT_EDITTEXT_ID", currentEditTextId);
		    	Intent mIntent = new Intent();
		    	mIntent.putExtras(bundle);
		    	setResult(RESULT_OK, mIntent);
		    	finish();
			}
		});

		btnCancelRemarks.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				finish();
			}
		});

		
	}

	


	

}
