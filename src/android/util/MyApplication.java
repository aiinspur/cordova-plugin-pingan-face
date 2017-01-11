package com.pingan.eauthsdk.util;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CallbackContext;

/**
 *
 * @ClassName: MyApplication.java
 * @Description: application共享信息
 * @author wxy
 * @version V1.0
 * @Date 2015年12月08日
 */
@SuppressWarnings({ "unused" })

public class MyApplication extends Application {
	private CallbackContext callbackContext;
	private String savePath;    // 测试环境和存储路径
	private String imgNum;      // 测试数量

	// private int countDown;     // 倒计时

	private boolean is_real; // 检查类型
	private boolean is_flag; // 页面跳转执行判断

	private boolean is_develop = false; //判断是否属于开发状态

	private String userId = "0";
	//上传对比图片的uri
	private Uri bitmapUri = null;

	//private String randomMode = "0";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	/*
	 * 用于设置用户上传照片的uri
	 * */
	public void setBitmapUri(Uri uri){
		if(uri!=null){
			this.bitmapUri = uri;
		}
	}

	public Uri getBitmapUri(){
		return bitmapUri;
	}

	public String getBitmapUrl(){
		return "http://renjufu.eigpay.com/f9027e9c-8111-4237-a085-2103de77c32f.jpg";
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		 * 在Application里面加上下面这2句：
		 * 这样就可以随时得到crash时候的log信息了
		 */
//		CustomCrashHandler catchHandler = CustomCrashHandler.getInstance();
//        catchHandler.init(getApplicationContext()); 

		setImgNum(imgNum);
		setSavePath(savePath);
		//setCountDown(countDown);
		setIs_real(is_real);
		setIs_flag(is_flag);

	}

	public boolean isIs_flag() {
		return is_flag;
	}


	public void setIs_flag(boolean is_flag) {
		this.is_flag = is_flag;
	}


	public boolean isIs_real() {
		return is_real;
	}


	public void setIs_real(boolean is_real) {
		this.is_real = is_real;
	}


	public String getImgNum() {
		return imgNum;
	}

	public void setImgNum(String imgNum) {
		this.imgNum = imgNum;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

/*	public int getCountDown() {
		return countDown;
	}

	public void setCountDown(int countDown) {
		this.countDown = countDown;
	}*/
	
/*	public String getRandomMode() {
		return randomMode;
	}

	public void setRandomMode(String randomMode) {
		this.randomMode = randomMode;
	}*/


	public CallbackContext getCallbackContext() {
		return callbackContext;
	}

	public  void setCallbackContext(CallbackContext callbackContext) {
		this.callbackContext = callbackContext;
	}
}
