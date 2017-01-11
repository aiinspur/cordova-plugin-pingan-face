package com.pingan.eauthsdk.util;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {

	public static int[] getScreenSize(Context context) {
		if (context == null) {
			return new int[] { 1, 1 };
		}
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return new int[] { display.getWidth(), display.getHeight() };
	}
}
