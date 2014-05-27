package org.irri.fieldlab.model;

public class DescriptionModel {
	private long id;
	private String traitcode;
	private String description;
	private String property;
	private String scale;
	private String method;
	private String datatype;
	private String value;
	private String label;
	private double minimumvalue;
	private double maximumvalue;
	private String category;
	private String inputtype;
	private String visible;
	private boolean selected;
	private String scoring;
	private String lock;
	
	public DescriptionModel(){
		
	}

	public DescriptionModel(long id,
			String traitcode,
			String description, 
			String property, 
			String scale,
			String method,
			String datatype,
			String value) {

		this.id=id;
		this.traitcode=traitcode;
		this.description=description;
		this.property=property;
		this.scale=scale;
		this.method=method;
		this.datatype=datatype;
		this.value=value;
		this.label=label;
	}
	
	public DescriptionModel(long id,
			String traitcode,
			String description, 
			String property, 
			String scale,
			String method,
			String datatype,
			String value,
			String label,
			double minimumvalue,
			double maximumvalue,
			String category,
			String inputType,
			String visible
			) {

		this.id=id;
		this.traitcode=traitcode;
		this.description=description;
		this.property=property;
		this.scale=scale;
		this.method=method;
		this.datatype=datatype;
		this.value=value;
		this.label=label;
		this.minimumvalue=minimumvalue;
		this.maximumvalue=maximumvalue;
		this.category=category;
		this.inputtype=inputType;
		this.visible=visible;
		this.selected=false;
	}

	public DescriptionModel(long id,
			String traitcode,
			String description, 
			String property, 
			String scale,
			String method,
			String datatype,
			String value,
			String label,
			double minimumvalue,
			double maximumvalue,
			String category,
			String inputType,
			String visible,
			String scoring,
			String lock) {

		this.id=id;
		this.traitcode=traitcode;
		this.description=description;
		this.property=property;
		this.scale=scale;
		this.method=method;
		this.datatype=datatype;
		this.value=value;
		this.label=label;
		this.minimumvalue=minimumvalue;
		this.maximumvalue=maximumvalue;
		this.category=category;
		this.inputtype=inputType;
		this.visible=visible;
		this.scoring=scoring;
		this.lock=lock;
		this.selected=false;
	}


	public long getId() {
		return id;
	}


	public String getTraitcode() {
		return traitcode;
	}


	public String getDescription() {
		return description;
	}


	public String getProperty() {
		return property;
	}


	public String getScale() {
		return scale;
	}


	public String getMethod() {
		return method;
	}


	public String getDatatype() {
		return datatype;
	}


	public double getMinimumvalue() {
		return minimumvalue;
	}


	public double getMaximumvalue() {
		return maximumvalue;
	}


	public String getCategory() {
		return category;
	}


	public String getInputtype() {
		return inputtype;
	}


	public String getVisible() {
		return visible;
	}


	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		this.selected=selected;
		
	}


	public String getScoring() {
		return scoring;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public void setMinimumvalue(double minimumvalue) {
		this.minimumvalue = minimumvalue;
	}


	public void setMaximumvalue(double maximumvalue) {
		this.maximumvalue = maximumvalue;
	}


	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}

	public String getLock() {
		return lock;
	}

	


}
