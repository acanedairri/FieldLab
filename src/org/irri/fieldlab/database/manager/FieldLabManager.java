package org.irri.fieldlab.database.manager;

import android.database.sqlite.SQLiteDatabase;

public class FieldLabManager {
	

	private ObservationManager observationManager;
	private RemarksManager remarksManager;
	private ScoringManager scoringManager;
	private GenericManager genericManager;
	private SettingsManager settingsManager;
	private TraitDictionaryManager traitDictionaryManager;
	private DescriptionManager descriptionManager;


	public FieldLabManager(SQLiteDatabase database ) {
		this.observationManager=new ObservationManager(database);
		this.remarksManager=new RemarksManager(database);
		this.scoringManager=new ScoringManager(database);
		this.descriptionManager=new DescriptionManager(database);
		this.settingsManager= new SettingsManager(database);
	}


	public ObservationManager getObservationManager() {
		return observationManager;
	}

	public RemarksManager getRemarksManager() {
		return remarksManager;
	}

	public ScoringManager getScoringManager() {
		return scoringManager;
	}

	public GenericManager getGenericManager() {
		return genericManager;
	}

	public SettingsManager getSettingsManager() {
		return settingsManager;
	}

	public TraitDictionaryManager getTraitDictionaryManager() {
		return traitDictionaryManager;
	}

	public DescriptionManager getDescriptionManager() {
		return descriptionManager;
	}


	public void setDescriptionManager(DescriptionManager descriptionManager) {
		this.descriptionManager = descriptionManager;
	}


}
