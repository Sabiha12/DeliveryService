package com.vanillacoder.delivery.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ServicelistdataItem{

	@SerializedName("childcat_id")
	private String childcatId;

	@SerializedName("Service_list")
	private List<ServiceListItem> serviceList;

	@SerializedName("title")
	private String title;

	public String getChildcatId(){
		return childcatId;
	}

	public List<ServiceListItem> getServiceList(){
		return serviceList;
	}

	public String getTitle(){
		return title;
	}
}