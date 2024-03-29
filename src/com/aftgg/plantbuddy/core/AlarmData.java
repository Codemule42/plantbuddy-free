/*
    Copyright 2012 Keith W. Silliman

    Plant Buddy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Plant Buddy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Plant Buddy.  If not, see <http://www.gnu.org/licenses/>
*/
package com.aftgg.plantbuddy.core;

public class AlarmData {
	private int   		plantAlarmId;
	private int		plantID;
	private String 		alarmText;
	private int 		timeQuantity;
	private String 		timeUom;
	private boolean	isOneTime;
	private String 		oneTimeDate;
	private String 		origDate;
	private String     nextAlarmDate;

	public AlarmData(int plantAlarmId, int	plantID, String alarmText, int 	timeQuantity, String timeUom, 
						String isOneTime, String oneTimeDate, String origDate, String nextAlarmDate) {
		this.plantAlarmId = plantAlarmId;
		this.plantID = plantID;
		this.alarmText = alarmText;
		this.timeQuantity = timeQuantity;
		this.timeUom = timeUom;
		this.isOneTime = Boolean.parseBoolean(isOneTime);
		this.oneTimeDate = oneTimeDate;
		this.origDate = origDate;
		this.nextAlarmDate = nextAlarmDate;
    }

	public int getPlantAlarmId() {
		return plantAlarmId;
	}

	public void setPlantAlarmId(int plantAlarmId) {
		this.plantAlarmId = plantAlarmId;
	}

	public int getPlantID() {
		return plantID;
	}

	public void setPlantID(int plantID) {
		this.plantID = plantID;
	}

	public String getAlarmText() {
		return alarmText;
	}

	public void setAlarmText(String alarmText) {
		this.alarmText = alarmText;
	}

	public int getTimeQuantity() {
		return timeQuantity;
	}

	public void setTimeQuantity(int timeQuantity) {
		this.timeQuantity = timeQuantity;
	}

	public String getTimeUom() {
		return timeUom;
	}

	public void setTimeUom(String timeUom) {
		this.timeUom = timeUom;
	}

	public boolean isOneTime() {
		return isOneTime;
	}

	public void setOneTime(boolean isOneTime) {
		this.isOneTime = isOneTime;
	}

	public String getOneTimeDate() {
		return oneTimeDate;
	}

	public void setOneTimeDate(String oneTimeDate) {
		this.oneTimeDate = oneTimeDate;
	}

	public String getOrigDate() {
		return origDate;
	}

	public void setOrigDate(String origDate) {
		this.origDate = origDate;
	}

	public String getNextAlarmDate() {
		return nextAlarmDate;
	}

	public void setNextAlarmDate(String nextAlarmDate) {
		this.nextAlarmDate = nextAlarmDate;
	}
}