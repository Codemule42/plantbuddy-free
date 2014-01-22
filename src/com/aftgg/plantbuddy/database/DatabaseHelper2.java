package com.aftgg.plantbuddy.database;

import java.util.ArrayList;
import com.aftgg.plantbuddy.core.AlarmData;
import com.aftgg.plantbuddy.core.PlantData;
import com.aftgg.plantbuddy.core.CareTipData;
import com.aftgg.plantbuddy.core.LinkData;
import com.aftgg.plantbuddy.utility.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper2 extends SQLiteOpenHelper {
	static final String dbName ="plants";
	
	static final String plantsTable = "plants";
	static final String colPlantID = "plant_id";
	static final String colPlantName = "plant_name";
	static final String colStartDate = "start_date";
	static final String colSource = "source";
	static final String colThumbFilename = "thumb_filename";
	static final String colDescription = "desctiption";
	
	static final String plantLinksTable = "plant_links";
	static final String colPlantLinkId = "plant_link_id";
	static final String colLinkDescription = "link_description";
	static final String colLinkUrl = "link_url";
	
	static final String plantImagesTable = "plant_images";
	static final String colPlantImageId = "plant_image_id";
	static final String colPlantImageUri = "image_uri";
	
	static final String plantNotesTable = "plant_notes";
	static final String colPlantNoteId = "plant_note_id";
	static final String colNote = "notes";
	static final String colDate = "date";
	
	static final String plantCareTipsTable = "plant_care_tips";
	static final String colPlantCareTipId = "plant_care_tip_id";
	static final String colCareTip = "care_tip";
	
	static final String plantAlarmsTable = "plant_alarms";
	static final String colPlantAlarmId = "plant_alarm_id";
	static final String colAlarmText = "alarm_text";
	static final String colTimeQuantity = "time_quantity";
	static final String colTimeUom = "time_uom";
	static final String colIsOneTime = "is_one_time";
	static final String colOneTimeDate = "one_time_date";
	static final String colOrigDate = "original_date";
	static final String colNextAlarmDate = "next_alarm_date";
	
	static final String viewPlants = "view_plants";
	static final String viewNotes = "view_notes";
	static final String viewLinks = "view_links";
	static final String viewImages = "view_images";
	static final String viewCareTips = "view_care_tips";
	
	public DatabaseHelper2(Context context) {
		super(context, dbName, null, 15);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create plants table
		db.execSQL("CREATE TABLE " + plantsTable + " (" + colPlantID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
														  colPlantName + " TEXT NOT NULL, "+
														  colStartDate + " TEXT, " +
														  colSource + " TEXT, " +
														  colThumbFilename + " TEXT, " +
														  colDescription + " TEXT)");
		db.execSQL("CREATE TABLE " + plantImagesTable + " (" + colPlantImageId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
				  colPlantImageUri + " TEXT NOT NULL, "+
				  colPlantID + " INTEGER NOT NULL );");
				  //colPlantID + "INTEGER NOT NULL, FOREIGN KEY (" + colPlantID + ") REFERENCES " + plantsTable + "( " + colPlantID +"));");
		
		db.execSQL("CREATE TABLE  "+ plantLinksTable + " ( " + colPlantLinkId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					colPlantID + " INTEGER NOT NULL, " +
					colLinkDescription + " TEXT NOT NULL, " +
					colLinkUrl + " TEXT NOT NULL );");
		
		db.execSQL("CREATE TABLE " + plantNotesTable + " ( " + colPlantNoteId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					colPlantID + " INTEGER NOT NULL, " +
					colNote + " TEXT NOT NULL, " +
					colDate + " TEXT NOT NULL );");
		
		db.execSQL("CREATE TABLE " + plantCareTipsTable + " ( " + colPlantCareTipId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					colPlantID + " INTEGER NOT NULL, " +
					colCareTip + " TEXT NOT NULL );");
		
		db.execSQL("CREATE TABLE " + plantAlarmsTable + " (" + colPlantAlarmId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
				  colPlantID + " INTEGER NOT NULL, " +	
				  colAlarmText + " TEXT NOT NULL, "+
				  colTimeQuantity + " INTEGER, " +
				  colTimeUom + " TEXT, " +
				  colIsOneTime + " TEXT, " +
				  colOneTimeDate + " TEXT, " +
				  colOrigDate + " TEXT, " +
				  colNextAlarmDate + " TEXT)");
		
		// TODO: Create views, (one for each table)
		// TODO: Create triggers to force use of FKs
		
		/*db.execSQL("CREATE TRIGGER fk_plantimage_plant_id " +
		"BEFORE INSERT " +
		" ON " + plantImagesTable +
		" FOR EACH ROW BEGIN " +
		" SELECT CASE WHEN ((SELECT "+ colPlantID + " FROM " + plantsTable +
		" WHERE " + colPlantID + "=new."+ colPlantName +" ) IS NULL)" +
		" THEN RAISE (ABORT, 'Foreign Key Violation') END;" +
		" END;");*/
		
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + plantsTable);
		db.execSQL("DROP TABLE IF EXISTS " + plantImagesTable);
		db.execSQL("DROP TABLE IF EXISTS " + plantLinksTable);
		db.execSQL("DROP TABLE IF EXISTS " + plantNotesTable);
		db.execSQL("DROP TABLE IF EXISTS " + plantCareTipsTable);
		db.execSQL("DROP TABLE IF EXISTS " + plantAlarmsTable);
		// TODO: drop other tables, triggers, and views
		onCreate(db);
	}
	
	public ArrayList<PlantData> getPlants(Context c)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + plantsTable, null);
		
		ArrayList<PlantData> plantArray = new ArrayList<PlantData>();
		
		cursor.moveToFirst();
        while (cursor.isAfterLast() == false) 
        {	
        	plantArray.add(new PlantData(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), 0, cursor.getString(4), cursor.getString(5)));
	        cursor.moveToNext();
        }
        
        for(int i = 0; i < plantArray.size(); i++)
        {
        	ArrayList<AlarmData> alarms = getAlarms(c, plantArray.get(i).getPlantId());
        	
        	for(int j = 0; j < alarms.size(); j++)
        	{
        		if(Utility.hasAlarmDatePassed(alarms.get(j).getNextAlarmDate()))
        		{
        			plantArray.get(i).setAlarm(true);
        			break;
        		}
        	}
        }
        
        
        return plantArray;
	}
	
	public int updatePlant(int plantId, String plant_name, String plant_description, String plant_source, String plant_age)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colPlantName, plant_name);
		values.put(colStartDate, plant_age);
		values.put(colSource, plant_source);
		values.put(colDescription, plant_description);
		
		if(plantId > 0)
		{
			String where = colPlantID + "=?";
			String[] whereArgs = {String.valueOf(plantId)};

			return db.update("plants", values, where, whereArgs);
		}
		else
		{
			db.insert(plantsTable, null, values);
			db.close();
			return 1;
		}
	}
	
	public String[] getPlantImages(int plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantImageUri, colPlantID};
		Cursor c = db.query(plantImagesTable, columns, colPlantID+"=?", new String[]{String.valueOf(plantId)}, null, null, null);
		String[] imageUriArray = new String[c.getCount()];
		
		int i = 0;
		c.moveToFirst();
        while (c.isAfterLast() == false) 
        {
        	imageUriArray[i] = c.getString(0);
	        c.moveToNext();
        	i++;
        }
        
        
        return imageUriArray;
	}
	
	public CharSequence[] getAllPlantNames()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantName};
		Cursor c = db.query(plantsTable, columns, null, null, null, null, null);
		CharSequence[] plantNameArray = new CharSequence[c.getCount()];
		
		int i = 0;
		c.moveToFirst();
        while (c.isAfterLast() == false) 
        {
        	plantNameArray[i] = c.getString(0);
	        c.moveToNext();
        	i++;
        }
        
        
        return plantNameArray;
	}
	
	public void removeImage(int plantId, String imageUri)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantImagesTable,colPlantImageUri+"=?", new String [] {String.valueOf(imageUri)});
		db.close();
		
		updatePlantThumbnail(plantId, null);
	}
	
	public void addPlantImage(String uri, int plantId)
	{
		ContentValues values = new ContentValues();
		values.put(colPlantID, plantId);
		values.put(colPlantImageUri, uri);
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(plantImagesTable, null, values);
	}
	
	public void updatePlantThumbnail(int plantId, String uri)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(colThumbFilename, uri);
		
		String where = colPlantID + "=?";
		String[] whereArgs = {String.valueOf(plantId)};
		
		db.update("plants", values, where, whereArgs);
	}
	
	public int getPlantIdByName(String plantName)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantID};
		Cursor cursor = db.query(plantsTable, columns, colPlantName+"=?", new String[]{plantName}, null, null, null);
		int plantId = 0;
		
		cursor.moveToFirst();
		plantId = cursor.getInt(0);
        
        return plantId;
	}
	
	public String getPlantNameById(String plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantName};
		Cursor cursor = db.query(plantsTable, columns, colPlantID+"=?", new String[]{plantId}, null, null, null);
		String plantName = "";
		
		cursor.moveToFirst();
		plantName = cursor.getString(0);
        
        return plantName;
	}
	
	public ArrayList<LinkData> getLinks(Context c, int plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantLinkId, colLinkDescription, colLinkUrl};
		Cursor cursor = db.query(plantLinksTable, columns, colPlantID+"=?", new String[]{String.valueOf(plantId)}, null, null, null);
		
		ArrayList<LinkData> linkArray = new ArrayList<LinkData>();
		
		cursor.moveToFirst();
        while ( cursor.isAfterLast() == false) 
        {
        	linkArray.add(new LinkData (cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        	cursor.moveToNext();
        }
        
        return linkArray;
	}
	
	public void removePlant(int plantId)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantsTable,colPlantID+"=?", new String [] {String.valueOf(plantId)});
		db.delete(plantImagesTable,colPlantID+"=?", new String [] {String.valueOf(plantId)});
		db.delete(plantLinksTable,colPlantID+"=?", new String [] {String.valueOf(plantId)});
		db.delete(plantNotesTable,colPlantID+"=?", new String [] {String.valueOf(plantId)});
		db.delete(plantCareTipsTable,colPlantID+"=?", new String [] {String.valueOf(plantId)});
		db.delete(plantAlarmsTable,colPlantID+"=?", new String [] {String.valueOf(plantId)});
		db.close();
	}
	
	public void addCareTip(int plantId, String tip)
	{
		ContentValues values = new ContentValues();
		values.put(colPlantID, plantId);
		values.put(colCareTip, tip);
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(plantCareTipsTable, null, values);
	}
	
	public void removeCareTip(int tipId)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantCareTipsTable,colPlantCareTipId+"=?", new String [] {String.valueOf(tipId)});
		db.close();
	}
	
	public ArrayList<CareTipData> getCareTips(Context c, int plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantCareTipId, colPlantID, colCareTip};
		Cursor cursor = db.query(plantCareTipsTable, columns, colPlantID+"=?", new String[]{String.valueOf(plantId)}, null, null, null);
		
		ArrayList<CareTipData> tipArray = new ArrayList<CareTipData>();
		
		cursor.moveToFirst();
        while ( cursor.isAfterLast() == false) 
        {
        	tipArray.add(new CareTipData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)));
        	cursor.moveToNext();
        }
        
        return tipArray;
	}

	public void updateCareTip(int tipId, String newTip)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colCareTip, newTip);
		
		String where = colPlantCareTipId + "=?";
		String[] whereArgs = {String.valueOf(tipId)};

		db.update(plantCareTipsTable, values, where, whereArgs);
		
	}
	
	public void addLink(int plantId, String description, String URL)
	{
		ContentValues values = new ContentValues();
		values.put(colPlantID, plantId);
		values.put(colLinkDescription, description);
		values.put(colLinkUrl, URL);
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(plantLinksTable, null, values);
	}
	
	public void removeLink(int linkId)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantLinksTable,colPlantLinkId+"=?", new String [] {String.valueOf(linkId)});
		db.close();
	}
	
	public void updateLink(int linkId, String newDescription, String newUrl)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colLinkDescription, newDescription);
		values.put(colLinkUrl, newUrl);
		
		String where = colPlantLinkId + "=?";
		String[] whereArgs = {String.valueOf(linkId)};

		db.update(plantLinksTable, values, where, whereArgs);
		
	}
	
	public void updateDescription(int plantId, String newDescription)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colDescription, newDescription);
		
		String where = colPlantID + "=?";
		String[] whereArgs = {String.valueOf(plantId)};

		db.update(plantsTable, values, where, whereArgs);
		
	}
	
	public void updateAge(int plantId, String newDate)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colStartDate, newDate);
		
		String where = colPlantID + "=?";
		String[] whereArgs = {String.valueOf(plantId)};

		db.update(plantsTable, values, where, whereArgs);
		
	}
	
	public void updateSource(int plantId, String newSource)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colSource, newSource);
		
		String where = colPlantID + "=?";
		String[] whereArgs = {String.valueOf(plantId)};

		db.update(plantsTable, values, where, whereArgs);
		
	}
	
	public void addNote(int plantId, String note, String date)
	{
		ContentValues values = new ContentValues();
		values.put(colPlantID, plantId);
		values.put(colNote, note);
		values.put(colDate, date);
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(plantNotesTable, null, values);
	}
	
	public void removeNotes(int noteId)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantNotesTable,colPlantNoteId+"=?", new String [] {String.valueOf(noteId)});
		db.close();
	}
	
	public ArrayList<LinkData> getNotes(Context c, int plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantNoteId, colDate, colNote, colPlantID};
		Cursor cursor = db.query(plantNotesTable, columns, colPlantID + "=?", new String[]{String.valueOf(plantId)}, null, null, null);
		
		ArrayList<LinkData> linkArray = new ArrayList<LinkData>();
		
		cursor.moveToFirst();
        while ( cursor.isAfterLast() == false) 
        {
        	linkArray.add(new LinkData(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        	cursor.moveToNext();
        }
        
        return linkArray;
	}

	public void updateNote(int tipId, String newTip)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(colCareTip, newTip);
		
		String where = colPlantCareTipId + "=?";
		String[] whereArgs = {String.valueOf(tipId)};

		db.update(plantNotesTable, values, where, whereArgs);
		
	}
	
	public void addAlarm(int plantId, String text, int timeQuantity, String timeUom, String oneTimeDate, String dateString) throws java.text.ParseException
	{
		ContentValues values = new ContentValues();
		values.put(colPlantID, plantId);
		values.put(colAlarmText, text);
		values.put(colTimeQuantity, timeQuantity);
		values.put(colTimeUom, timeUom);
		
		if(oneTimeDate != null)
		{
			values.put(colIsOneTime, "true");
			values.put(colOneTimeDate, oneTimeDate);
			values.put(colNextAlarmDate, oneTimeDate);
		}
		else
		{
			String nextAlarmDate = Utility.calculateNextAlarmDate(dateString, timeQuantity, timeUom);
			
			values.put(colNextAlarmDate, nextAlarmDate);
			values.put(colIsOneTime, "false");
		}
		
		values.put(colOneTimeDate, oneTimeDate);
		values.put(colOrigDate, dateString);
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(plantAlarmsTable, null, values);
	}
	
	public ArrayList<AlarmData> getAlarms(Context c, int plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String [] columns = new String[]{colPlantAlarmId, colPlantID, colAlarmText, colTimeQuantity, colTimeUom, colIsOneTime, colOneTimeDate, colOrigDate, colNextAlarmDate};
		
		String selection = null;
		String[] selectionArgs = null;
		
		if(plantId > 0)
		{
			selection = colPlantID + "=?";
			selectionArgs = new String[]{String.valueOf(plantId)};
		}
		
		Cursor cursor = db.query(plantAlarmsTable, columns, selection, selectionArgs, null, null, null);
		
		ArrayList<AlarmData> alarmArray = new ArrayList<AlarmData>();
		
		cursor.moveToFirst();
        while ( cursor.isAfterLast() == false) 
        {
        	alarmArray.add(new AlarmData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5),
        								cursor.getString(6), cursor.getString(7), cursor.getString(8)));
        	cursor.moveToNext();
        }
        
        return alarmArray;
	}
	
	public void removeAlarm(int alarmId)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantAlarmsTable,colPlantAlarmId+"=?", new String [] {String.valueOf(alarmId)});
		db.close();
	}
	
	public void removeAllAlarms()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(plantAlarmsTable, null, null);
		db.close();
	}
	
	public void resetAlarms(int[] alarmIDs, int plantId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = new String[]{colPlantAlarmId, colPlantID, colAlarmText, colTimeQuantity, colTimeUom, colIsOneTime, colOneTimeDate, colOrigDate, colNextAlarmDate};
		
		String selection = colPlantAlarmId + " IN (";
		ArrayList<String> selectionArgs = new ArrayList<String>();
		
		for(int i = 0; i < alarmIDs.length; i++)
		{
			
			if(alarmIDs[i] > 0)
			{	
				selection += "?,";
				String anAlarmId = String.valueOf(alarmIDs[i]);
				selectionArgs.add(anAlarmId);
			}
		}
		
		selection = selection.substring(0, selection.length() - 1) + ")";
		
		String[] selectionArgsArray = selectionArgs.toArray(new String[selectionArgs.size() - 1]);
		
		Cursor cursor = db.query(plantAlarmsTable, columns, selection, selectionArgsArray, null, null, null);
		
		ArrayList<AlarmData> alarmArray = new ArrayList<AlarmData>();
		
		cursor.moveToFirst();
        while ( cursor.isAfterLast() == false) 
        {
        	alarmArray.add(new AlarmData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5),
        								cursor.getString(6), cursor.getString(7), cursor.getString(8)));
        	cursor.moveToNext();
        }
        
		SQLiteDatabase writeableDB = this.getWritableDatabase();
	
		for(int j = 0; j < alarmArray.size(); j++)
		{
			if(alarmArray.get(j).getPlantID() != plantId)
			{
				// If the alarm to be reset isn't for the selected plant, don't reset it.
				// This way an alarm won't automatically be reset until the plant with the alarm is viewed.
				continue;
			}
			ContentValues values = new ContentValues();
		
			if(alarmArray.get(j).isOneTime())
			{
				SQLiteDatabase anotherDB =this.getWritableDatabase();
				anotherDB.delete(plantAlarmsTable,colPlantAlarmId+"=?", new String [] {String.valueOf(alarmArray.get(j).getPlantAlarmId())});
				anotherDB.close();
				
				continue;
			}
			
			String nextAlarmDate = Utility.calculateNextAlarmDate(alarmArray.get(j).getNextAlarmDate(), alarmArray.get(j).getTimeQuantity(), alarmArray.get(j).getTimeUom());
		
			values.put(colNextAlarmDate, nextAlarmDate);
				
			String where = colPlantAlarmId + "=?";
			String[] whereArgs = {String.valueOf(alarmArray.get(j).getPlantAlarmId())};

			writeableDB.update(plantAlarmsTable, values, where, whereArgs);
		}
	}
	
	 public void resetRecurringAlarms(int alarmID, String previousNextAlarmDate, int timeQuantity, String timeUom)	
	 {
		 SQLiteDatabase db = this.getWritableDatabase();
	
		 ContentValues values = new ContentValues();

	     String nextAlarmDate = Utility.calculateNextAlarmDate(previousNextAlarmDate, timeQuantity, timeUom);

	     values.put(colNextAlarmDate, nextAlarmDate);
	              
	     String where = colPlantAlarmId + "=?";	
	     String[] whereArgs = {String.valueOf(alarmID)};
	
	     db.update(plantAlarmsTable, values, where, whereArgs);	
	 }
}
