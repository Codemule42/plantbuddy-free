package com.aftgg.plantbuddy.core;

import com.aftgg.plantbuddy.core.CareTipData;

public class PlantData {
	private 	int				plant_id;
	private 	String  		plant_name;
	private    String			start_date;
	private 	String			source;
	private 	int 			imageResource = -1;
	private 	String  		icon_filename;
	private 	String  		description;
	@SuppressWarnings("unused")
	private    LinkData[]  		links;
	@SuppressWarnings("unused")
	private 	CareTipData[]	care_tips;
	@SuppressWarnings("unused")
	private    NotesData[]		notes;
	private		boolean			alarm = false;
	

	public PlantData(int plant_id, String plant_name, String start_date, String source, int imageResource, String icon_filename, String description ) {
        this.setImageResource(imageResource);
        this.setPlantName(plant_name);
        this.setPlantId(plant_id);
        this.setAge(start_date);
        this.setSource(source);
    	this.setDescription(description);
    	this.setIconFilename(icon_filename);
    }
	
	public void setAlarm(boolean value)
	{
		this.alarm = value;
	}
	
	public boolean getAlarm()
	{
		return this.alarm;
	}
	
	public void setLinks(LinkData[] links)
	{
		this.links = links;
	}
	
	public void setCareTips(CareTipData[] care_tips)
	{
		this.care_tips = care_tips;
	}

	public String getDescription() {
		return description;
	}
	
	public void setNotes(NotesData[] notes)
	{
		this.notes = notes;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public String getAge() {
		
		// TODO: do some time calculations to see how old plant is based on start date.
		
		return start_date;
	}

	public void setAge(String start_date) {
		this.start_date = start_date;
	}
	

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public String getPlantName() {
		return plant_name;
	}

	public void setPlantName(String plant_name) {
		this.plant_name = plant_name;
	}

	public int getPlantId() {
		return plant_id;
	}

	public void setPlantId(int plant_id) {
		this.plant_id = plant_id;
	}
	
	public String getIconFilename() {
		return this.icon_filename;
	}

	public void setIconFilename(String icon_filename) {
		this.icon_filename = icon_filename;
	}

	public class LinkData {
		private int plantId;
		private String description;
		private String url;
		
		public LinkData(int plantId, String description, String url)
		{
			this.plantId = plantId;
			this.description = description;
			this.url = url;
		}
		
		public int getPlantId()
		{
			return this.plantId;
		}
		
		public String getDescription()
		{
			return this.description;
		}
		
		public String getUrl()
		{
			return this.url;
		}
	}
	
	
	
	public class NotesData {
		private int plantId;
		private String note;
		private String date;
		
		public NotesData(int plantId, String note, String date)
		{
			this.plantId = plantId;
			this.note = description;
			this.date = date;
		}
		
		public int getPlantId()
		{
			return this.plantId;
		}
		
		public String getNote()
		{
			return this.note;
		}
		
		public String getDate()
		{
			return this.date;
		}
	}
}
