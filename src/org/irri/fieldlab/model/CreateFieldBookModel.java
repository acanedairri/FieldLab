package org.irri.fieldlab.model;

public class CreateFieldBookModel {
	//study description
	private String sdProgram;
	private String sdProject;
	private String sdStudyName;
	private String sdStudyType;
	private String sdStudyId;
	private String sdStudyDescription;
	private String sdStudyCoordinator;
	private String sdStudyCropEstablishment;
	
	//agronomic
	
	private String agroTransplantingDate;
	private String agroSowingDate;
	private String agroFertilizationType;
	private String agroFertilizationTime;
	private String agroFertilizationDosage;
	private String agroCroppingDensity;
	private String agroHarvestArea;
	
	//layout
	private String layoutDesignType;
	private String layoutNumberOfGenotype;
	private String layoutNumberOfCompleteReflicate;
	private String layoutNumberOfBlockPerRep;
	private String layoutNumberOfDesignRow;
	private String layoutNumberOfRowPerRep;
	private String layoutNumberOfDesignColumn;
	private String layoutNumberOfColumnPerRep;
	private String layoutNumberOfPlotSize;
	private String layoutNumberOfChecks;
	
	// Site Description
	
	private String siteId;
	private String siteName;
	private String siteType;
	private String siteManagementContact;
	private String siteLatitude;
	private String siteAltitude;
	
	
	public void setSiteAltitude(String siteAltitude) {
		this.siteAltitude = siteAltitude;
	}





	private String siteElevation;
	private String siteAdministrativeZone;
	private String siteNearestMeteorologyStation;
	
	//soil Characteristic
	
	private String soilType;
	private String soilTexture;
	private String soidPh;
	private String soilSalinity;
	private String soilOrganic;
	
	//climate
	private String climateTemparature;
	private String climateDailyRainfall;
	private String climateHumidity;
	private String climateWindSpeed;
	
	
	
	
	
	public CreateFieldBookModel(){
		
	}





	public String getSdProgram() {
		return sdProgram;
	}





	public void setSdProgram(String sdProgram) {
		this.sdProgram = sdProgram;
	}





	public String getSdProject() {
		return sdProject;
	}





	public void setSdProject(String sdProject) {
		this.sdProject = sdProject;
	}





	public String getSdStudyName() {
		return sdStudyName;
	}





	public void setSdStudyName(String sdStudyName) {
		this.sdStudyName = sdStudyName;
	}





	public String getSdStudyType() {
		return sdStudyType;
	}





	public void setSdStudyType(String sdStudyType) {
		this.sdStudyType = sdStudyType;
	}





	public String getSdStudyId() {
		return sdStudyId;
	}





	public void setSdStudyId(String sdStudyId) {
		this.sdStudyId = sdStudyId;
	}





	public String getSdStudyDescription() {
		return sdStudyDescription;
	}





	public void setSdStudyDescription(String sdStudyDescription) {
		this.sdStudyDescription = sdStudyDescription;
	}





	public String getSdStudyCoordinator() {
		return sdStudyCoordinator;
	}





	public void setSdStudyCoordinator(String sdStudyCoordinator) {
		this.sdStudyCoordinator = sdStudyCoordinator;
	}





	public String getSdStudyCropEstablishment() {
		return sdStudyCropEstablishment;
	}





	public void setSdStudyCropEstablishment(String sdStudyCropEstablishment) {
		this.sdStudyCropEstablishment = sdStudyCropEstablishment;
	}





	public String getAgroTransplantingDate() {
		return agroTransplantingDate;
	}





	public void setAgroTransplantingDate(String agroTransplantingDate) {
		this.agroTransplantingDate = agroTransplantingDate;
	}





	public String getAgroSowingDate() {
		return agroSowingDate;
	}





	public void setAgroSowingDate(String agroSowingDate) {
		this.agroSowingDate = agroSowingDate;
	}





	public String getAgroFertilizationType() {
		return agroFertilizationType;
	}





	public void setAgroFertilizationType(String agroFertilizationType) {
		this.agroFertilizationType = agroFertilizationType;
	}





	public String getAgroFertilizationTime() {
		return agroFertilizationTime;
	}





	public void setAgroFertilizationTime(String agroFertilizationTime) {
		this.agroFertilizationTime = agroFertilizationTime;
	}





	public String getAgroFertilizationDosage() {
		return agroFertilizationDosage;
	}





	public void setAgroFertilizationDosage(String agroFertilizationDosage) {
		this.agroFertilizationDosage = agroFertilizationDosage;
	}





	public String getAgroCroppingDensity() {
		return agroCroppingDensity;
	}





	public void setAgroCroppingDensity(String agroCroppingDensity) {
		this.agroCroppingDensity = agroCroppingDensity;
	}





	public String getAgroHarvestArea() {
		return agroHarvestArea;
	}








	public void setAgroHarvestArea(String agroHarvestArea) {
		this.agroHarvestArea = agroHarvestArea;
	}





	public String getLayoutDesignType() {
		return layoutDesignType;
	}





	public void setLayoutDesignType(String layoutDesignType) {
		this.layoutDesignType = layoutDesignType;
	}





	public String getLayoutNumberOfGenotype() {
		return layoutNumberOfGenotype;
	}





	public void setLayoutNumberOfGenotype(String layoutNumberOfGenotype) {
		this.layoutNumberOfGenotype = layoutNumberOfGenotype;
	}





