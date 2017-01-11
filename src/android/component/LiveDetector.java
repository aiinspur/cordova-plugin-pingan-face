package com.pingan.eauthsdk.component;

import com.pingan.eauthsdk.api.EAuthApi;
import com.pingan.eauthsdk.api.EAuthFaceInfo;
import com.pingan.eauthsdk.api.EAuthRequest;
import com.pingan.eauthsdk.api.EAuthResponse;
import com.pingan.eauthsdk.api.EAuthResponseType;
import com.pingan.eauthsdk.util.BitmapUtil;
import com.pingan.eauthsdk.util.CopyFileFromAssets;
import com.pingan.eauthsdk.util.GetResourceUtil;
import com.pingan.eauthsdk.util.SysUtil;
import com.pingan.paeauth.CallBackDetect;
import com.pingan.paeauth.DetectorParams;
import com.pingan.paeauth.PALivenessDetector;
import com.pingan.paeauth.UserTip;
import com.pingan.paeauth.bean.PALivenessDetectionFrame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * PALivenessDetector的控制类 1. 控制PALivenessDetector的初始化，处理图片 和销毁动作。 2.
 * 继承了CallBackDetect类，BackUserTips 对SDK返回消息进行处理。 3.
 * 继承了CallBackDetect类，onDetectionResult 对SDK返回结果进行处理。
 * 
 * @author Administrator
 *
 */
@SuppressLint("ShowToast")
public class LiveDetector extends CallBackDetect {

	private final static String TAG = "LiveDetector";

	private final static int TIME_DELAY = 2;

	private long mTimeBegin = 0; // 标记摄像头启动的时间

	// private Context mContext;
	private Activity mActivity;
	private EAuthRequest mRequest;
	private PALivenessDetector mDetector = null;
	private static boolean init_flag = false;

	// 全局参数
	private PALivenessDetectionFrame mDetectionFrame = new PALivenessDetectionFrame();

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param request
	 */
	public LiveDetector(Activity activity, EAuthRequest request) {
		mTimeBegin = System.currentTimeMillis();
		mActivity = activity;
		mRequest = request;

		if (mDetector == null) {
			// 初始化Core类, 进行调用doDetection前的类初始化
			String savePath = mActivity.getApplicationContext().getFilesDir().getAbsolutePath();
			String assetPath = savePath + "/haarcascade_frontalface_alt2.xml";
			if (CopyFileFromAssets.fileIsExists(assetPath)) {
				Log.i(TAG, "模型文件已经拷贝");
			} else {
				// 拷贝model到SD卡存储
				CopyFileFromAssets.copyAllModel(mActivity, savePath);
				Log.i(TAG, "模型文件拷贝完成");
			}
			DetectorParams params = new DetectorParams();
			params.setPALivenessDetectorStepTimeLimit(10);
			params.setPALivenessDetectorModelPath(savePath);
			// TODO change active mode
			if (null!= request && !request.isAddActive()) {
				params.setPALivenessDetectorActiveRandomMode(0);
			} else if (null!= request && request.isAddActive() && request.isMouseActive()) {
				params.setPALivenessDetectorActiveRandomMode(1);
			} else {
				params.setPALivenessDetectorActiveRandomMode(2);
			}
			mDetector = PALivenessDetector.getInstance(params);
			mDetector.setOutTime(mRequest.getOutTime());
			mDetector.setShowLTRBWarn(mRequest.getShowLTRBWarn());
		}
		if (!init_flag) {
			init_flag = true;
			mDetector.init(this);
		}
		mDetector.reset(this);
	}
	// TODO remove this method
	public void setUpsideDown(boolean is_upsideDown) {
		this.mDetector.setIsUpsideDown(is_upsideDown);
	}

	private TextView mAnimationText;
	private TextView mAnimationText1;

	private TextView mFaceTextOne;
	private ImageView miImageView_anim;
	private AnimationDrawable animation;

	private ImageView miImageView_anim1;
	private AnimationDrawable animation1;

