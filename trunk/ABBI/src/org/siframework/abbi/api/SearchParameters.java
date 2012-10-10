package org.siframework.abbi.api;

import java.util.Date;

public class SearchParameters {
	// Strings
	private String patientID = null;
	private String classes[] = null;
	private String mimeType[] = null;
	private String format[] = null;
	// Pagination
	private int index = 0;
	private int count = 0;
	// Times
	private Date serviceStartTimeFrom = null;
	private Date serviceStopTimeFrom = null;
	private Date serviceStartTimeTo = null;
	private Date serviceStopTimeTo = null;
	private Date creationTimeFrom = null; 
	private Date creationTimeTo = null;
	private String outputFormat = null;
	private boolean includeContent = false;
	
	
	public String getPatientID() {
		return patientID;
	}
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	public String[] getClasses() {
		return classes;
	}
	public void setClasses(String[] classes) {
		this.classes = classes;
	}
	public String[] getMimeType() {
		return mimeType;
	}
	public void setMimeType(String[] mimeType) {
		this.mimeType = mimeType;
	}
	public String[] getFormat() {
		return format;
	}
	public void setFormat(String[] format) {
		this.format = format;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Date getServiceStartTimeFrom() {
		return serviceStartTimeFrom;
	}
	public void setServiceStartTimeFrom(Date serviceStartTimeFrom) {
		this.serviceStartTimeFrom = serviceStartTimeFrom;
	}
	public Date getServiceStopTimeFrom() {
		return serviceStopTimeFrom;
	}
	public void setServiceStopTimeFrom(Date serviceStopTimeFrom) {
		this.serviceStopTimeFrom = serviceStopTimeFrom;
	}
	public Date getServiceStartTimeTo() {
		return serviceStartTimeTo;
	}
	public void setServiceStartTimeTo(Date serviceStartTimeTo) {
		this.serviceStartTimeTo = serviceStartTimeTo;
	}
	public Date getServiceStopTimeTo() {
		return serviceStopTimeTo;
	}
	public void setServiceStopTimeTo(Date serviceStopTimeTo) {
		this.serviceStopTimeTo = serviceStopTimeTo;
	}
	public Date getCreationTimeFrom() {
		return creationTimeFrom;
	}
	public void setCreationTimeFrom(Date creationTimeFrom) {
		this.creationTimeFrom = creationTimeFrom;
	}
	public Date getCreationTimeTo() {
		return creationTimeTo;
	}
	public void setCreationTimeTo(Date creationTimeTo) {
		this.creationTimeTo = creationTimeTo;
	}
	
	public boolean isContentIncluded() {
		return includeContent;
	}
	public void setContentIncluded(boolean includeContent) {
		this.includeContent = includeContent;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
}
