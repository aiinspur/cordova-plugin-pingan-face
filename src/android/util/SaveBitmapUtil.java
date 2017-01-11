package com.pingan.eauthsdk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pingan.paeauth.config.Path;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class SaveBitmapUtil {

	public static void saveBitmap2Sdcard(String fileName, Bitmap bmp) {

		if (bmp == null || fileName == null) {
			return;
		}

		FileOutputStream fos = null;

		try {
			File dir = new File(Path.DEBUG_UPLOAD_BITMAP_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File bitmapFile = new File(Path.DEBUG_UPLOAD_BITMAP_PATH
					+ fileName);
			bitmapFile.createNewFile();
			fos = new FileOutputStream(bitmapFile);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
				}
			}
		}

	}
}
