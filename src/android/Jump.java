package com.pingan.eauthsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pingan.eauthsdk.api.EAuthApi;
import com.pingan.eauthsdk.api.EAuthRequest;
import com.pingan.eauthsdk.component.MainActivity;

import org.apache.cordova.CallbackContext;

/**
 * Created by V-dongxuchai on 2017/1/2.
 */

public class Jump extends Activity {

    private final static int REQUEST_CODE = 0x05;

    private CallbackContext callbackContext;

    private  Activity activity;
    private String totalResult;

    public Jump(Activity activity){
        this.activity =activity;
    }

    public Jump(){

    }

    public Jump(Activity activity,CallbackContext callbackContext){
        this.activity = activity;
        this.callbackContext = callbackContext;
    }

    public  void call(){
        Log.d("test","0000");

        EAuthRequest mEAuthRequest = new EAuthRequest();
        mEAuthRequest.setOpenSound(false);
        mEAuthRequest.setShowCountDown(true);
        mEAuthRequest.setAddActive(false);
        Log.d("test","here");
        Intent intent = new Intent();
        intent.setClass(activity, MainActivity.class);
        activity.startActivityForResult(intent,REQUEST_CODE);
        //EAuthApi.getInstance().startEAuthSDKForResult(activity,mEAuthRequest, REQUEST_CODE,"");
        Log.d("test","1111");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        if(result != null && !result.equals("")){
                if(result.equals("0")){
                    //tv_result.setText("是活体，验证通过");
                   totalResult = "活体检测：活  体 \n 人脸比对：本  人 \n 检测结果：通  过 ";
                }

                if(result.equals("1")){
                    //tv_result.setText("是活体，验证不通过");
                    totalResult = "活体检测：活  体 \n 人脸比对：非本人\n 检测结果：不通过";
                }

                if(result.equals("2")){
                    //tv_result.setText("非活体，验证不通过");
                    totalResult = "活体检测：非活体\n 人脸比对：本人\n 检测结果：不通过";
                }

                if(result.equals("3")){
                    //tv_result.setText("非活体，验证不通过");
                    totalResult = "活体检测：非活体\n 人脸比对：非本人\n 检测结果：不通过";
                }

                if(result.equals("4")||result.equals("9")){
                    totalResult = "非法请求";
            }
            callbackContext.success(totalResult);
            Log.e("JUMP",totalResult);
        }
        callbackContext.error("error ----");

//        setContentView(R.layout.jump_test);
//        call();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("test requestCode:",requestCode+"");
        Log.d("test resultCode:",resultCode+"");
        Log.d("test data:",data+"");
    }
}
