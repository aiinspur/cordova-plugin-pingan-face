package com.pingan.eauthsdk.view;

import com.pingan.eauthsdk.util.GetResourceUtil;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


public class OtherView {
	Context mContext;
	TextView tView;
	public OtherView(Context context) {
		mContext = context;
	}
	
	public void setText(View view, String info){
		tView = (TextView) view.findViewById(GetResourceUtil.getResourceByType(mContext,"id", "eauth_for_other_text"));
		tView.setText(info);
	}
}
