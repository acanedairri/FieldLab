package org.irri.fieldlab.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.util.Log;

public class FileManager {


	public FileManager(){

	}

	public void createFieldLabFolders(AssetManager assets) throws IOException  {
		// TODO Auto-generated method stub

		File f = new File(FieldLabPath.APPLICATION_PATH);

		if(f.exists() && f.isDirectory()){
			return;

		}else{
			File FieldLabDirectory = new File(FieldLabPath.APPLICATION_PATH);
			FieldLabDirectory.mkdirs();
			
			File fStudy = new File(FieldLabPath.STUDY_FOLDER);
			fStudy.mkdirs();
			
			File fWorkbook = new File(FieldLabPath.WORKBOOK_FOLDER);
			fWorkbook.mkdirs();
			
			File fMaintenance = new File(FieldLabPath.MAINTENANCE_FOLDER);
			fMaintenance.mkdirs();
			
			File fMorphoValue = new File(FieldLabPath.MORPHO_FOLDER);
			fMorphoValue.mkdirs();
			
			File fExportData = new File(FieldLabPath.EXPORTDATA_FOLDER);
			fExportData.mkdirs();
			
			File fImportData = new File(FieldLabPath.IMPORTDATA_FOLDER);
			fImportData.mkdirs();
			
			File fImageData = new File(FieldLabPath.IMAGE_FOLDER);
			fImageData.mkdirs();
			
			File fAudioData = new File(FieldLabPath.AUDIO_FOLDER);
			fAudioData.mkdirs();

			CopyFileFromAssets(assets);
			
		}
	}
	
	public void copyICISWorkbookTemplate(String fileName) throws IOException{
		
		InputStream inRemarks=new FileInputStream(FieldLabPath.WORKBOOK_FOLDER+fileName+".xls");
		OutputStream outRemarks=new FileOutputStream(FieldLabPath.EXPORTDATA_FOLDER+fileName+".xls");
		copyFile(inRemarks, outRemarks);
		inRemarks.close();
		outRemarks.close();
		
	}
	
	private  void CopyFileFromAssets(AssetManager assets) throws IOException {
		String[] files = null;
		try {
			files = assets.list("");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
//		for(String filename : files) {
//			InputStream in = null;
//			OutputStream out = null;
//			try {
//				in = assets.open(filename);
//				
//				if(filename.contains("remarks") || filename.contains("scoring") || filename.contains("traits")){
//					out = new FileOutputStream(FieldLabPath.MAINTENANCE_FOLDER + filename);
//				}
//				else if(filename.contains("Template")){
//					out = new FileOutputStream(FieldLabPath.WORKBOOK_FOLDER + filename);
//				}else if(filename.contains("testdb")){
//					out = new FileOutputStream(FieldLabPath.STUDY_FOLDER + filename);  
//				
//				}else{
//					//out = new FileOutputStream(new FieldLabPath().STUDY_FOLDER + filename);  
//				}
//				copyFile(in, out);
//				in.close();
//				in = null;
//				out.flush();
//				out.close();
//				out = null;
//			} catch(Exception e) {
//				Log.e("tag", e.getMessage());
//
//			}      
//		}
		
		
		try {
			
			InputStream inRemarks=assets.open("remarks.xls");
			OutputStream outRemarks=new FileOutputStream(FieldLabPath.MAINTENANCE_FOLDER+"remarks.xls");
			copyFile(inRemarks, outRemarks);
			inRemarks.close();
			outRemarks.close();
			
			InputStream inScoring=assets.open("scoring.xls");
			OutputStream outScoring=new FileOutputStream(FieldLabPath.MAINTENANCE_FOLDER+"scoring.xls");
			copyFile(inScoring, outScoring);
			inScoring.close();
			outScoring.close();
			
			InputStream inTrait=assets.open("traits.xls");
			OutputStream outTrait=new FileOutputStream(FieldLabPath.MAINTENANCE_FOLDER+"traits.xls");
			copyFile(inTrait, outTrait);
			inTrait.close();
			outTrait.close();
			
			InputStream inFieldLabGuide=assets.open("FieldLab Guide.ppt");
			OutputStream outFieldLabGuide=new FileOutputStream(FieldLabPath.MAINTENANCE_FOLDER+"FieldLab Guide.ppt");
			copyFile(inFieldLabGuide, outFieldLabGuide);
			inFieldLabGuide.close();
			outFieldLabGuide.close();
			
			InputStream inSample1=assets.open("sample.xls");
			OutputStream outSample1=new FileOutputStream(FieldLabPath.WORKBOOK_FOLDER+"sample.xls");
			copyFile(inSample1, outSample1);
			inSample1.close();
			outSample1.close();
			
			InputStream inSample2=assets.open("ARYT2010WS.xls");
			OutputStream outSample2=new FileOutputStream(FieldLabPath.WORKBOOK_FOLDER+"ARYT2010WS.xls");
			copyFile(inSample2, outSample2);
			inSample2.close();
			outSample2.close();
			
			InputStream inSample3=assets.open("CHAR2004WS.xls");
			OutputStream outSample3=new FileOutputStream(FieldLabPath.WORKBOOK_FOLDER+"CHAR2004WS.xls");
			copyFile(inSample3, outSample3);
			inSample3.close();
			outSample3.close();
			
			
			InputStream inStudy=assets.open("icisworkbook");
			OutputStream outStudy=new FileOutputStream(FieldLabPath.STUDY_FOLDER+"icisworkbook");
			copyFile(inStudy, outStudy);
			inStudy.close();
			outStudy.close();
			

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	public boolean createNewDatabaseFile(AssetManager assets,String newFileName){

		File f = new File(FieldLabPath.STUDY_FOLDER+newFileName);

		if(f.exists()){
			return false;
		}else{

			String[] files = null;
			try {
				files = assets.list("");
			} catch (IOException e) {
				Log.e("tag", e.getMessage());
			}
			for(String filename : files) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assets.open(filename);
					if(filename.contains("icisworkbook")){
						out = new FileOutputStream(FieldLabPath.STUDY_FOLDER + filename); 
						copyFile(in, out);
						in.close();
						in = null;
						out.flush();
						out.close();
						out = null;
						break;
					}

				} catch(Exception e) {
					Log.e("tag", e.getMessage());
				}       
			}

			//rename the file

			File from = new File(FieldLabPath.STUDY_FOLDER,"icisworkbook");
			File to = new File(FieldLabPath.STUDY_FOLDER,newFileName);
			from.renameTo(to);
			return true;
		}

	}

	
}
