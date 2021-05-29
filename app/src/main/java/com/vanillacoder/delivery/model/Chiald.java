package com.vanillacoder.delivery.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Chiald{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Childcatdata")
	private List<ChildcatItem> childcat;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public List<ChildcatItem> getChildcat(){
		return childcat;
	}

	public String getResult(){
		return result;
	}
}