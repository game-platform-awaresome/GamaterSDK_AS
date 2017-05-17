package com.vsgm.permission;

import java.util.List;

public interface PermissionCallback {
	void onGranted();

	void onDenied(List<String> permissions);
}
