package com.pingan.eauthsdk.component;

import com.pingan.eauthsdk.api.EAuthApi;
import com.pingan.eauthsdk.api.EAuthRequest;
import com.pingan.eauthsdk.api.EAuthResponseType;
import com.pingan.eauthsdk.ctrl.CameraCtrl;
import com.pingan.eauthsdk.util.GetResourceUtil;
import com.pingan.eauthsdk.util.ScreenUtil;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * EAuth SDK Main Fragment
 * 包含类实例：
 * 	LiveDetector
 * 	CameraCtrl
 * 	EAuthRequest
 * 
 * @author Administrator
 *
 */
public class EAuthFragment extends Fragment implements Callback {

	private final static String TAG = "EAuthFragment";
	
	//界面元素
	private SurfaceView mSurfaceView=null;
	private SurfaceHolder mSurfaceHolder=null;
	private TextView camera_Info_tv = null;

	//控制参数
	private LiveDetector mLiveDetector=null;
	private CameraCtrl mCameraCtrl=null;
	private EAuthRequest eAuthRequest=null;

	/**
	 * Fragment 创建
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		if (getArguments() != null) {
			eAuthRequest = (EAuthRequest)this. getArguments().getParcelable(EAuthApi.EAUTH_REQUEST_PARCELABLE);
		}else{
			eAuthRequest=new EAuthRequest();
		}
		if (eAuthRequest == null){
			eAuthRequest=new EAuthRequest();
		}

		// ***4月19日21：03将其放入onresume
		mLiveDetector = new LiveDetector(getActivity(),eAuthRequest);
		mCameraCtrl = new CameraCtrl();
		
		//手机成像倒置问题
		if (eAuthRequest.getUpsideDown()) {
			mCameraCtrl.setIsUpsideDown(true);
			mLiveDetector.setUpsideDown(true);
		}
		
		//后置摄像头
		if (eAuthRequest.getUseBackCamera()) {
			mCameraCtrl.setBackCamera(true);
			mLiveDetector.setUpsideDown(true);
		}
	}
	

	/*/*
	 * Fragment 显示
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View _view = inflater.inflate(GetResourceUtil.getResourceByType(getActivity(),"layout", "eauth_fragment_eauth"), container, false);
		//Surface
		mSurfaceView = (SurfaceView) _view
			.findViewById(GetResourceUtil.getResourceByType(getActivity(),"id", "eauth_camera_view"));

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		//摄像头信息
		camera_Info_tv = (TextView) _view.findViewById(GetResourceUtil.getResourceByType(getActivity(),"id", "eauth_camera_info"));
		mLiveDetector.initView(_view);

		//返回按钮
		
		ImageView mBackImageView = (ImageView) _view.findViewById(GetResourceUtil.getResourceByType(getActivity(),"id","eauth_imageview_back"));
		
		mBackImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == GetResourceUtil.getResourceByType(getActivity(),"id","eauth_imageview_back")) {
					// 强行退出sdk
					Toast.makeText(getActivity(), "您已退出验证", Toast.LENGTH_LONG).show();
					mLiveDetector.finishActivetyToMain(EAuthResponseType.SELF_QUIT, null);
				}
			}
		});
		return _view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
		// 强行退出sdk
		mLiveDetector.finishActivetyToMain(EAuthResponseType.SELF_QUIT, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
		if (mLiveDetector == null) {
			mLiveDetector = new LiveDetector(getActivity(),eAuthRequest);
		}
		Camera result_Camera = mCameraCtrl.openCamera();
		if (result_Camera == null) {
			//TODO 加上摄像头开启失败的返回码
			camera_Info_tv.setText("没有开启摄像头权限!");
		} else {
			setSurfaceSize();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void setSurfaceSize() {
		Camera.Size bestPreviewSize = mCameraCtrl.getOptimalPreviewSize();
		ViewGroup.LayoutParams lyP = mSurfaceView.getLayoutParams();
		lyP.width = ScreenUtil.getScreenSize(EAuthFragment.this.getActivity())[0];
		lyP.height = bestPreviewSize.width
				* lyP.width / bestPreviewSize.height;
	}

	
	//////////////////////////////Surface CallBack 事件 ////////////////////////////////////////////
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged, width:" + width + ", height:" + height);
		mCameraCtrl.setParameters();
		mCameraCtrl.startPreview(new PreviewCallback(){
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				Log.i(TAG, "--------Fragment onPreviewFrame------------");
				//这里什么也不做，交由LiveDetector解决
				mLiveDetector.doFrame(data, camera);
			}
		});
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		mCameraCtrl.setPreviewDisplay(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		mCameraCtrl.closeCamera();
	}

}
