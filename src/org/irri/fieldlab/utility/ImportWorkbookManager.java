package org.irri.fieldlab.utility;

import java.io.SyncFailedException;

import jxl.Sheet;
import jxl.Workbook;

import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.database.manager.ObservationManager;
import org.irri.fieldlab.model.TableICIS;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;

public class ImportWorkbookManager {

	private MaintenanceManager maintenanceManager;
	private String DEFAULT_FILE_SCORING= FieldLabPath.MAINTENANCE_FOLDER+"scoring.xls";
	private String DEFAULT_FILE_REMARKS=FieldLabPath.MAINTENANCE_FOLDER+"remarks.xls";
	private String DEFAULT_FILE_TRAIT=FieldLabPath.MAINTENANCE_FOLDER+"traits.xls";
	private boolean isValidTrait;

	public  ImportWorkbookManager(){

	}


	public boolean importICISWorkbook(Workbook workbook,SQLiteDatabase database) throws SyncFailedException{

		try{
			Sheet sheetDescription = workbook.getSheet(0);
			Sheet sheetObservation = workbook.getSheet(1);
			DescriptionManager descriptionManager = new DescriptionManager(database);

			String traitcode="";
			String description="";
			String property="";
			String scale="";
			String method="";
			String datatype="";
			String value="";
			String label="";
			int rowCondition=7;

			//			String project = sheetDescription.getCell(1, 0).getContents();
			String studyName = sheetDescription.getCell(1, 0).getContents();
			String title = sheetDescription.getCell(1, 1).getContents();
			String pmkey = sheetDescription.getCell(1, 2).getContents();
			String objective = sheetDescription.getCell(1,3).getContents();
			String startDate = sheetDescription.getCell(1,4).getContents();
			String endDate =sheetDescription.getCell(1,5).getContents();
			String studytype =sheetDescription.getCell(1,6).getContents();


			ContentValues contentStudyName = new ContentValues();
			contentStudyName.put(TableICIS.DESCRIPTION_TRAITCODE, "STUDY_NAME");
			contentStudyName.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
			contentStudyName.put(TableICIS.DESCRIPTION_PROPERTY, "");
			contentStudyName.put(TableICIS.DESCRIPTION_SCALE, "");
			contentStudyName.put(TableICIS.DESCRIPTION_METHOD, "");
			contentStudyName.put(TableICIS.DESCRIPTION_DATATYPE, "");
			contentStudyName.put(TableICIS.DESCRIPTION_VALUE, studyName);
			contentStudyName.put(TableICIS.DESCRIPTION_LABEL, "");
			contentStudyName.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
			contentStudyName.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
			contentStudyName.put(TableICIS.DESCRIPTION_CATEGORY, "description");
			contentStudyName.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
			contentStudyName.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
			contentStudyName.put(TableICIS.DESCRIPTION_LOCK, "Y");


			descriptionManager.insertDescription(contentStudyName);

			ContentValues contentStudyTitle = new ContentValues();
			contentStudyTitle.put(TableICIS.DESCRIPTION_TRAITCODE, "TITLE");
			contentStudyTitle.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
			contentStudyTitle.put(TableICIS.DESCRIPTION_PROPERTY, "");
			contentStudyTitle.put(TableICIS.DESCRIPTION_SCALE, "");
			contentStudyTitle.put(TableICIS.DESCRIPTION_METHOD, "");
			contentStudyTitle.put(TableICIS.DESCRIPTION_DATATYPE, "");
			contentStudyTitle.put(TableICIS.DESCRIPTION_VALUE, title);
			contentStudyTitle.put(TableICIS.DESCRIPTION_LABEL, "");
			contentStudyTitle.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
			contentStudyTitle.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
			contentStudyTitle.put(TableICIS.DESCRIPTION_CATEGORY, "description");
			contentStudyTitle.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
			contentStudyTitle.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");	
			contentStudyTitle.put(TableICIS.DESCRIPTION_LOCK, "Y");

			descriptionManager.insertDescription(contentStudyTitle);

			ContentValues contentStudyPmKey = new ContentValues();
			contentStudyPmKey.put(TableICIS.DESCRIPTION_TRAITCODE, "PM_KEY");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_PROPERTY, "");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_SCALE, "");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_METHOD, "");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_DATATYPE, "");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_VALUE, pmkey);
			contentStudyPmKey.put(TableICIS.DESCRIPTION_LABEL, "");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
			contentStudyPmKey.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
			contentStudyPmKey.put(TableICIS.DESCRIPTION_CATEGORY, "description");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
			contentStudyPmKey.put(TableICIS.DESCRIPTION_LOCK, "Y");

			descriptionManager.insertDescription(contentStudyPmKey);

			ContentValues contentStudyObjective = new ContentValues();
			contentStudyObjective.put(TableICIS.DESCRIPTION_TRAITCODE, "OBJECTIVE");
			contentStudyObjective.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
			contentStudyObjective.put(TableICIS.DESCRIPTION_PROPERTY, "");
			contentStudyObjective.put(TableICIS.DESCRIPTION_SCALE, "");
			contentStudyObjective.put(TableICIS.DESCRIPTION_METHOD, "");
			contentStudyObjective.put(TableICIS.DESCRIPTION_DATATYPE, "");
			contentStudyObjective.put(TableICIS.DESCRIPTION_VALUE, objective);
			contentStudyObjective.put(TableICIS.DESCRIPTION_LABEL, "");
			contentStudyObjective.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
			contentStudyObjective.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
			contentStudyObjective.put(TableICIS.DESCRIPTION_CATEGORY, "description");
			contentStudyObjective.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
			contentStudyObjective.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
			contentStudyObjective.put(TableICIS.DESCRIPTION_LOCK, "Y");

			descriptionManager.insertDescription(contentStudyObjective);

			ContentValues contentStudyStartDate = new ContentValues();
			contentStudyStartDate.put(TableICIS.DESCRIPTION_TRAITCODE, "START_DATE");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_PROPERTY, "");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_SCALE, "");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_METHOD, "");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_DATATYPE, "");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_VALUE, startDate);
			contentStudyStartDate.put(TableICIS.DESCRIPTION_LABEL, "");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
			contentStudyStartDate.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
			contentStudyStartDate.put(TableICIS.DESCRIPTION_CATEGORY, "description");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
			contentStudyStartDate.put(TableICIS.DESCRIPTION_LOCK, "Y");

			descriptionManager.insertDescription(contentStudyStartDate);

			ContentValues contentStudyEndDate = new ContentValues();
			contentStudyEndDate.put(TableICIS.DESCRIPTION_TRAITCODE, "END_DATE");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_PROPERTY, "");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_SCALE, "");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_METHOD, "");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_DATATYPE, "");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_VALUE, endDate);
			contentStudyEndDate.put(TableICIS.DESCRIPTION_LABEL, "");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
			contentStudyEndDate.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
			contentStudyEndDate.put(TableICIS.DESCRIPTION_CATEGORY, "description");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
			contentStudyEndDate.put(TableICIS.DESCRIPTION_LOCK, "Y");
			
			descriptionManager.insertDescription(contentStudyEndDate);
			
			if(!studytype.equals("")){

				ContentValues contentStudyStudyType = new ContentValues();
				contentStudyStudyType.put(TableICIS.DESCRIPTION_TRAITCODE, "STUDY_TYPE");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_DESCRIPTION, "");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_PROPERTY, "");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_SCALE, "");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_METHOD, "");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_DATATYPE, "");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_VALUE, studytype);
				contentStudyStudyType.put(TableICIS.DESCRIPTION_LABEL, "");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
				contentStudyStudyType.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
				contentStudyStudyType.put(TableICIS.DESCRIPTION_CATEGORY, "description");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
				contentStudyStudyType.put(TableICIS.DESCRIPTION_LOCK, "Y");
				
				descriptionManager.insertDescription(contentStudyStudyType);
				rowCondition=6;
			}

			boolean conditionRow=false;
			boolean factorRow=false;
			boolean constantRow=false;
			boolean variateRow=false;
			int factorColumnVisible=0;
			int traitColumnVisible=0;

			for(int i=rowCondition ;i < sheetDescription.getRows();i++){

				String indicator=sheetDescription.getCell(0, i).getContents();

				if(indicator.equals("CONDITION")){
					conditionRow=true;
					i++;
				}else if(indicator.equals("FACTOR")){
					i++;
					conditionRow=false;
					factorRow=true;
				}else if(indicator.equals("CONSTANT")){
					i++;
					factorRow=false;
					constantRow=true;
				}else if(indicator.equals("VARIATE")){
					i++;
					constantRow=false;
					variateRow=true;

				}

				if(conditionRow){

					if(!sheetDescription.getCell(0, i).getContents().equals("")){

						traitcode = sheetDescription.getCell(0, i).getContents();
						description = sheetDescription.getCell(1, i).getContents();
						property = sheetDescription.getCell(2, i).getContents();
						scale = sheetDescription.getCell(3, i).getContents();
						method = sheetDescription.getCell(4, i).getContents();
						datatype =sheetDescription.getCell(5,i).getContents();
						value =sheetDescription.getCell(6,i).getContents();
						label=sheetDescription.getCell(7,i).getContents();

						ContentValues contentCondition = new ContentValues();
						contentCondition.put(TableICIS.DESCRIPTION_TRAITCODE, traitcode);
						contentCondition.put(TableICIS.DESCRIPTION_DESCRIPTION, description);
						contentCondition.put(TableICIS.DESCRIPTION_PROPERTY, property);
						contentCondition.put(TableICIS.DESCRIPTION_SCALE, scale);
						contentCondition.put(TableICIS.DESCRIPTION_METHOD, method);
						contentCondition.put(TableICIS.DESCRIPTION_DATATYPE, datatype);
						contentCondition.put(TableICIS.DESCRIPTION_VALUE, value);
						contentCondition.put(TableICIS.DESCRIPTION_LABEL, "");
						contentCondition.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
						contentCondition.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
						contentCondition.put(TableICIS.DESCRIPTION_CATEGORY, "condition");
						contentCondition.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
						contentCondition.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R0");
						contentCondition.put(TableICIS.DESCRIPTION_LOCK, "Y");

						descriptionManager.insertDescription(contentCondition);
					}
				}

				if(factorRow){

					if(!sheetDescription.getCell(0, i).getContents().equals("")){
						traitcode = sheetDescription.getCell(0, i).getContents();
						description = sheetDescription.getCell(1, i).getContents();
						property = sheetDescription.getCell(2, i).getContents();
						scale = sheetDescription.getCell(3, i).getContents();
						method = sheetDescription.getCell(4, i).getContents();
						datatype =sheetDescription.getCell(5,i).getContents();
						value =sheetDescription.getCell(6,i).getContents();
						label=sheetDescription.getCell(7,i).getContents();

						ContentValues contentFactor = new ContentValues();
						contentFactor.put(TableICIS.DESCRIPTION_TRAITCODE, traitcode);
						contentFactor.put(TableICIS.DESCRIPTION_DESCRIPTION, description);
						contentFactor.put(TableICIS.DESCRIPTION_PROPERTY, property);
						contentFactor.put(TableICIS.DESCRIPTION_SCALE, scale);
						contentFactor.put(TableICIS.DESCRIPTION_METHOD, method);
						contentFactor.put(TableICIS.DESCRIPTION_DATATYPE, datatype);
						contentFactor.put(TableICIS.DESCRIPTION_VALUE, value);
						contentFactor.put(TableICIS.DESCRIPTION_LABEL, "");
						contentFactor.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
						contentFactor.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
						contentFactor.put(TableICIS.DESCRIPTION_CATEGORY, "factor");
						contentFactor.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
						contentFactor.put(TableICIS.DESCRIPTION_LOCK, "Y");


						if(traitcode.contains("REP") || traitcode.contains("ROW")||
								traitcode.contains("COL")||traitcode.contains("PLOT")||
								traitcode.contains("ENTRY")||traitcode.contains("BLOCK")||traitcode.contains("BLK")
								||traitcode.contains("ENTNO")){
							contentFactor.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R2");
							contentFactor.put(TableICIS.DESCRIPTION_VISIBLE, "Y");
						}else{
							contentFactor.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R1");
							contentFactor.put(TableICIS.DESCRIPTION_VISIBLE, "Y");
						}

						factorColumnVisible++;
						descriptionManager.insertDescription(contentFactor);
					}
				}

				if(constantRow){

					if(!sheetDescription.getCell(0, i).getContents().equals("")){
						traitcode = sheetDescription.getCell(0, i).getContents();
						description = sheetDescription.getCell(1, i).getContents();
						property = sheetDescription.getCell(2, i).getContents();
						scale = sheetDescription.getCell(3, i).getContents();
						method = sheetDescription.getCell(4, i).getContents();
						datatype =sheetDescription.getCell(5,i).getContents();
						value =sheetDescription.getCell(6,i).getContents();
						label =sheetDescription.getCell(7,i).getContents();

						ContentValues contentConstant = new ContentValues();
						contentConstant.put(TableICIS.DESCRIPTION_TRAITCODE, traitcode);
						contentConstant.put(TableICIS.DESCRIPTION_DESCRIPTION, description);
						contentConstant.put(TableICIS.DESCRIPTION_PROPERTY, property);
						contentConstant.put(TableICIS.DESCRIPTION_SCALE, scale);
						contentConstant.put(TableICIS.DESCRIPTION_METHOD, method);
						contentConstant.put(TableICIS.DESCRIPTION_DATATYPE, datatype);
						contentConstant.put(TableICIS.DESCRIPTION_VALUE, value);
						contentConstant.put(TableICIS.DESCRIPTION_LABEL, "");
						contentConstant.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
						contentConstant.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
						contentConstant.put(TableICIS.DESCRIPTION_CATEGORY, "constant");
						contentConstant.put(TableICIS.DESCRIPTION_INPUTTYPE, "label");
						contentConstant.put(TableICIS.DESCRIPTION_LOCK, "Y");
						
						descriptionManager.insertDescription(contentConstant);

					}
				}

				if(variateRow){

					if(!sheetDescription.getCell(0, i).getContents().equals("")){
						traitcode = sheetDescription.getCell(0, i).getContents();
						description = sheetDescription.getCell(1, i).getContents();
						property = sheetDescription.getCell(2, i).getContents();
						scale = sheetDescription.getCell(3, i).getContents();
						method = sheetDescription.getCell(4, i).getContents();
						datatype =sheetDescription.getCell(5,i).getContents();
						value =sheetDescription.getCell(6,i).getContents();
						label =sheetDescription.getCell(7,i).getContents();

						ContentValues contentVariate = new ContentValues();
						//						traitcode=traitcode.replace(" ", "_");

						//						if(traitcode.substring(0,1).matches("[0-9]")){
						//							return false;
						//						}

						contentVariate.put(TableICIS.DESCRIPTION_TRAITCODE, traitcode);
						contentVariate.put(TableICIS.DESCRIPTION_DESCRIPTION, description);
						contentVariate.put(TableICIS.DESCRIPTION_PROPERTY, property);
						contentVariate.put(TableICIS.DESCRIPTION_SCALE, scale);
						contentVariate.put(TableICIS.DESCRIPTION_METHOD, method);
						contentVariate.put(TableICIS.DESCRIPTION_DATATYPE, datatype);
						contentVariate.put(TableICIS.DESCRIPTION_VALUE, value);
						contentVariate.put(TableICIS.DESCRIPTION_LABEL, label);
						contentVariate.put(TableICIS.DESCRIPTION_MINIMUMVALUE, 0);
						contentVariate.put(TableICIS.DESCRIPTION_MAXIMUMVALUE, 0);
						contentVariate.put(TableICIS.DESCRIPTION_CATEGORY, "variate");
						contentVariate.put(TableICIS.DESCRIPTION_INPUTTYPE, "textfield");
						contentVariate.put(TableICIS.DESCRIPTION_DISPLAYREGION, "R2");
						contentVariate.put(TableICIS.DESCRIPTION_LOCK, "Y");

						if(traitColumnVisible < 2 ){
							contentVariate.put(TableICIS.DESCRIPTION_VISIBLE, "Y");
						}else{
							contentVariate.put(TableICIS.DESCRIPTION_VISIBLE, "N");
						}
						traitColumnVisible++;
						descriptionManager.insertDescription(contentVariate);
					}
				}
			}

			// Create Observation Data Field
			ObservationManager observationManager= new ObservationManager(database);
			observationManager.createObservationTable();
			String column="";
			int totalColumn=0;

			// create field on observation table
			for(int i=0;i < sheetObservation.getColumns() ;i++){
				String cellValue=sheetObservation.getCell(i, 0).getContents();

				if(!cellValue.equals("")){
					observationManager.alterTableObservation("`"+cellValue+"`");
					//					observationManager.insertObservationColumnHeader(cellValue);
					column+="`"+cellValue+"`,";
					totalColumn++;
				}else{
					break;
				}
			}

			// add observation factor data on the observation table
			for(int row=1;row < sheetObservation.getRows();row++)
			{
				String data;
				String firstData=sheetObservation.getCell(0, row).getContents();
				if(firstData.equals("")){
					break;
				}else{
					data="\""+sheetObservation.getCell(0, row).getContents()+"\",";
				}
				for(int col=1;col < totalColumn;col++){
					data+="\""+sheetObservation.getCell(col, row).getContents()+"\",";
				}
				data=data.substring(0,data.length()-1);
				String field=column.substring(0,column.length()-1);
				observationManager.insertObservationData(field,data);
			}

			// import maintenance data

			MaintenanceManager maintenanceManager= new MaintenanceManager(database);
			maintenanceManager.loadMaintenanceData(DEFAULT_FILE_SCORING, 0);

			// create study image folder

			return true;

		}catch(SQLiteFullException e){
			return false;
		}catch (Exception e) {
			return false;
		}


	}
	/*
	private String createObservationTable(Sheet sheetObservation){

		ArrayList<String> columnHeader = new ArrayList<String>();

		for(int i=0;i < sheetObservation.getColumns() ;i++){
			String cellValue=sheetObservation.getCell(i, 0).getContents();
			if(!cellValue.equals("")){
				columnHeader.add(cellValue);
			}else{
				break;
			}
		}
		int i;

		 String querryString = columnHeader.get(0)+" TEXT,";
         for(i=1;i<columnHeader.size()-1;i++)
         {               
             querryString += columnHeader.get(i);
             querryString +=" TEXT";
             querryString +=",";
         }
         querryString+= columnHeader.get(i) +" TEXT";
         String tableName="observation";
		 querryString = "CREATE TABLE IF NOT EXISTS " + tableName + "(_id INTEGER PRIMARY KEY,"+querryString+");";
		 return querryString;
	}
	 */




}
