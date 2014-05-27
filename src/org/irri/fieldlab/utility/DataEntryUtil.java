package org.irri.fieldlab.utility;

import org.irri.fieldlab.database.manager.ScoringManager;

import android.database.Cursor;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class DataEntryUtil {
	
	public static void setEditTextRed(EditText editText){
		editText.setTextColor(Color.RED);
		editText.setTextSize(40);
	}

	public static void setEditTextBlack(EditText editText){
		editText.setTextColor(Color.BLACK);
		editText.setTextSize(27);
	}
	
	public static String getScoring(String traitcode, ScoringManager scoringManager){
		String scoreDescription = "";
		Cursor cursor=scoringManager.getScores(traitcode);
		if(cursor.getCount() > 0){
			if (cursor.moveToFirst()){

				do {
					scoreDescription+= cursor.getString(0)+" : "+cursor.getString(1)+":"+cursor.getString(2)+":"+cursor.getString(3)+"\n";
				} while (cursor.moveToNext());

			}
		}
		return scoreDescription;
	}
	
	public static void setEditTextInputType(String dtype, View v) {
		if(dtype.equals("C")){ // ||   dtype.equals("CD")
			((EditText) v).setInputType(1);
		}else if(dtype.equals("D") ||   dtype.equals("CD")){
			//((EditText) v).setInputType(3);
			if(dtype.equals("D")){
			}
		}else{
			((EditText) v).setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		
	}


}
