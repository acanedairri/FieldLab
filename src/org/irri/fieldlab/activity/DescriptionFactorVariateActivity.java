package org.irri.fieldlab.activity;

import java.util.ArrayList;
import java.util.List;

import org.irri.fieldlab.adapter.DescriptionFactorVariateArrayAdapter;
import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.ObservationManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.model.DescriptionModel;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class DescriptionFactorVariateActivity extends ListActivity {

	/** Called when the activity is first created. */
	//	private static String SNAME = "";
	private String databaseName;
	private DatabaseConnect databaseConnect;
	private DescriptionManager descriptionManager;
	private ScoringManager scoringManager;
	private Button btnAddVariate;
	private Button btnClose;
	private ObservationManager observationManager;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Create an array of Strings, that will be put to our ListActivity
		setContentView(R.layout.traitfactorvariate);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Factor and Variate");
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			databaseName = extras != null ? extras.getString("DBNAME") : null;
			//			SNAME=extras.getString("SNAME");
		}

		databaseConnect= new DatabaseConnect(DescriptionFactorVariateActivity.this, databaseName);
		databaseConnect.openDataBase();
		descriptionManager=new DescriptionManager(databaseConnect.getDataBase());
		observationManager=new ObservationManager(databaseConnect.getDataBase());
		scoringManager=new ScoringManager(databaseConnect.getDataBase());

		ArrayAdapter<DescriptionModel> adapter = new DescriptionFactorVariateArrayAdapter(DescriptionFactorVariateActivity.this,
				getStudyVariate(), descriptionManager,observationManager);
		this.setListAdapter(adapter);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		btnAddVariate= (Button) findViewById(R.id.btnAddVariate);
		btnAddVariate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent(DescriptionFactorVariateActivity.this, AddVariateActivity.class);
				i.putExtra("DBNAME",databaseName);
				startActivity(i);
			}
		});

		btnClose= (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Perform action on click
				finish();
			}
		});
	}

	private List<DescriptionModel> getStudyVariate() {
		List<DescriptionModel> list = new ArrayList<DescriptionModel>();
		list.clear();

		Cursor cursorTrait = descriptionManager.getFactorVariate();

		int i=0;


		if (cursorTrait.moveToFirst()){

			do {

				String scoring = getScore(cursorTrait.getString(1));
				list.add(get(
						cursorTrait.getInt(0), // id
						cursorTrait.getString(1), // traitcode
						cursorTrait.getString(2), // description
						cursorTrait.getString(3), // property
						cursorTrait.getString(4), // scale
						cursorTrait.getString(5), // method
						cursorTrait.getString(6), // datatype
						cursorTrait.getString(7), // value
						cursorTrait.getString(8), // label
						cursorTrait.getDouble(9), // minimumvalue
						cursorTrait.getDouble(10), // maximumvalue
						cursorTrait.getString(11), // category
						cursorTrait.getString(12), // inputtype
						cursorTrait.getString(13), // visible
						scoring,cursorTrait.getString(15)));
				if(cursorTrait.getString(13).equals("Y")){
					list.get(i).setSelected(true);
				}
				i++;
			} while (cursorTrait.moveToNext());

		}

		// Initially select one of the items
		cursorTrait.close();
		return list;
	}

	private String getScore(String traitcode) {
		// TODO Auto-generated method stub

		Cursor cursorScore = scoringManager.getScoringFilterByTraitcode(traitcode);
		//startManagingCursor(variateValue);
		String traitScore="";

		if(cursorScore.getCount() > 0){
			if (cursorScore.moveToFirst()){

				do {
					traitScore+="\n"+cursorScore.getString(2).toString()+" : "+cursorScore.getString(3).toString();
				} while (cursorScore.moveToNext());

			}
		}
		cursorScore.close();
		return traitScore;
	}



	private DescriptionModel get(long id,
			String traitcode,
			String description, 
			String property, 
			String scale,
			String method,
			String datatype,
			String value,
			String label,
			double minimumvalue,
			double maximumvalue,
			String category,
			String inputType,
			String visible,
			String scoring,
			String lock){

		return new DescriptionModel(id,
				traitcode,
				description, 
				property, 
				scale,
				method,
				datatype,
				value,
				label,
				minimumvalue,
				maximumvalue,
				category,
				inputType,
				visible,
				scoring,
				lock);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		databaseConnect.close();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ArrayAdapter<DescriptionModel> adapter = new DescriptionFactorVariateArrayAdapter(DescriptionFactorVariateActivity.this,
				getStudyVariate(), descriptionManager,observationManager);
		this.setListAdapter(adapter);
	}
}

