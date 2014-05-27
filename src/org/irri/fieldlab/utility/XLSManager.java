package org.irri.fieldlab.utility;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class XLSManager extends Activity {
	
	
	public XLSManager(){
		
	}

	
	public static Intent openXLS(String filename){
		MaintenanceManager maintenanceManager= new MaintenanceManager();
		try {
			Intent intent= maintenanceManager.getIntent(filename);
			return intent;
		} 
		catch (ActivityNotFoundException e) {
			// Toast.makeText(OpenPdf.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	
	public static Intent openPowerpoint(String filename){
		MaintenanceManager maintenanceManager= new MaintenanceManager();
		try {
			Intent intent= maintenanceManager.getIntent(filename);
			return intent;
		} 
		catch (ActivityNotFoundException e) {
			// Toast.makeText(OpenPdf.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	
}
