package com.pingan.eauthsdk;

import com.pingan.eauthsdk.api.EAuthFaceInfo;
import com.pingan.eauthsdk.component.MainActivity;
import com.pingan.eauthsdk.util.BitmapUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Local SDK 返回成功界面
 * @author Administrator
 *
 */
public class BackSuccesActivity extends Activity{
	private static String TAG = "BackSuccesActivity";
	
	TextView bright_tv;
	TextView blurness_tv;
	TextView eye_degree_tv;
	TextView yaw_tv;
	TextView pitch_tv;
	TextView rotate_tv;
	TextView mouth_motion_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eauth_back_success);
		
		ImageView passView = (ImageView) findViewById(R.id.success_mark_iv);
		bright_tv = (TextView) findViewById(R.id.bright_value_info_tv);
		blurness_tv = (TextView) findViewById(R.id.blurness_value_info_tv);
		eye_degree_tv = (TextView) findViewById(R.id.eye_degree_value_info_tv);
		yaw_tv = (TextView) findViewById(R.id.yaw_value_info_tv);
		pitch_tv = (TextView) findViewById(R.id.pitch_value_info_tv);
		rotate_tv = (TextView) findViewById(R.id.rotate_value_info_tv);
		mouth_motion_tv = (TextView) findViewById(R.id.mouth_motion_value_info_tv);
		Button back_main_btn = (Button) findViewById(R.id.back_main_btn);
		back_main_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.back_main_btn) {
					startActivity(new Intent(BackSuccesActivity.this, MainActivity.class));
					BackSuccesActivity.this.finish();
				}
			}
		});
		setBitmap(passView);
		
	}
	
	/**
	 * 设置回显的图片数据
	 * @param imgview
	 */
	public void setBitmap(ImageView imgview){
		EAuthFaceInfo mData=(EAuthFaceInfo) getIntent().getParcelableExtra("data");
		if(mData ==null){
			Log.e(TAG, "The PALivenessDetectionFrame data is null!");
			return;
		}
		bright_tv.setText("亮度："+String.valueOf(mData.getBrightness()));
		blurness_tv.setText("模糊度："+String.valueOf(mData.getMotion_blurness()));
		eye_degree_tv.setText("眼睛睁开程度："+String.valueOf(mData.getEye_hwratio()));
		yaw_tv.setText("左右角度："+String.valueOf(mData.getYaw()));
		pitch_tv.setText("俯仰角度："+String.valueOf(mData.getPitch()));
		rotate_tv.setText("倾斜角度："+String.valueOf(mData.getRotate()));
		mouth_motion_tv.setText("嘴巴动作程度："+String.valueOf(mData.getMouth_motion()));
		Bitmap smallBitmap=mData.getBitmapImage();
		imgview.setImageBitmap(smallBitmap);   // 显示在页面端
		BitmapUtil.saveImageToGallery(smallBitmap); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BackSuccesActivity.this.finish();
	}
}
