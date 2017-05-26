package com.example.king.newimdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class LoginActivity extends Activity {

    private String user;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //doLogin();
    }

    public void doLogin() {
        LoginInfo info = new LoginInfo(user, pwd); // config.. ..
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        Log.i("结果", "成功");
                        startActivity(new Intent(LoginActivity.this, MessageActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.i("结果", "失败===" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.i("结果", "异常==" + exception.getMessage());
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }

    public void Login(View view) {
        user = ((EditText) findViewById(R.id.user)).getText().toString().toLowerCase();
        pwd = ((EditText) findViewById(R.id.pwd)).getText().toString().toLowerCase();
        doLogin();
    }
}
