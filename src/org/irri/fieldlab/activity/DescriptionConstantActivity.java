package org.irri.fieldlab.activity;

import java.util.ArrayList;
import java.util.List;

import org.irri.fieldlab.adapter.DescriptionConstantArrayAdapter;
import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.model.DescriptionModel;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

public class DescriptionConstantActivity extends ListActivity {

	/** Called when the activity is first created. */
//	private static String SNAME = "";
	private String databaseName;
	private DatabaseConnect databaseConnect;
	private DescriptionManager descriptionManager;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Create an array of Strings, that will be put to our ListActivity
		setContentView(R.layout.traitconstant);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Trait List: ");
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			databaseName = extras != null ? extras.getString("DBNAME") : null;
		}

		databaseConnect= new DatabaseConnect(DescriptionConstantActivity.this, databaseName);
		databaseConnect.openDataBase();
		descriptionManager=new DescriptionManager(databaseConnect.getDataBase());

		ArrayAdapter<DescriptionModel> adapter = new DescriptionConstantArrayAdapter(DescriptionConstantActivity.this,
				getStudyVariate(), descriptionManager);
		this.setListAdapter(adapter);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private List<DescriptionModel> getStudyVariate() {
		List<DescriptionModel> list = new ArrayList<DescriptionModel>();
		list.clear();

		Cursor cursorCondition = descriptionManager.getConstant();

		int i=0;


		if (cursorCondition.moveToFirst()){

			do {

				list.add(get(
						cursorCondition.getInt(0), // id
						cursorCondition.getString(1), // traitcode
						cursorCondition.getString(2), // description
						cursorCondition.getString(3), // property
						cursorCondition.getString(4), // scale
						cursorCondition.getString(5), // method
						cursorCondition.getString(6), // datatype
						cursorCondition.getString(7) // value
						));
				i++;
			} while (cursorCondition.moveToNext());
		}

		// Initially select one of the items
		cursorCondition.close();
		return list;
	}

	private DescriptionModel get(long id,
			String traitcode,
			String description, 
			String property, 
			String scale,
			String method,
			String datatype,
			String value){
	
		return new DescriptionModel(id,
				traitcode,
				description, 
				property, 
				scale,
				method,
				datatype,
				value);
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

