package com.vanillacoder.delivery.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TimeslotDataItem{

	@SerializedName("status")
	private String status;

	@SerializedName("days")
	private List<String> days;

	@SerializedName("tslot")
	private List<String> tslot;

	public void setDays(List<String> days){
		this.days = days;
	}

	public List<String> getDays(){
		return days;
	}

	public void setTslot(List<String> tslot){
		this.tslot = tslot;
	}

	public List<String> getTslot(){
		return tslot;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}