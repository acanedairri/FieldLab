package org.irri.fieldlab.utility;

import java.io.File;
import java.util.ArrayList;

public class ImageManager {
	
	public ImageManager(){
		
	}
	
	public int getImageCount(String path, String plotName){
		File folder = new File(path);
		int imageCount=0;

		for(File img : folder.listFiles()){
			if(img.getName().endsWith(".png") || img.getName().endsWith(".jpg")){
				if(img.getName().contains("_") && img.getName().split("_")[1].equals(plotName) || img.getName().split("_")[2].equals(plotName) )
				    imageCount++;
			}
		}

		return imageCount;
	}
	
	
	public int getAudioCount(String path, String plotName){
		File folder = new File(path);
		int audioCount=0;

		for(File img : folder.listFiles()){
			if(img.getName().endsWith(".mp4") || img.getName().endsWith(".mp3")){
				if(img.getName().contains("_") && img.getName().split("_")[1].equals(plotName) || img.getName().split("_")[2].equals(plotName) )
					audioCount++;
			}
		}

		return audioCount;
	}
	
}
