package com.pingan.eauthsdk.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pingan.eauthsdk.BackErrorActivity;
import com.pingan.eauthsdk.BackTimeOutActivity;
import com.pingan.eauthsdk.ProgressBarActivity;
import com.pingan.eauthsdk.R;
import com.pingan.eauthsdk.api.EAuthApi;
import com.pingan.eauthsdk.api.EAuthFaceInfo;
import com.pingan.eauthsdk.api.EAuthRequest;
import com.pingan.eauthsdk.api.EAuthResponse;
import com.pingan.eauthsdk.api.EAuthResponseType;
import com.pingan.eauthsdk.api.PafaceConfig;
import com.pingan.eauthsdk.util.MyApplication;
import com.pingan.paeauth.config.Path;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainFragment extends Fragment{
	
	private final static String TAG = "MainFragment";
	
	private MyApplication myApp = null;

	/*用户ID列表*/
	private List<String> userList = new ArrayList<String>();
	/*当前用户ID*/
	//private String userId;
	/*动作类型*/
	private int activeRandomMode=0; //0：无动作| 1：张嘴动作 | 2：张嘴动作和摇头动作随机
	//比对的bitmap uri
	private Uri bitmapUri = null;
	public final static int IMAGE_CODE = 0;
	public final static String IMAGE_TYPE = "image/*";
	private boolean isSelectedPic = false;
	
	
	TextView _noticeText;
	
	private final static int REQUEST_CODE = 0x05;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.eauth_fragment_main, container, false);
		checkPermission();
		Button _button = (Button) view.findViewById(R.id.eauth_btn_start_detect);
		_button.setOnClickListener(mOnClickListener);
		_noticeText = (TextView) view.findViewById(R.id.eauth_notice_tv);
		
		view.findViewById(R.id.eauth_add_pic).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bitmapUri=null;
				selectPic();
			}
		});
		
	
		final RadioButton cb_isSaveImage = (RadioButton) view.findViewById(R.id.cb_save_image);
		cb_isSaveImage.setChecked(PafaceConfig.sDebugBitmap);
		cb_isSaveImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PafaceConfig.sDebugBitmap = !PafaceConfig.sDebugBitmap;
				cb_isSaveImage.setChecked(PafaceConfig.sDebugBitmap);
			}
		});
		
		TextView saveImagePath = (TextView) view.findViewById(R.id.tv_save_image_path);
		saveImagePath.setText(Path.DEBUG_UPLOAD_BITMAP_PATH);
		
		//Spinner spinner = (Spinner) view.findViewById(R.id.eauth_user_id_spi);
		//////////////////////////////////////////////////////////////////////////////
		//TODO 添加测试用户
		userList.add("huhongyi");
		
		
		//////////////////////////////////////////////////////////////////////////////
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), 
				R.layout.drop_down_item,userList);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				userId = userList.get(position);
				Log.i(TAG, userId);
				myApp = (MyApplication)getActivity().getApplication(); // 共享信息类创建
				myApp.setUserId(userId);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getActivity(), "No user choosed!", Toast.LENGTH_LONG).show();
			}
		});*/
		//spinner.setAdapter(adapter);
		
		RadioGroup group = (RadioGroup) view.findViewById(R.id.eauth_randommode_rg);
		group.check(R.id.eauth_randommode_0);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.eauth_randommode_0){
					activeRandomMode=0;
					Log.i(TAG, "Set Random Mode 0.");
				}
				if(checkedId == R.id.eauth_randommode_1){
					activeRandomMode=1;
					Log.i(TAG, "Set Random Mode 1.");
				}
				if(checkedId == R.id.eauth_randommode_2){
					activeRandomMode=2;
					Log.i(TAG, "Set Random Mode 2.");
				}
			}
		});
		
		return view;
	}

	
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(isSelectedPic==true){
//				myApp = (MyApplication)getActivity().getApplication(); // 共享信息类创建
				myApp = new MyApplication();
				myApp.setIs_flag(false);
				myApp.setIs_real(true);
				EAuthRequest mEAuthRequest = new EAuthRequest();
				//设置uri
				myApp.setBitmapUri(bitmapUri);
				
				mEAuthRequest.setOpenSound(false);
				mEAuthRequest.setShowCountDown(true);
				if(activeRandomMode==0){//没有动作
					mEAuthRequest.setAddActive(false);
				}else if(activeRandomMode==1){//张嘴动作
					mEAuthRequest.setAddActive(true);
					mEAuthRequest.setMouseActive(true);
				}else if(activeRandomMode==2){//张嘴，摇头随机动作
					mEAuthRequest.setAddActive(true);
					int index=(new Random()).nextInt(2);//取 0 和 1随机数
					if(index==0){
						mEAuthRequest.setMouseActive(true);
					}else{
						mEAuthRequest.setHeadActive(true);
					}
				}
				EAuthApi.getInstance()
						.startEAuthSDKForResult(MainFragment.this,
						mEAuthRequest, REQUEST_CODE);
			}else{
				translate();
			}
		}	
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.i(TAG, "requestCode:=" + requestCode + ", resultCode:=" + resultCode);
		
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				EAuthResponse eAuthResponse =(EAuthResponse) data.getParcelableExtra(EAuthApi.EAUTH_RESPONSE_PARCELABLE);
				
				EAuthResponseType type = eAuthResponse.getType();
				EAuthFaceInfo faceInfo=eAuthResponse.getFaceInfo();
				Intent intent = null;
				int msg = R.string.eauth_code_success;
				switch(type){
					case SUCCESS:
						msg = R.string.eauth_code_success;
						intent = new Intent();
						intent.setClass(getActivity(), ProgressBarActivity.class);
						intent.putExtra("data", faceInfo);
						getActivity().startActivity(intent);
						break;
					case DETECT_TIMEOUT:
						msg = R.string.eauth_code_live_timeout;
						intent = new Intent();
						intent.setClass(getActivity(), BackTimeOutActivity.class);
						Bundle mBundle = new Bundle();
						mBundle.putString("time", getActivity().getString(R.string.eauth_detect_timeout));
						intent.putExtras(mBundle);
						getActivity().startActivity(intent); 
						break;
					case NON_CONTINUITY_ATTACK:
						intent = new Intent();
						intent.setClass(getActivity(), BackErrorActivity.class);
						mBundle = new Bundle();
						mBundle.putString("type", "非连续性攻击");
						intent.putExtras(mBundle);
						getActivity().startActivity(intent);
						break;
					case SELF_QUIT:
						msg = R.string.eauth_code_quit_sdk;
						break;
				}
			}
		}
		
		 if (resultCode != Activity.RESULT_OK) {
	            Log.e("TAG->onresult", "ActivityResult resultCode error");
	            return;
	        }
	        if (requestCode == IMAGE_CODE) {
	            Uri originalUri = data.getData();  //获得图片的uri
				bitmapUri = originalUri;
				if(bitmapUri!=null){
					isSelectedPic = true;
				}
	        }
	}
	
	private void selectPic(){
		boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent  getAlbum;
        if (isKitKatO) {
            getAlbum = new Intent(Intent.ACTION_PICK);
        } else {
            getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        }
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, IMAGE_CODE);
	}

	// 左右平移动画
    public void translate() {
       ObjectAnimator animator = ObjectAnimator.ofFloat(_noticeText, "translationX",0, 100);
       animator.setDuration(70);
        animator.setRepeatCount(3); // 重复3次
        animator.setRepeatMode(ObjectAnimator. REVERSE); // 重复的模式
       animator.start();
    }

	//API23及以上手动添加所需权限
	private void checkPermission() {
		String permission1 = "android.permission.CAMERA";
		String permission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
		String permission3 = "android.permission.READ_EXTERNAL_STORAGE";
		String permission4 = "android.permission.WRITE_SETTINGS";
		String[] permissionArray = {permission1, permission2, permission3,permission4};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(permissionArray, 1234);
		}
	}
    
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
 
}
