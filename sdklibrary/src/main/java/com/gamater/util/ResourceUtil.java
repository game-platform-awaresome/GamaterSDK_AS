package com.gamater.util;

import java.lang.reflect.Field;

import android.content.Context;

import com.gamater.common.Config;

public class ResourceUtil {
	private static Context sContext;

	public static void init(Context context) {
		if (context != null)
			sContext = context;
	}

	public static int getLayoutId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "layout", sContext.getPackageName());
	}

	public static int getStringId(String paramString) {
		if (sContext == null)
			return 0;
		if ("vsgm_tony_dialog_info".equalsIgnoreCase(paramString)
				|| "paytype_select_title".equalsIgnoreCase(paramString)) {
			if (Config.isOkgameLogo)
				paramString = "vsgm_tony_okgame_dialog_title";
			else if (Config.isGmLogo)
				paramString = "vsgm_tony_gamemy_dialog_title";
			else
				paramString = "vsgm_tony_dialog_info";
		}
		return sContext.getResources().getIdentifier(paramString, "string",
				sContext.getPackageName());
	}

	public static int getDrawableId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "drawable", sContext.getPackageName());
	}

	public static int getStyleId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "style",
				sContext.getPackageName());
	}

	public static int getId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "id",
				sContext.getPackageName());
	}

	public static int getColorId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "color", sContext.getPackageName());
	}

	public static int getDimenId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "dimen",
				sContext.getPackageName());
	}

	public static int getAnimId(String paramString) {
		if (sContext == null)
			return 0;
		return sContext.getResources().getIdentifier(paramString, "anim", sContext.getPackageName());
	}

	public static final int[] getStyleableIntArray(String name) {
		try {
			if (sContext == null)
				return null;
			Field field = Class.forName(sContext.getPackageName() + ".R$styleable").getDeclaredField(name);
			int[] ret = (int[]) field.get(null);
			return ret;
		} catch (Throwable t) {
		}
		return null;
	}

	public static final int getStyleableIntArrayIndex(String name) {
		try {
			if (sContext == null)
				return 0;
			// use reflection to access the resource class
			Field field = Class.forName(sContext.getPackageName() + ".R$styleable").getDeclaredField(name);
			int ret = (Integer) field.get(null);
			return ret;
		} catch (Throwable t) {
		}
		return 0;
	}

}