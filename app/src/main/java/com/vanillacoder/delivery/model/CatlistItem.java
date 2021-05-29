package com.vanillacoder.delivery.model;

import com.google.gson.annotations.SerializedName;

public class CatlistItem{

	@SerializedName("cat_img")
	private String catImg;

	@SerializedName("cat_name")
	private String catName;

	@SerializedName("Total_subcat")
	private int totalSubcat;

	@SerializedName("cat_subtitle")
	private String catSubtitle;

	@SerializedName("cat_id")
	private String catId;

	@SerializedName("cat_video")
	private String catVideo;

	public void setCatImg(String catImg){
		this.catImg = catImg;
	}

	public String getCatImg(){
		return catImg;
	}

	public void setCatName(String catName){
		this.catName = catName;
	}

	public String getCatName(){
		return catName;
	}

	public void setTotalSubcat(int totalSubcat){
		this.totalSubcat = totalSubcat;
	}

	public int getTotalSubcat(){
		return totalSubcat;
	}

	public void setCatSubtitle(String catSubtitle){
		this.catSubtitle = catSubtitle;
	}

	public String getCatSubtitle(){
		return catSubtitle;
	}

	public void setCatId(String catId){
		this.catId = catId;
	}

	public String getCatId(){
		return catId;
	}

	public void setCatVideo(String catVideo){
		this.catVideo = catVideo;
	}

	public String getCatVideo(){
		return catVideo;
	}
}