	public String getLayoutNumberOfCompleteReflicate() {
		return layoutNumberOfCompleteReflicate;
	}





	public void setLayoutNumberOfCompleteReflicate(
			String layoutNumberOfCompleteReflicate) {
		this.layoutNumberOfCompleteReflicate = layoutNumberOfCompleteReflicate;
	}





	public String getLayoutNumberOfBlockPerRep() {
		return layoutNumberOfBlockPerRep;
	}





	public void setLayoutNumberOfBlockPerRep(String layoutNumberOfBlockPerRep) {
		this.layoutNumberOfBlockPerRep = layoutNumberOfBlockPerRep;
	}





	public String getLayoutNumberOfDesignRow() {
		return layoutNumberOfDesignRow;
	}





	public void setLayoutNumberOfDesignRow(String layoutNumberOfDesignRow) {
		this.layoutNumberOfDesignRow = layoutNumberOfDesignRow;
	}





	public String getLayoutNumberOfRowPerRep() {
		return layoutNumberOfRowPerRep;
	}





	public void setLayoutNumberOfRowPerRep(String layoutNumberOfRowPerRep) {
		this.layoutNumberOfRowPerRep = layoutNumberOfRowPerRep;
	}





	public String getLayoutNumberOfDesignColumn() {
		return layoutNumberOfDesignColumn;
	}





	public void setLayoutNumberOfDesignColumn(String layoutNumberOfDesignColumn) {
		this.layoutNumberOfDesignColumn = layoutNumberOfDesignColumn;
	}





	public String getLayoutNumberOfColumnPerRep() {
		return layoutNumberOfColumnPerRep;
	}





	public void setLayoutNumberOfColumnPerRep(String layoutNumberOfColumnPerRep) {
		this.layoutNumberOfColumnPerRep = layoutNumberOfColumnPerRep;
	}





	public String getLayoutNumberOfPlotSize() {
		return layoutNumberOfPlotSize;
	}





	public void setLayoutNumberOfPlotSize(String layoutNumberOfPlotSize) {
		this.layoutNumberOfPlotSize = layoutNumberOfPlotSize;
	}





	public String getLayoutNumberOfChecks() {
		return layoutNumberOfChecks;
	}





	public void setLayoutNumberOfChecks(String layoutNumberOfChecks) {
		this.layoutNumberOfChecks = layoutNumberOfChecks;
	}





	public String getSiteId() {
		return siteId;
	}





	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}





	public String getSiteName() {
		return siteName;
	}





	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}





	public String getSiteType() {
		return siteType;
	}





	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}





	public String getSiteManagementContact() {
		return siteManagementContact;
	}





	public void setSiteManagementContact(String siteManagementContact) {
		this.siteManagementContact = siteManagementContact;
	}





	public String getSiteLatitude() {
		return siteLatitude;
	}





	public void setSiteLatitude(String siteLatitude) {
		this.siteLatitude = siteLatitude;
	}





	public String getSiteAltitude() {
		return siteAltitude;
	}





	public void soilSalinity(String siteAltitude) {
		this.siteAltitude = siteAltitude;
	}





	public String getSiteElevation() {
		return siteElevation;
	}





	public void setSiteElevation(String siteElevation) {
		this.siteElevation = siteElevation;
	}





	public String getSiteAdministrativeZone() {
		return siteAdministrativeZone;
	}





	public void setSiteAdministrativeZone(String siteAdministrativeZone) {
		this.siteAdministrativeZone = siteAdministrativeZone;
	}





	public String getSiteNearestMeteorologyStation() {
		return siteNearestMeteorologyStation;
	}





	public void setSiteNearestMeteorologyStation(
			String siteNearestMeteorologyStation) {
		this.siteNearestMeteorologyStation = siteNearestMeteorologyStation;
	}





	public String getSoilType() {
		return soilType;
	}





	public void setSoilType(String soilType) {
		this.soilType = soilType;
	}





	public String getSoilTexture() {
		return soilTexture;
	}





	public void setSoilTexture(String soilTexture) {
		this.soilTexture = soilTexture;
	}





	public String getSoidPh() {
		return soidPh;
	}





	public void setSoidPh(String soidPh) {
		this.soidPh = soidPh;
	}





	public String getSoilSalinity() {
		return soilSalinity;
	}





	public void setSoilSalinity(String soilSalinity) {
		this.soilSalinity = soilSalinity;
	}





	public String getSoilOrganic() {
		return soilOrganic;
	}





	public void setSoilOrganic(String soilOrganic) {
		this.soilOrganic = soilOrganic;
	}





	public String getClimateTemparature() {
		return climateTemparature;
	}





	public void setClimateTemparature(String climateTemparature) {
		this.climateTemparature = climateTemparature;
	}





	public String getClimateDailyRainfall() {
		return climateDailyRainfall;
	}





	public void setClimateDailyRainfall(String climateDailyRainfall) {
		this.climateDailyRainfall = climateDailyRainfall;
	}





	public String getClimateHumidity() {
		return climateHumidity;
	}





	public void setClimateHumidity(String climateHumidity) {
		this.climateHumidity = climateHumidity;
	}





	public String getClimateWindSpeed() {
		return climateWindSpeed;
	}





	public void setClimateWindSpeed(String climateWindSpeed) {
		this.climateWindSpeed = climateWindSpeed;
	}






	

}
