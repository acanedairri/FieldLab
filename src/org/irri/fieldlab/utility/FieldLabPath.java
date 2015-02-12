package org.irri.fieldlab.utility;

import android.os.Environment;

public class FieldLabPath {

	public static String AppPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/FieldLab-V2.10";
	//static String AppPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/external_sd/FieldLab";
	public  static String STUDY_FOLDER=AppPath+"/study/";
	public  static String DEFAULT_FOLDER=AppPath+"/default/";
	public  static String WORKBOOK_FOLDER=AppPath+"/workbook/";
	public  static String MAINTENANCE_FOLDER=AppPath+"/maintenance/";
	public  static String MORPHO_FOLDER=AppPath+"/maintenance/Morpho Valid Values/";
	public  static String EXPORTDATA_FOLDER=AppPath+"/export data/";
	public  static String IMPORTDATA_FOLDER=AppPath+"/import data/";
	public  static String IMAGE_FOLDER=AppPath+"/images/";
//	public  static String IMAGE_PATH="/FieldLab/images/";
	public  static String AUDIO_FOLDER=AppPath+"/audio/";
	public  static String APPLICATION_PATH=AppPath;
}
