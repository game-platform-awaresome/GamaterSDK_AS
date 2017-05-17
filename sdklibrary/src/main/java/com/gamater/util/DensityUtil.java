package com.gamater.util;

import android.content.Context;

/**
 * dp轉換px工具類
 * 
 * @author tony
 *
 */
public class DensityUtil {
	public static int dip2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
