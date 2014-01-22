package com.aftgg.plantbuddy.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
	
	public enum eTimeUom {
	    Minutes, Hours, Days, Weeks, Months, Seasons, Years
	}
	
	@SuppressWarnings("unused")
	public static String calculateNextAlarmDate(String previousAlarmDate, int timeQuantity, String timeUom)
	{
		 Date today = null;
		 String nextAlarmDate = null;
		 SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		 
		 try {
			 today = formatter.parse(previousAlarmDate);
		    } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 Calendar c = Calendar.getInstance();
		 //c.setTime(today);
		// Calculate the next alarm date by adding N days/months/seasons/years
		switch(eTimeUom.valueOf(timeUom))
		{
			case Minutes:
				c.add(Calendar.MINUTE, timeQuantity);
				nextAlarmDate = formatter.format(c.getTime());
				break;
			case Days:
				c.add(Calendar.DATE, timeQuantity);
				nextAlarmDate = formatter.format(c.getTime());
				break;
			case Weeks:
				c.add(Calendar.WEEK_OF_YEAR, timeQuantity);
				nextAlarmDate = formatter.format(c.getTime());
				break;
			case Months:
				c.add(Calendar.MONTH, timeQuantity);
				nextAlarmDate = formatter.format(c.getTime());
				break;
			case Seasons:
				c.add(Calendar.MONTH, timeQuantity * 3);
				nextAlarmDate = formatter.format(c.getTime());
				break;
			case Years:
				c.add(Calendar.YEAR, timeQuantity);
				nextAlarmDate = formatter.format(c.getTime());
				break;
			default:
				break;
		}
		
		return nextAlarmDate;
	}

	public static boolean hasAlarmDatePassed(String alarmDate)
	{
		Calendar today = Calendar.getInstance();
		// Zero out the hour, minute, second, and millisecond
	    today.set(Calendar.HOUR_OF_DAY, 0);
	    today.set(Calendar.MINUTE, 0);
	    today.set(Calendar.SECOND, 0);
	    today.set(Calendar.MILLISECOND, 0);
	      	
	    Date currentDate = today.getTime();
		Date alarmDateObj = null;
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		 
		 try {
			 alarmDateObj = formatter.parse(alarmDate);
		    } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 if(alarmDateObj.before(currentDate) || alarmDateObj.equals(currentDate))
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 } 
	}
}
