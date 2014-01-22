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
