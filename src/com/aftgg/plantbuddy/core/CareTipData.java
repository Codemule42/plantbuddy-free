package com.aftgg.plantbuddy.core;

public class CareTipData {
	private int plantId;
	private String tip;
	private int tipId;
	
	public CareTipData(int tipId, int plantId, String tip)
	{
		this.plantId = plantId;
		this.tip = tip;
		this.tipId = tipId;
	}
	
	public int getPlantId()
	{
		return this.plantId;
	}
	
	public String getTip()
	{
		return this.tip;
	}
	
	public int getTipId()
	{
		return this.tipId;
	}
}