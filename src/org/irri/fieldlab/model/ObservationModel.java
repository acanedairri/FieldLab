package org.irri.fieldlab.model;

public class ObservationModel {
	
	long id;	
	private String sname;
	private String sbarcode;
	private String sdataref;
	private String vcode;
	private String vvalue;
	private String remarks;
	private String dategathered;
	private String datatype;
	
	


	/**
	 * @return the datatype
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public  ObservationModel(long id,String sname,String sdataref, String sbarcode, String vcode,String vvalue,String remarks,String dType){
		this.id = id;
		this.sname=sname;
		this.sdataref=sdataref;
		this.sbarcode=sbarcode;
		this.vcode=vcode;
		this.vvalue=vvalue;
		this.remarks=remarks;
		this.datatype=dType;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @return the sname
	 */
	public String getSname() {
		return sname;
	}
	/**
	 * @return the sbarcode
	 */
	public String getSbarcode() {
		return sbarcode;
	}
	/**
	 * @return the sdataref
	 */
	public String getSdataref() {
		return sdataref;
	}
	/**
	 * @return the vcode
	 */
	public String getVcode() {
		return vcode;
	}
	/**
	 * @return the vvalue
	 */
	public String getVvalue() {
		return vvalue;
	}
	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * @return the dategathered
	 */
	public String getDategathered() {
		return dategathered;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @param sname the sname to set
	 */
	public void setSname(String sname) {
		this.sname = sname;
	}
	/**
	 * @param sbarcode the sbarcode to set
	 */
	public void setSbarcode(String sbarcode) {
		this.sbarcode = sbarcode;
	}
	/**
	 * @param sdataref the sdataref to set
	 */
	public void setSdataref(String sdataref) {
		this.sdataref = sdataref;
	}
	/**
	 * @param vcode the vcode to set
	 */
	public void setVcode(String vcode) {
		this.vcode = vcode;
	}
	/**
	 * @param vvalue the vvalue to set
	 */
	public void setVvalue(String vvalue) {
		this.vvalue = vvalue;
	}
	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	/**
	 * @param dategathered the dategathered to set
	 */
	public void setDategathered(String dategathered) {
		this.dategathered = dategathered;
	}
	
	

}
