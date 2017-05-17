package com.gamater.receiver;

import java.util.List;

import com.gamater.sdk.game.GamaterSDK;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import bolts.AppLinks;

public class OpenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent data) {
		Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(context, data);
		if (targetUrl != null) {
			String packageName = targetUrl.getQueryParameter("package");
			if (context.getPackageName().equals(packageName)) {
				boolean isSDKInit = GamaterSDK.getInstance() == null
						|| GamaterSDK.getInstance().getActivity() == null;
				PackageManager pm = context.getPackageManager();
				Intent intent = pm.getLaunchIntentForPackage(packageName);
				if (intent != null) {
					List<ResolveInfo> list = context.getPackageManager()
							.queryIntentActivities(intent, 0);
					if (list != null) {
						if (list.size() > 0) {
							ResolveInfo ri = list.iterator().next();
							if (ri != null) {
								ComponentName cn = new ComponentName(
										ri.activityInfo.packageName,
										ri.activityInfo.name);
								Intent launchIntent = new Intent();
								launchIntent.setComponent(cn);
								context.startActivity(launchIntent);
							}
						}
					}
				}
			}
		}
	}

}
