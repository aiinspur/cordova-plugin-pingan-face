package com.pingan.eauthsdk.component;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.pingan.eauthsdk.api.EAuthApi;
import com.pingan.eauthsdk.api.EAuthRequest;
import com.pingan.eauthsdk.util.BitmapUtil;
import com.pingan.eauthsdk.util.GetResourceUtil;


/**
 * EAuth Activity
 * @author Administrator
 *
 */
public class EAuthActivity extends SingleFragmentActivity {
	
	private Bundle mArgs;
	
	@Override
	protected Fragment createFragment(Bundle savedInstanceState) {
		EAuthRequest mEAuthRequest = (EAuthRequest) getIntent()
				.getParcelableExtra(EAuthApi.EAUTH_REQUEST_PARCELABLE);
		Bundle args = new Bundle();
		args.putParcelable(EAuthApi.EAUTH_REQUEST_PARCELABLE, mEAuthRequest);
		mArgs =  args;
		//判断是否开启了相机权限
		if (BitmapUtil.cameraIsCanUse() == true) {
			EAuthFragment fragment = new EAuthFragment();
			fragment.setArguments(mArgs);
			return fragment;
		}else{
			Log.e("test", "没权限");
			NoPremissionFragment fragment = new NoPremissionFragment();
			return fragment;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK==keyCode){
			Toast.makeText(this, "您已退出验证", Toast.LENGTH_SHORT).show();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public void switchToEAuthFragment(NoPremissionFragment noPreFragment) {
		FragmentManager fm = getSupportFragmentManager();
		EAuthFragment mFragment = new EAuthFragment();
		mFragment.setArguments(mArgs);
		fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
		hide(noPreFragment).add(GetResourceUtil.getResourceByType(this,"id","eauth_fragment_container"), mFragment).commit();
	}

	
	
}
