package com.pingan.eauthsdk;

import com.pingan.eauthsdk.component.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 错误界面
 * @author Administrator
 *
 */
public class BackErrorActivity extends Activity implements OnClickListener{
	
	TextView mTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eauth_back_error);
		Button back_main_btn = (Button)findViewById(R.id.back_main2_btn);
		back_main_btn.setOnClickListener(this);
		mTextView = (TextView) findViewById(R.id.error_prompt_info_tv);
		getDetectType();
	}
	
	public void getDetectType(){
		String detectType = (String) getIntent().getStringExtra("type");
		if(!"".equalsIgnoreCase(detectType)){
			try {
				mTextView.setText(detectType); // 错误类型显示	
			} catch (NullPointerException ne) {
				ne.printStackTrace();
			}
		} else {
			Toast.makeText(this, "detectType为空！！！", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.back_main2_btn){
			startActivity(new Intent(BackErrorActivity.this, MainActivity.class));
			BackErrorActivity.this.finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BackErrorActivity.this.finish();
	}
}
