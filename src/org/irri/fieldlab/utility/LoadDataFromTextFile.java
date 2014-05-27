package org.irri.fieldlab.utility;


public class LoadDataFromTextFile {

	private String studyName;
	private boolean templatedLoaded;

	public LoadDataFromTextFile(){

	}
/*
	public void loadStudyTextFile(String fileName,SQLiteDatabase database){

		int lineCounter = 0;
		templatedLoaded=false;

		try {
			// open the file for reading
			File fileI = new File(fileName);
			InputStream instream = new FileInputStream(fileI);
			// if file the available for reading
			if (instream != null) {
				// prepare the file for reading
				InputStreamReader inputreader = new InputStreamReader(
						instream);
				BufferedReader buffreader = new BufferedReader(
						inputreader);

				String line;
				HashMap<Integer, String> lineRowHashMap = new HashMap<Integer,String>();

				int studyKey = 0;
				int variateKey = 0;
				int dataRefKey = 0;

				// read every line of the file into the line-variable, one line at the time
				while ((line = buffreader.readLine()) != null) {
					// do something with the settings from the file
					//								Log.i("FILECONTENT", line);
					lineRowHashMap.put(lineCounter, line);

					// Get the keys!
					if (line.contains("@1STUDY")) {
						studyKey = lineCounter;
					} else if (line.contains("@2VARIATE")) {
						variateKey = lineCounter;
					} else if (line.contains("@3DATA_REFERENCE")) {
						dataRefKey = lineCounter;
					}

					lineCounter++;
				}

				// Get the study details
				String studyLine;
				StudyManager studyManager=  new StudyManager(database);
				
				

				for (int key = studyKey+1; key < variateKey; key++) {

					studyLine = lineRowHashMap.get(key);
					studyName = extractStudyName(studyLine);
					ContentValues cvStudyRef=extractStudyContentValues(studyLine);

					if (studyManager.checkIfStudyExists(studyName) == 0)
						studyManager.insertStudy(cvStudyRef);
					else {
						templatedLoaded = true;
						break;
					}

				}
				// Get the variate details
				String variateLine;
				TraitManager studyVariateManager = new TraitManager(database);

				for (int key = variateKey+1; key < dataRefKey; key++) {

					if (!templatedLoaded){
						variateLine = lineRowHashMap.get(key);
						ContentValues cvVariateRef = extractVariateContentValues(variateLine, studyName);
//						studyVariateManager.insertStudyVariate(cvVariateRef);
					}else {
						break;
					}
				}
			}

			// close the file again
			instream.close();


		} catch (java.io.FileNotFoundException e) {
			// do something if the myfilename.txt does not exits
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private String extractStudyName(String studyLine) {
		String sname = "";
		StringTokenizer tokenizer = new StringTokenizer(studyLine, "\t");
		if (tokenizer.hasMoreTokens()) {
			sname = tokenizer.nextToken();
		}
		return sname;
	}

	private ContentValues extractStudyContentValues(String text) {

		ContentValues content = new ContentValues();
		Log.i("TEXT-TEST", text);
		String[] rowArray = text.split("\t", -1);

		String studyName = "";
		String title = "";
		String barcode = "";
		String startdate = "";
		String enddate = "";
		String studyreference = "";

		if (rowArray.length > 0) {
			studyName = rowArray[0];
			title = rowArray[1];
			startdate = rowArray[2];
			enddate = rowArray[3];
			barcode = rowArray[4];
			studyreference = rowArray[5];
		}

		content.put(TableICIS.STUDY_STUDYNAME, studyName);
		content.put(TableICIS.STUDY_TITLE, title);
//		content.put(Table.STUDY_BARCODE, barcode);
		content.put(TableICIS.STUDY_STARTDATE, startdate);
		content.put(TableICIS.STUDY_ENDDATE, enddate);
//		content.put(Table.STUDY_STUDYREFERENCE, studyreference);

		return content;
	}

	private ContentValues extractVariateContentValues(String text, String studyName) {

		ContentValues content = new ContentValues();
		Log.i("DEBUG", text);
		String[] rowArray = text.split("\t", -1);

		String traitcode = "";
		String description = "";
		String property = "";
		String scale = "";
		String method = "";
		String datatype = "";
		String visible = "";

		if (rowArray.length > 0) {
			traitcode = rowArray[0];
			description = rowArray[1];
			property = rowArray[2];
			scale = rowArray[3];
			method = rowArray[4];
			datatype = rowArray[5];
		}

//		content.put(TableICIS.VARIATE_STUDYNAME, studyName);
//		content.put(TableICIS.VARIATE_TRAITCODE,traitcode );
//		content.put(TableICIS.VARIATE_DESCRIPTION, description);
//		content.put(TableICIS.VARIATE_PROPERTY, property);
//		content.put(TableICIS.VARIATE_SCALE, scale);
//		content.put(TableICIS.VARIATE_METHOD, method);
//		content.put(TableICIS.VARIATE_DATATYPE, datatype);
//		content.put(TableICIS.VARIATE_VISIBLE, "N");
//		content.put(TableICIS.VARIATE_MINIMUMVALUE, 0);
//		content.put(TableICIS.VARIATE_MAXIMUMVALUE, 0);

		return content;
	}
*/
	public String getStudyName() {
		return studyName;
	}

	public boolean isTemplatedLoaded() {
		return templatedLoaded;
	}

}