	private ImageView imageView_Timer;
	private RotateAnimation rotateAnimation_Timer;
	private TextView textView_Timer;
	private RelativeLayout relativeLayout_Timer;
	
	public void initView(final View view) {

		relativeLayout_Timer = (RelativeLayout) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "area_Tiemr"));

		miImageView_anim = (ImageView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "eauth_Animation_igv"));
		miImageView_anim.setBackgroundResource(GetResourceUtil.getResourceByType(mActivity,"drawable", "eauth_frame_mouth_open"));
		animation = (AnimationDrawable) miImageView_anim.getBackground();
		animation.setOneShot(false);
		animation.start();

		miImageView_anim1 = (ImageView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "eauth_Animation_igv1"));
		miImageView_anim1.setBackgroundResource(GetResourceUtil.getResourceByType(mActivity,"drawable", "eauth_frame_head_yaw"));
		animation1 = (AnimationDrawable) miImageView_anim1.getBackground();
		animation1.setOneShot(false);
		animation1.start();

		// ***4月24日加入新的计时器
		textView_Timer = (TextView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "textView_Timer"));
		imageView_Timer = (ImageView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "timer_Animation_igv"));
		int pivotType = Animation.RELATIVE_TO_SELF;
		rotateAnimation_Timer = new RotateAnimation(0f, 360f, pivotType, .5f, pivotType, .5f);
		rotateAnimation_Timer.setDuration(1000);
		rotateAnimation_Timer.setInterpolator(new LinearInterpolator());
		rotateAnimation_Timer.setRepeatCount(Animation.INFINITE);
		rotateAnimation_Timer.setRepeatMode(Animation.RESTART);
		imageView_Timer.setAnimation(rotateAnimation_Timer);
		rotateAnimation_Timer.start();

		mAnimationText = (TextView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "eauth_Animation_text"));
		mAnimationText1 = (TextView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "eauth_Animation_text1"));
		mFaceTextOne = (TextView) view.findViewById(GetResourceUtil.getResourceByType(mActivity,"id", "eauth_face_text_one"));
		mFaceTextOne.setVisibility(View.VISIBLE);

	}

	private static int WIDTH_w = 480; // width 参数
	private static int HEIGHT_h = 640;// height 参数
	private static float RATIO = 1.6f;

	/**
	 * 处理图像数据
	 * 
	 * @param data
	 * @param camera
	 */
	public void doFrame(final byte[] data, final Camera camera) {
		if (System.currentTimeMillis() - mTimeBegin >= TIME_DELAY * 1000) {
			mDetector.detectWithImage(data, WIDTH_w, HEIGHT_h);
		}
	}

	/**
	 * handler机制
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int tip_id = msg.what;
			UserTip tip = UserTip.getUserTipByID(tip_id);
			Log.i(TAG, tip_id + ":" + tip.getDescription());
			// 计时tip
			if (tip_id > 0 && tip_id <= mRequest.getOutTime()) {
				textView_Timer.setText(tip.getDescription());
				// 采集提醒tip
			} else if (tip_id > 100 && tip_id < 120) {
				mFaceTextOne.setText(tip.getDescription());
			} else {
				switch (tip) {
				case DETECTION_SUCCESS:
					Log.i(TAG, "DetectSuccess");
					imageView_Timer.setVisibility(View.INVISIBLE);
					mAnimationText.setVisibility(View.INVISIBLE);
					finishActivetyToMain(EAuthResponseType.SUCCESS, mDetectionFrame);
					break;
				case DETECTION_FAILED_DISCONTINUITYATTACK:
					imageView_Timer.setVisibility(View.INVISIBLE);
					mAnimationText.setVisibility(View.INVISIBLE);
					finishActivetyToMain(EAuthResponseType.NON_CONTINUITY_ATTACK, null);
					break;
				case DETECTION_TIMEOUT:
					imageView_Timer.setVisibility(View.INVISIBLE);
					mAnimationText.setVisibility(View.INVISIBLE);
					finishActivetyToMain(EAuthResponseType.DETECT_TIMEOUT, null);
					break;
				case DECTECT_START: // Show Tip
					relativeLayout_Timer.setVisibility(View.VISIBLE);
					break;
				case DECTECT_MOUTH_START: // Show Tip
					mFaceTextOne.setVisibility(View.INVISIBLE);
					miImageView_anim.setVisibility(View.VISIBLE);
					mAnimationText.setVisibility(View.VISIBLE);
					break;
				case DECTECT_HEAD_START: // Show Tip
					mFaceTextOne.setVisibility(View.INVISIBLE);
					miImageView_anim1.setVisibility(View.VISIBLE);
					mAnimationText1.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
		}
	};

	public void finishActivetyToMain(EAuthResponseType type, PALivenessDetectionFrame frame) {
		mDetector.release();
		Intent intent = new Intent(mActivity,EAuthActivity.class);
		EAuthResponse mEAuthResponse = new EAuthResponse();
		mEAuthResponse.setType(type);
		if (frame != null) {
			EAuthFaceInfo faceInfo = new EAuthFaceInfo();
			faceInfo.setRect_x(frame.getRect_x());
			faceInfo.setRect_y(frame.getRect_y());
			faceInfo.setRect_w(frame.getRect_width());
			faceInfo.setRect_h(frame.getRect_height());
			faceInfo.setYaw(frame.getYaw());
			faceInfo.setPitch(frame.getPitch());
			faceInfo.setRotate(frame.getRotate());
			faceInfo.setBrightness(frame.getBrightness());
			faceInfo.setMotion_blurness(frame.getMotionblurness());
			faceInfo.setHead_motion(frame.getHead_motion());
			faceInfo.setMouth_motion(frame.getMouth_motion());
			faceInfo.setEye_hwratio(frame.getEye_hwratio());
			faceInfo.setBitmapImage(getBitmapImage(frame));
			mEAuthResponse.setFaceInfo(faceInfo);
		}
		intent.putExtra(EAuthApi.EAUTH_RESPONSE_PARCELABLE, mEAuthResponse);
		mActivity.setResult(Activity.RESULT_OK, intent);
		mActivity.finish();
	}

	private Bitmap getBitmapImage(PALivenessDetectionFrame frame) {
		PALivenessDetectionFrame mData = frame;
		// 获得成功的图片
		try {
			int x = mData.getRect_x();
			int y = mData.getRect_y();
			int w = mData.getRect_width();
			int h = mData.getRect_height();
			RectF rect = null;
			if (x >= 0 && y >= 0 && w >= 0 && h >= 0) {
				rect = new RectF(x, y, w, h);
				byte[] data = mData.getYuvData();
				if (data.length != 0 && data != null) {
					Bitmap mBitmap = BitmapUtil.createBitmapFromYUVByte(data, HEIGHT_h, WIDTH_w, true, 90);
					String UI_prop = BitmapUtil.getSystemProperty("ro.miui.ui.version.name");
					if (UI_prop != null) {
						if (UI_prop.equals("V6")) {
							mBitmap = BitmapUtil.adjustPhotoRotation(mBitmap, 180);
							rect = new RectF(WIDTH_w - (w + x), y, w, h);
						}
					}
					String[] version = SysUtil.getVersion();
					if (version != null) {
						if (version.length >= 3) {
							if (version[2].equals("ATH-TL00H")) {
								mBitmap = BitmapUtil.adjustPhotoRotation(mBitmap, 180);
								rect = new RectF(WIDTH_w - (w + x), y, w, h);
							}
						}
					}
					// 扣图扩大1.2 / 0314加float ratio / int nHeight,int nWidth,
					Bitmap smallBitmap = BitmapUtil.cropBitmap(rect, mBitmap, HEIGHT_h, WIDTH_w, RATIO);
					return smallBitmap;
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String BackUserTips(UserTip tip) {
		handler.sendEmptyMessage(tip.getId());
		return null;
	}

	@Override
	public void onDetectionSuccessResult(PALivenessDetectionFrame frame, UserTip tip) {
		if (frame != null && tip != null) {
			mDetectionFrame = frame;
			handler.sendEmptyMessage(tip.getId());
		}
	}

}
