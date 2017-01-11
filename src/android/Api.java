package com.pingan.eauthsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.pingan.eauthsdk.api.EAuthApi;
import com.pingan.eauthsdk.api.EAuthFaceInfo;
import com.pingan.eauthsdk.api.EAuthRequest;
import com.pingan.eauthsdk.api.EAuthResponse;
import com.pingan.eauthsdk.api.EAuthResponseType;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by shihujiang on 16/12/30.
 */

public class Api extends CordovaPlugin {

    private Activity activity;

    public CallbackContext callbackContext;

    private final static int REQUEST_CODE = 0x05;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
        checkPermission();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        Log.d("test",args.get(0).toString());

        if (action.equals("coolMethod")){
            Log.d("test start call","start call");
            EAuthRequest mEAuthRequest = new EAuthRequest();
            mEAuthRequest.setOpenSound(false);
            mEAuthRequest.setShowCountDown(true);
            mEAuthRequest.setAddActive(false);

            cordova.setActivityResultCallback(this);
            EAuthApi.getInstance()
                    .startEAuthSDKForResult(this.cordova.getActivity(),mEAuthRequest, REQUEST_CODE,"");

//            Intent var5;
//            (var5 = new Intent()).setClassName(this.cordova.getActivity(), EAuthActivity.class.getName());
//            var5.putExtra("EauthRequestParcelable", mEAuthRequest);
//            this.cordova.startActivityForResult(this,var5, REQUEST_CODE);


            //Intent intent = new Intent(this.cordova.getActivity(), com.pingan.eauthsdk.component.MainActivity.class);
            //this.cordova.startActivityForResult(this,intent,0);

            Log.d("test end call",this.getUuid());
            //callbackContext.success(this.getUuid()+",tigerj");
        }
        else{
            callbackContext.error("tigerj error.");
        }
        return true;
    }

    public String getUuid() {
        String uuid = Settings.Secure.getString(this.cordova.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        return uuid;
    }

//    public static void  call(String rnt){
//        callbackContext.success(rnt);
////    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        Log.i("test--resultCode:",resultCode+"");
//        Log.i("test--intent:",intent.getStringExtra("aaa"));
//        callbackContext.success(intent.getStringExtra("aaa"));
//
//    }

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.i("api", "requestCode:=" + requestCode + ", resultCode:=" + resultCode);

            if (requestCode == 0 && resultCode == 1000){
                Log.d("api",String.format("image url:%s",data.getStringExtra("imgurl")));
                Log.d("api",String.format("faceInfo:%s",data.getStringExtra("faceInfo")));
                callbackContext.success(data.getStringExtra("faceInfo"));
            }

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
                        Log.d("api",msg+"");
                        intent = new Intent();
                        intent.setClass(this.cordova.getActivity(), ProgressBarActivity.class);
                        intent.putExtra("data", faceInfo);
                        //this.cordova.getActivity().startActivity(intent);
                        this.cordova.startActivityForResult(this,intent,0);
                        break;
                    case DETECT_TIMEOUT:
                        msg = R.string.eauth_code_live_timeout;
                        Log.d("api",msg+"");
                        intent = new Intent();
                        intent.setClass(this.cordova.getActivity(), BackTimeOutActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("time", this.cordova.getActivity().getString(R.string.eauth_detect_timeout));
                        intent.putExtras(mBundle);
                        this.cordova.getActivity().startActivity(intent);
                        break;
                    case NON_CONTINUITY_ATTACK:
                        Log.d("api","NON_CONTINUITY_ATTACK");
                        intent = new Intent();
                        intent.setClass(this.cordova.getActivity(), BackErrorActivity.class);
                        mBundle = new Bundle();
                        mBundle.putString("type", "非连续性攻击");
                        intent.putExtras(mBundle);
                        this.cordova.getActivity().startActivity(intent);
                        break;
                    case SELF_QUIT:
                        msg = R.string.eauth_code_quit_sdk;
                        Log.d("api",msg+"");
                        break;
                }
            }
        }

        if (resultCode != Activity.RESULT_OK) {
            Log.e("TAG->onresult", "ActivityResult resultCode error");
            return;
        }


    }

    //API23及以上手动添加所需权限
    private void checkPermission() {
        String permission1 = "android.permission.CAMERA";
        String permission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        String permission3 = "android.permission.READ_EXTERNAL_STORAGE";
        String permission4 = "android.permission.WRITE_SETTINGS";
        String[] permissionArray = {permission1, permission2, permission3,permission4};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.cordova.getActivity().requestPermissions(permissionArray, 1234);
        }
    }

    }
