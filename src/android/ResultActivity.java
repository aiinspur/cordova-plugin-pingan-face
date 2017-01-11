package com.pingan.eauthsdk;

import com.pingan.eauthsdk.component.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * NET SDK DEMO 
 * 人脸比对回显界面
 * @author Administrator
 *
 */
public class ResultActivity extends Activity implements OnClickListener{
	
	private ImageView igv_01;
	private ImageView igv_02;
	private TextView tv_result;
	private Button back_backreult_btn;
	
	private byte[] img1;
	private byte[] img2;
	private String result_string;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eauth_back_result);
		igv_01 = (ImageView) findViewById(R.id.eauth_localimage);
		igv_02 = (ImageView) findViewById(R.id.eauth_currentimage);
		tv_result = (TextView) findViewById(R.id.eauth_result_tv);
		back_backreult_btn = (Button) findViewById(R.id.eauth_back_backresult_btn);
		back_backreult_btn.setOnClickListener(this);
		setviews();
	}
	
	private void setviews() {
		img1 = getIntent().getByteArrayExtra("img1");
		img2 = getIntent().getByteArrayExtra("img2");
		result_string = getIntent().getStringExtra("result");
		Log.d("ResultActivity", "11: "+result_string);
		Bitmap bitmap2 = BitmapFactory.decodeByteArray(img1, 0, img1.length);
		Bitmap bitmap1 = BitmapFactory.decodeByteArray(img2, 0, img2.length);
		igv_01.setImageBitmap(bitmap1);
		igv_02.setImageBitmap(bitmap2);
	
		if(result_string!=null){
			if(result_string.equals("0")){
				//tv_result.setText("是活体，验证通过");
				tv_result.setText("活体检测：活  体 \n 人脸比对：本  人 \n 检测结果：通  过 ");
			}
			
			if(result_string.equals("1")){
				//tv_result.setText("是活体，验证不通过");
				tv_result.setText("活体检测：活  体 \n 人脸比对：非本人\n 检测结果：不通过");
			}
			
			if(result_string.equals("2")){
				//tv_result.setText("非活体，验证不通过");
				tv_result.setText("活体检测：非活体\n 人脸比对：本人\n 检测结果：不通过");
			}
			
			if(result_string.equals("3")){
				//tv_result.setText("非活体，验证不通过");
				tv_result.setText("活体检测：非活体\n 人脸比对：非本人\n 检测结果：不通过");
			}
			
			if(result_string.equals("4")||result_string.equals("9")){
				tv_result.setText("非法请求");
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		startActivity(new Intent(ResultActivity.this, MainActivity.class));
		ResultActivity.this.finish();
	}
}
