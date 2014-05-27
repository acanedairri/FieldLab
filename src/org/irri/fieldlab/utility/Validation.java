package org.irri.fieldlab.utility;

import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.ScoringManager;

import android.database.Cursor;
import android.util.Log;

public class Validation {
	
	public Validation(){
		
	}
	
	public boolean isValidRange(String editTextValue, String traitcode,DescriptionManager descriptionManager) {

		Cursor cursor=descriptionManager.getVariateRangeValue(traitcode);
		int min = cursor.getInt(0);
		int max=cursor.getInt(1);
		Log.d("max",String.valueOf(max));
		Log.d("min",String.valueOf(min));
		double value=Double.valueOf(editTextValue);
//		int value=100;
		if(min==0 && max ==0){
			return true;
		}else{

			if( value > max || value < min  ){
				return false;
			}else{
				return true;
			}
		}
	}

	public String getValidRange(String traitcode,DescriptionManager descriptionManager) {

		Cursor cursor=descriptionManager.getVariateRangeValue(traitcode);
		int min = cursor.getInt(0);
		int max=cursor.getInt(1);
		return String.valueOf(min)+"-"+String.valueOf(max);
	}


	public boolean isValidScore(String editTextValue, String traitcode,
			ScoringManager scoringManager) {
		
		return scoringManager.isValidScore(editTextValue,traitcode);
	}

}
