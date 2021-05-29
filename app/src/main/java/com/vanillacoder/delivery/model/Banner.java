package com.vanillacoder.delivery.model;

import com.google.gson.annotations.SerializedName;

public class Banner {

	@SerializedName("img")
	private String img;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	public void setImg(String img){
		this.img = img;
	}

	public String getImg(){
		return img;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}