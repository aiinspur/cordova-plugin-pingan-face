package com.pingan.eauthsdk.component;

import com.pingan.eauthsdk.R;
import com.pingan.eauthsdk.util.BitmapUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class NoPremissionFragment extends Fragment implements OnClickListener{
	//相机是否可用
	private boolean isCameraCanUse = true;
	private EAuthActivity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.eauth_no_premission, null);
		view.findViewById(R.id.eauth_btn_redetect).setOnClickListener(this);;
		return view;
	}

	@Override
	public void onClick(View v) {
		
		activity =  (EAuthActivity) getActivity();
		MyTask task = new MyTask();
		task.execute(activity);
	}
	
	class MyTask extends AsyncTask<Context, Void, String>{

		@Override
		protected String doInBackground(Context... params) {
			isCameraCanUse = BitmapUtil.cameraIsCanUse();
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(isCameraCanUse==true){
				activity.switchToEAuthFragment(NoPremissionFragment.this);
			}
		}
	}
	
}
