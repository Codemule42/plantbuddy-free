package com.aftgg.plantbuddy.core;

public class LinkData {
	//private int	plantID;
	private String	url;
	private String  title;
	private int linkId;

	public LinkData( int linkId, String title, String  url) {
		//this.plantID = plantId;
        this.setUrl(url);
        this.setTitle(title);
        this.linkId = linkId;
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getLinkId()
	{
		return this.linkId;
	}
}