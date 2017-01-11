package com.pingan.eauthsdk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

public class CopyFileFromAssets {
	/**
	 * 
	 * @param myContext
	 * @param ASSETS_NAME
	 *            要复制的文件名
	 * @param savePath
	 *            要保存的路径
	 * @param saveName
	 *            复制后的文件名 testCopy(Context context)是一个测试例子。
	 */

	public static void copy(Context myContext, String ASSETS_NAME,
			String savePath, String saveName) {
		String filename = savePath + "/" + saveName;
		File dir = new File(savePath);
		// 如果目录不中存在，创建这个目录
		if (!dir.exists())
			dir.mkdir();
		try {
			if (!(new File(filename)).exists()) {
				InputStream is = myContext.getResources().getAssets()
						.open(ASSETS_NAME);
				FileOutputStream fos = new FileOutputStream(filename);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean fileIsExists(String filePath) {
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * TODO 将Model复制到安卓apk目录下
	 * @param mContext
	 * @param ASSETS_NAME
	 * @param savePath
	 * @param saveName
	 */
	static String model_haar = "haarcascade_frontalface_alt2.xml";
	static String dmodel  = "3dmodel.bin";
	public static void copyAllModel(Context mContext,String savePath){
		CopyFileFromAssets.copy(mContext, model_haar, savePath, model_haar);
		CopyFileFromAssets.copy(mContext, dmodel, savePath, dmodel);
	}
}
