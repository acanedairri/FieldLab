package org.irri.fieldlab.activity;

import org.irri.fieldlab.utility.FieldLabPath;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

public class ScoringMorphoActivity extends Activity {

	private TableLayout tableData;
	private String folder="";
	private String filename;
	private Object score;
	private int currentEditTextId;
	private String scores;
	private String currentTraitCodeSelected;
	private String traitDescription;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scoring_morpho);
		tableData = (TableLayout) findViewById(R.id.tableImage);

		Bundle extras = getIntent().getExtras();
		currentEditTextId=extras.getInt("CURRENTTEXTID");
		scores=extras.getString("SCORES");
		traitDescription=extras.getString("TRAITDESCRIPTION");
		currentTraitCodeSelected=extras.getString("CURRENT_TRAITCODE_SELECTED");
		//		Toast.makeText(getBaseContext(),"calendar ID "+String.valueOf(currentEditTextId), Toast.LENGTH_SHORT).show();
		setTitle("Scoring for :"+traitDescription);
		
		String[] scoring =scores.split("\n");

		for(int i=0;i<scoring.length;i++){

			String[] scoreValue=scoring[i].split(":");
			folder=scoreValue[3];
			filename=scoreValue[2];
			score=scoreValue[0];
			TableRow tR = new TableRow(this);
			tR.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			ImageButton newImgBtn = new ImageButton(this);

			String picture=FieldLabPath.MORPHO_FOLDER+folder+"/"+filename;
			Bitmap bmp = BitmapFactory.decodeFile(picture.trim());
			newImgBtn.setImageBitmap(bmp);
			newImgBtn.setTag(score);
			newImgBtn.setOnClickListener(new OnClickListener(){

				public void onClick(View arg0) {
//					Toast.makeText(getApplicationContext(), (CharSequence) arg0.getTag(), Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
			    	bundle.putString("SCORE_SELECTED",  arg0.getTag().toString());
			       	bundle.putInt("CURRENTTEXTID", currentEditTextId);
			    	Intent mIntent = new Intent();
			    	mIntent.putExtras(bundle);
			    	setResult(RESULT_OK, mIntent);
			    	finish();
				}});

			tR.addView(newImgBtn);
			tableData.addView(tR);
		}

	}
}
