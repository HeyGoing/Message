package com.example.administrator.message;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class MainActivity extends AppCompatActivity implements OnClickListener,Callback {
    private Button btn=null;
    private Button btn1;
    private String phonenumber;
    private EditText et_number;

    private EditText et_phonenumber;
    private static String APPKEY = "";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_phonenumber = (EditText) findViewById(R.id.editText);
        et_number=(EditText)findViewById(R.id.editText2);
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        btn = (Button) findViewById(R.id.button);
        btn1 = (Button) findViewById(R.id.button2);

        btn.setOnClickListener(this);
        btn1.setOnClickListener(this);

        initSDK();
    }

    private void initSDK() {
        try {

            final Handler handler = new Handler(this);
            EventHandler eventHandler = new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            };

            SMSSDK.registerEventHandler(eventHandler); // 注册短信回调

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    protected void onDestroy() {

        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();

    }

    public boolean handleMessage(Message msg) {

        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;
        if (result == SMSSDK.RESULT_COMPLETE) {
            System.out.println("--------result"+event);
            //回调完成
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                Toast.makeText(MainActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
            }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                //获取验证码成功
                Toast.makeText(MainActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();

            }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){


                //返回支持发送验证码的国家列表
            }

        }else{

//				((Throwable) data).printStackTrace();
//				Toast.makeText(MainActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
            int status = 0;
            try {
                ((Throwable) data).printStackTrace();
                Throwable throwable = (Throwable) data;

                JSONObject object = new JSONObject(throwable.getMessage());
                String des = object.optString("detail");
                status = object.optInt("status");
                if (!TextUtils.isEmpty(des)) {
                    Toast.makeText(MainActivity.this, des, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                SMSLog.getInstance().w(e);
            }

        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                phonenumber = et_phonenumber.getText().toString().trim();
                if (!TextUtils.isEmpty(phonenumber)) {
                    SMSSDK.getVerificationCode("86", phonenumber);//获取短信
                    //SMSSDK.getVoiceVerifyCode("86", phonenumber);

                }else {
                    Toast.makeText(MainActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.button2:

                String number = et_number.getText().toString().trim();
                if (!TextUtils.isEmpty(number)) {
                    SMSSDK.submitVerificationCode("86", phonenumber,number);//验证短信

                }else {
                    Toast.makeText(MainActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            default:
                break;
        }
    }
}

