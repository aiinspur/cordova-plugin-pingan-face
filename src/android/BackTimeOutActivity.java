package com.pingan.eauthsdk;

import com.pingan.eauthsdk.component.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 超时回显界面
 * @author Administrator
 */
public class BackTimeOutActivity extends Activity{
	
	TextView mTextView,mTextView2;
	String timeOutType = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eauth_back_timeout);
		
		mTextView = (TextView)findViewById(R.id.prompt_info_tv);
		mTextView2 = (TextView)findViewById(R.id.prompt_info_tv2); // 超时原因
		Button back_time_btn = (Button)findViewById(R.id.back_time_btn);
		back_time_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if(view.getId() == R.id.back_time_btn){
					startActivity(new Intent(BackTimeOutActivity.this, MainActivity.class));
					BackTimeOutActivity.this.finish();
				}
			}
		});
		try {
			timeOutType = (String)getIntent().getStringExtra("time");
			if(!"".equalsIgnoreCase(timeOutType)){
				mTextView2.setText(timeOutType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BackTimeOutActivity.this.finish();
	}
	
}
