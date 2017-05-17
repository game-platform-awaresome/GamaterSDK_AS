package com.gamater.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appsflyer.MultipleInstallBroadcastReceiver;
import com.kochava.android.tracker.ReferralCapture;

/*
 *  A simple Broadcast Receiver to receive an INSTALL_REFERRER
 *  intent and pass it to other receivers.
 */
public class MasterReferrerReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		// Pass the intent to other receivers.
		new InstallReceiver().onReceive(context, intent);
		// appsflyer 统计
		new MultipleInstallBroadcastReceiver().onReceive(context, intent);

		// Pass the intent to the Kochava receiver.
		new ReferralCapture().onReceive(context, intent);
	}
}
