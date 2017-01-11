package com.pingan.eauthsdk.component;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

import com.pingan.eauthsdk.R;

/**
 * 首界面
 * @author Administrator
 *
 */
public class MainActivity extends FragmentActivity {


	protected Fragment createFragment(Bundle savedInstanceState) {
		return new MainFragment();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(R.layout.eauth_activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment mFragment = fm.findFragmentById(R.id.eauth_fragment_container);
		if (mFragment == null) {
			mFragment = createFragment(savedInstanceState);
			fm.beginTransaction().add(R.id.eauth_fragment_container, mFragment).commit();
		}

		//this.finish();
	}

}
