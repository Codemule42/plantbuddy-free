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
package com.aftgg.plantbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			Intent serviceIntent = new Intent(context, AlarmNotificationService.class);
			context.startService(serviceIntent);
	}

}
