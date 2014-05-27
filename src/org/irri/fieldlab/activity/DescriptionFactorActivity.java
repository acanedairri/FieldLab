package org.irri.fieldlab.activity;

import java.util.ArrayList;
import java.util.List;

import org.irri.fieldlab.adapter.DescriptionFactorArrayAdapter;
import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.ScoringManager;
import org.irri.fieldlab.model.DescriptionModel;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

public class DescriptionFactorActivity extends ListActivity {

	/** Called when the activity is first created. */
//	private static String SNAME = "";
	private String databaseName;
	private DatabaseConnect databaseConnect;
	private DescriptionManager descriptionManager;
	private ScoringManager scoringManager;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Create an array of Strings, that will be put to our ListActivity
		setContentView(R.layout.traitfactor);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Trait List: ");
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			databaseName = extras != null ? extras.getString("DBNAME") : null;
//			SNAME=extras.getString("SNAME");
		}

		databaseConnect= new DatabaseConnect(DescriptionFactorActivity.this, databaseName);
		databaseConnect.openDataBase();
		descriptionManager=new DescriptionManager(databaseConnect.getDataBase());
		scoringManager=new ScoringManager(databaseConnect.getDataBase());

		ArrayAdapter<DescriptionModel> adapter = new DescriptionFactorArrayAdapter(DescriptionFactorActivity.this,
				getStudyVariate(), descriptionManager);
		this.setListAdapter(adapter);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private List<DescriptionModel> getStudyVariate() {
		List<DescriptionModel> list = new ArrayList<DescriptionModel>();
		list.clear();

		Cursor cursorTrait = descriptionManager.getFactor();

		int i=0;


		if (cursorTrait.moveToFirst()){

			do {
				
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
						cursorTrait.getString(13) // visible
						));
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
			String visible
			){
	
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
				visible
				);
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


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	//	@Override
	//	protected void onResume() {
	//		// TODO Auto-generated method stub
	//		super.onResume();
	//		ArrayAdapter<VariateModel> adapter = new VariateOfStudyArrayAdapter(VariateOfStudy.this,
	//				getStudyVariate(DB_NAME,SNAME), this.myDbHelper);
	//		this.setListAdapter(adapter);
	//		
	//	}

}

