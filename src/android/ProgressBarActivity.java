package com.pingan.eauthsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.pingan.eauthsdk.api.EAuthFaceInfo;
import com.pingan.eauthsdk.api.PafaceConfig;
import com.pingan.eauthsdk.util.BitmapUtil;
import com.pingan.eauthsdk.util.MyApplication;
import com.pingan.eauthsdk.util.NetImageUrlTrans;
import com.pingan.eauthsdk.util.SaveBitmapUtil;
import com.pingan.paeauth.config.Path;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * NET SDK DEMO 
 * 人脸比对进度界面
 * @author Administrator
 *
 */
public class ProgressBarActivity extends Activity {

	private byte[] img1;
	private byte[] img2;

	private boolean _is_IDCard = false;
	private boolean _is_needed_demark = false;


	private Bitmap smallBitmap = null;
	private Bitmap icon2 = null;

	private MyApplication myApp = null;
	private String url;

	//private String userId = null;

	private MyTask task;

	private Map<String,Object> map;

	EAuthFaceInfo faceInfo;

	//private String key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("ProgressBarActivity", "进入ProgressBarActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eauth_activity_progressbar);
		EAuthFaceInfo faceInfo=(EAuthFaceInfo) getIntent().getParcelableExtra("data");
		smallBitmap=faceInfo.getBitmapImage();
		// 拍照的照片
		img1 = bitmapToBytes(smallBitmap);

		//设置是否需要做去网纹处理
		_is_IDCard = false;
		_is_needed_demark = false;

		// faceInfo = faceInfo;
		map = new HashMap<String, Object>();
		map.put("rect_x",faceInfo.getRect_x());
		map.put("rect_y",faceInfo.getRect_y());
		map.put("rect_w",faceInfo.getRect_w());
		map.put("rect_h",faceInfo.getRect_h());
		map.put("yaw",faceInfo.getYaw());
		map.put("pitch",faceInfo.getPitch());
		map.put("rotate",faceInfo.getRotate());
		map.put("brightness",faceInfo.getBrightness());
		map.put("motion_blurness",faceInfo.getMotion_blurness());
		map.put("eye_hwratio",faceInfo.getEye_hwratio());
		map.put("mouth_motion",faceInfo.getMouth_motion());
		map.put("head_motion",faceInfo.getHead_motion());


