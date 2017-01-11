package com.pingan.eauthsdk.component;

import com.pingan.eauthsdk.util.GetResourceUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment createFragment(Bundle savedInstanceState);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(GetResourceUtil.getResourceByType(this,"layout","eauth_activity_fragment"));
		FragmentManager fm = getSupportFragmentManager();
		Fragment mFragment = fm.findFragmentById(GetResourceUtil.getResourceByType(this,"id","eauth_fragment_container"));
		if (mFragment == null) {
			mFragment = createFragment(savedInstanceState);
			fm.beginTransaction().add(GetResourceUtil.getResourceByType(this,"id","eauth_fragment_container"), mFragment).commit();
		}
	}

}
