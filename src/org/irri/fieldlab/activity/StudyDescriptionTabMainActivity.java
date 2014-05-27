package org.irri.fieldlab.activity;

import org.irri.fieldlab.database.helper.DatabaseConnect;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.FieldLabManager;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class StudyDescriptionTabMainActivity extends TabActivity {
	
private String databaseName;
private DatabaseConnect databaseConnect;
private FieldLabManager fieldLabManager;
private DescriptionManager descriptionManager;
private TextView lblStudy;
private TextView lblTitle;
private TextView lblObjective;
private TextView lblPmkey;
private TextView lblStartDate;
private TextView lblEndDate;
private TextView lblStudyType;
private TextView studyTypeLabel;

//	 private Button btnNextSiteInfo;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studydescriptiontabmain);
 
        Bundle extras = getIntent().getExtras();

		if (extras != null) {
			databaseName = extras != null ? extras.getString("DBNAME") : null;
//			SNAME=extras.getString("SNAME");
		}
        
	
		
		lblStudy =(TextView) findViewById(R.id.lblStudy);
		lblTitle =(TextView) findViewById(R.id.lblTitle);
		lblObjective =(TextView) findViewById(R.id.lblObjective);
		lblPmkey =(TextView) findViewById(R.id.lblPmkey);
		lblStartDate =(TextView) findViewById(R.id.lblStartDate);
		lblEndDate =(TextView) findViewById(R.id.lblEndDate);
		lblStudyType =(TextView) findViewById(R.id.lblStudyType);
		studyTypeLabel = (TextView) findViewById(R.id.studyType);
		init();
		
        TabHost tabHost = getTabHost();
        tabHost.setup();
        
        
        TextView txtTabCondition = new TextView(this);
        txtTabCondition.setText("CONDITION");
        txtTabCondition.setPadding(8, 9, 8, 9);
        txtTabCondition.setTextColor(Color.WHITE);
        txtTabCondition.setTextSize(22);
        txtTabCondition.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        txtTabCondition.setBackgroundResource(R.drawable.green1);
        
        TabSpec conditionTab=tabHost.newTabSpec("Factor");
        conditionTab.setIndicator(txtTabCondition);
        final Intent conditionIntent= new Intent(this, DescriptionConditionActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        conditionIntent.putExtra("DBNAME", databaseName);
        conditionTab.setContent(conditionIntent);
        
        
        TextView txtTabFactor = new TextView(this);
        txtTabFactor.setText("FACTOR");
        txtTabFactor.setPadding(8, 9, 8, 9);
        txtTabFactor.setTextColor(Color.WHITE);
        txtTabFactor.setTextSize(22);
        txtTabFactor.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        txtTabFactor.setBackgroundResource(R.drawable.green2);
        
        TabSpec factorTab=tabHost.newTabSpec("Factor");
        factorTab.setIndicator(txtTabFactor);
        final Intent factorIntent= new Intent(this, DescriptionFactorActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        factorIntent.putExtra("DBNAME", databaseName);
        factorTab.setContent(factorIntent);
        
        
        TextView txtTabVariate = new TextView(this);
        txtTabVariate.setText("VARIATE");
        txtTabVariate.setPadding(8, 9, 8, 9);
        txtTabVariate.setTextColor(Color.WHITE);
        txtTabVariate.setTextSize(22);
        txtTabVariate.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        txtTabVariate.setBackgroundResource(R.drawable.blue1);
        
        TabSpec variateTab=tabHost.newTabSpec("Variate");
        variateTab.setIndicator(txtTabVariate);
        Intent variateIntent= new Intent(this, DescriptionVariateActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        variateIntent.putExtra("DBNAME", databaseName);
        variateTab.setContent(variateIntent);

        TextView txtTabConstant = new TextView(this);
        txtTabConstant.setText("CONSTANT");
        txtTabConstant.setPadding(8, 9, 8, 9);
        txtTabConstant.setTextColor(Color.WHITE);
        txtTabConstant.setTextSize(22);
        txtTabConstant.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        txtTabConstant.setBackgroundResource(R.drawable.blue2);
        
        
        TabSpec constantTab=tabHost.newTabSpec("Constant");
        constantTab.setIndicator(txtTabConstant);
        Intent constantIntent= new Intent(this, DescriptionConstantActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        constantIntent.putExtra("DBNAME", databaseName);
        constantTab.setContent(constantIntent);
        
        tabHost.addTab(conditionTab);
        tabHost.addTab(factorTab);
        tabHost.addTab(variateTab);
        tabHost.addTab(constantTab);
    }
    
    
    private void init(){
		databaseConnect= new DatabaseConnect(StudyDescriptionTabMainActivity.this, databaseName);
		databaseConnect.openDataBase();
		fieldLabManager= new FieldLabManager(databaseConnect.getDataBase());
		descriptionManager=fieldLabManager.getDescriptionManager();
		
		
		lblStudy.setText(descriptionManager.getStudyName());
		lblTitle.setText(descriptionManager.getStudyTitle());
		lblPmkey.setText(descriptionManager.getStudyPMKey());
		lblObjective.setText(descriptionManager.getStudyObjective());	
		lblStartDate.setText(descriptionManager.getStudyStartDate());
		lblEndDate.setText(descriptionManager.getStudyEndDate());
		String studyType=descriptionManager.getStudyType();
		Log.d("StudyType", studyType);
		if(studyType.equals("")){
			Log.d("StudyTypeGone", studyType);
			lblStudyType.setText(studyType);
			studyTypeLabel.setVisibility(View.GONE);
		}else{
			Log.d("StudyTypeVisible", studyType);
			lblStudyType.setText(studyType);
			studyTypeLabel.setVisibility(View.VISIBLE);
		}
    }

}