		myApp = new MyApplication(); // 共享信息类创建
		//userId = myApp.getUserId();
		//TODO  获得本地图片
		/////////////////////////////////TODO//////////////////////////////////////
		/*if ("huhongyi".equalsIgnoreCase(userId)) {
			icon2 = BitmapFactory.decodeResource(getResources(), R.drawable.huhongyi);
		//ADD HERE 
		} else {
			icon2 = BitmapFactory.decodeResource(getResources(), R.drawable.face01);
		}*/
		////////////////////////////////////////////////////////////////////////	
		Context context = this;
		task = new MyTask();
		task.execute(context);
	}

	// ***发送图片给服务器1
	private String urlConnectionSupportedIDCard(byte[] img1, byte[] img2, boolean is_IDCard, boolean is_needed_demark)
			throws IOException {
		Log.i("ProgressBarActivity", "准备上传");
		// 得到图片的base64编码

		String imgbase64 = Base64.encodeToString(img1, Base64.DEFAULT);
		String bdbase64 = Base64.encodeToString(img2, Base64.DEFAULT);

		String string = "";
		try{
			JSONObject map1json = new JSONObject();
			map1json.put("sdk", "o236");
			map1json.put("type", "android");
			map1json.put("system", "android4.4");
			map1json.put("user_agent", "Mozilla");

			// 拍照的照片
			JSONObject map2json = new JSONObject();
			map2json.put("category", "1");
			map2json.put("mark", "0");
			map2json.put("content_type", "jpg");
			map2json.put("data", imgbase64);

			JSONObject map3json = new JSONObject();
			map3json.put("category", "2");
			map3json.put("mark", "0");
			map3json.put("content_type", "jpg");
			map3json.put("data", bdbase64);
			if (is_IDCard) {
				map3json.put("is_IDCard", "1");
			} else {
				map3json.put("is_IDCard", "0");
			}
			if (is_needed_demark) {
				map3json.put("is_mark", "1");
			} else {
				map3json.put("is_mark", "0");
			}

			JSONObject map = new JSONObject();
			map.put("app_id", "1234123412");
			map.put("terminal", map1json);
			map.put("person_id", "AccountID");
			map.put("image1", map3json);
			map.put("image2", map2json);
			string = map.toString();
		}catch(Exception e){
			e.printStackTrace();
		}

		// original
		URL url = new URL("http://114.141.178.39:50480/bioauth/apiByAndroid/facedemo/compare");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(false);
		// Set the content type to urlencoded,
		connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		connection.setRequestProperty("Content-Length", String.valueOf(string.length()));
		connection.setRequestProperty("Content-Type", "application/json");
		connection.connect();

		//Long sendTime = System.currentTimeMillis();

		DataOutputStream out = new DataOutputStream(connection.getOutputStream());

		out.writeBytes(string);
		out.flush();
		out.close(); // flush and close
		// Get Session ID
		DataInputStream in = new DataInputStream(connection.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String result = "";
		String line = br.readLine();
		while (line != null) {
			result += line;
			line = br.readLine();
		}
		if (result != null) {
			try {
				JSONTokener jsonParser = new JSONTokener(result);
				JSONObject jsonstring = (JSONObject) jsonParser.nextValue();
				Log.d("JSON", ""+jsonstring.toString());
				if (jsonstring.getString("is_alive").toString().equals("活体")) {
					if (jsonstring.getString("errormsg").toString().equals("根据阀值，人脸验证成功，是同一人！")) {
						Log.i("errormsg", jsonstring.getString("errormsg").toString());
						// ***比对成功，返回“是活体，验证通过”
						Log.i("result", jsonstring.getString("errormsg").toString());
						return "0";
					} else if (jsonstring.getString("errormsg").toString().equals("根据阀值，人脸验证失败，不是同一人请重新上传！")) {
						// ***比对失败，返回“是活体，不是同一个人，验证不通过”
						Log.i("errormsg", jsonstring.getString("errormsg").toString() + "__");
						Log.i("result", jsonstring.getString("errormsg").toString());
						return "1";
					} else {
						// failed....
						Log.i("errormsg", jsonstring.getString("errormsg").toString() + "__");
						Log.i("result", jsonstring.getString("errormsg").toString());
						return "4";
					}

				} else {
					// ***比对失败，返回“非活体，验证不通过”
					in.close();
					Log.i("result", jsonstring.getString("similarity"));

					if (jsonstring.getString("errormsg").toString().equals("根据阀值，人脸验证成功，是同一人！")) {
						Log.i("errormsg", jsonstring.getString("errormsg").toString());
						// ***比对成功，返回“是活体，验证通过”
						Log.i("result", jsonstring.getString("errormsg").toString());
						return "2";
					} else if (jsonstring.getString("errormsg").toString().equals("根据阀值，人脸验证失败，不是同一人请重新上传！")) {
						// ***比对失败，返回“是活体，不是同一个人，验证不通过”
						Log.i("errormsg", jsonstring.getString("errormsg").toString() + "__");
						Log.i("result", jsonstring.getString("errormsg").toString());
						return "3";
					}else{
						// failed....
						Log.i("errormsg", jsonstring.getString("errormsg").toString() + "__");
						Log.i("result", jsonstring.getString("errormsg").toString());
						return "4";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}finally{
				if(in!=null){
					in.close();
				}
			}
		}
		if(in!=null){
			in.close();
		}
		// ***比对失败，返回“非法请求”
		Log.i("result", string);
		return "9";
		//return  result;
	}

	// bitmap转换成二进制数组
	public static byte[] bitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	class MyTask extends AsyncTask<Context, Void, String> {

		String uploading_result;

		@Override
		protected String doInBackground(Context... params) {
			try {
//				url = myApp.getBitmapUrl();
//				if(url != null){
//
//					Drawable drawable = NetImageUrlTrans.getDrawable(url);
//					BitmapDrawable bd = (BitmapDrawable) drawable;
//					Bitmap bm = bd.getBitmap();
//					float width =  BitmapUtil.dip2px(ProgressBarActivity.this,120f);
//					float heigh = BitmapUtil.dip2px(ProgressBarActivity.this, 140f);
//					icon2 = BitmapUtil.compressLimitDecodeCompress(bm, 50, width, heigh, 50);
//					img2 = bitmapToBytes(icon2);
//				}
				/*Uri bitmapUri =  myApp.getBitmapUri();
				if(bitmapUri!=null){
					Bitmap bitmap =  decodeUriAsBitmap(bitmapUri);
					float width =  BitmapUtil.dip2px(ProgressBarActivity.this,120f);
					float heigh = BitmapUtil.dip2px(ProgressBarActivity.this, 140f);
					icon2 = BitmapUtil.compressLimitDecodeCompress(bitmap, 50, width, heigh, 50);
					bitmap.recycle();
				}else{
					icon2 = BitmapFactory.decodeResource(getResources(), R.drawable.huhongyi);
				}*/


				//debug模式保存图像到sdcard
				if (PafaceConfig.sDebugBitmap){
					String namePrefix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
					SaveBitmapUtil.saveBitmap2Sdcard(namePrefix + "_a", icon2);
					SaveBitmapUtil.saveBitmap2Sdcard(namePrefix + "_b", smallBitmap);
				}


				//uploading_result = urlConnectionSupportedIDCard(img1, img2, _is_IDCard,_is_needed_demark);



			} catch (Exception e) {
				e.printStackTrace();
			}

//			Intent intent = new Intent();
//			intent.putExtra("result",uploading_result);
//			setResult(1000,intent);
//			finish();

			//return uploading_result;
			return "999";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("ProgressBarActivity", "8");
			//Intent intent = new Intent(ProgressBarActivity.this, Jump.class);
			String namePrefix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
			SaveBitmapUtil.saveBitmap2Sdcard(namePrefix + "_b", smallBitmap);
			String filePath = Path.DEBUG_UPLOAD_BITMAP_PATH+namePrefix + "_b";


			Log.i("ProgressBar result", result==null?"":result);
			Intent intent = new Intent();
			//intent.putExtra("img1", img1);
			//intent.putExtra("img2", img2);
			//intent.putExtra("result", result);
			//intent.putExtra("imgurl",filePath);
			map.put("imgurl",filePath);
			intent.putExtra("faceInfo",new JSONObject(map).toString());

			if (result != null) {
				//startActivity(intent);
				setResult(1000,intent);
				ProgressBarActivity.this.finish();
			} else {
				Toast.makeText(getApplicationContext(), "000很抱歉,网络异常!", Toast.LENGTH_LONG).show();
				finish();
			}

		}

	}
	//通过uri获取bitmap
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (task != null){
			task.cancel(true);
		}

	}

	/*public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					Drawable drawable = (Drawable) msg.obj;
					BitmapDrawable bd = (BitmapDrawable) drawable;
					Bitmap bm = bd.getBitmap();
					float width =  BitmapUtil.dip2px(ProgressBarActivity.this,120f);
					float heigh = BitmapUtil.dip2px(ProgressBarActivity.this, 140f);
					icon2 = BitmapUtil.compressLimitDecodeCompress(bm, 50, width, heigh, 50);
					img2 = bitmapToBytes(icon2);
					break;
			}
			super.handleMessage(msg);
		}
	};*/
}
