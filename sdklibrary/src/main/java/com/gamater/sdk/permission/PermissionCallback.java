package com.gamater.sdk.permission;

import java.util.List;

public interface PermissionCallback {
	void onGranted();

	void onDenied(List<String> permissions);
}
