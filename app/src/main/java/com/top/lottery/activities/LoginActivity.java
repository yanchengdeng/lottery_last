package com.top.lottery.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.UserInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.ll_center)
    LinearLayout llCenter;

    private int REQUEST_PHPNE_STATUTS = 0x110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        hideTittle();

        if (com.top.lottery.utils.Utils.isLogin()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            getImei();
            showContentView();
        }

        //测试 登录 账号
        etUserName.setText("10000000");
        etPassword.setText("11111111");

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etUserName.getEditableText().toString())) {
                    ToastUtils.showShort(getString(R.string.input_user_name));
                    return;
                }

                if (TextUtils.isEmpty(etPassword.getEditableText().toString())) {
                    ToastUtils.showShort(getString(R.string.input_user_pwd));
                    return;
                }

                doLoginAction();
            }
        });

    }

    private void getImei() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHPNE_STATUTS);
        } else {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String imei = TelephonyMgr.getDeviceId();
            Utils.setImei(imei);
        }
    }

    private void doLoginAction() {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", etUserName.getEditableText().toString());
        data.put("password", etPassword.getEditableText().toString());
        OkGo.<LotteryResponse<UserInfo>>post(Constants.Net.LOGIN_IN)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo>> response) {
                        UserInfo userInfo = response.body().body;
                        if (userInfo != null) {
                            SPUtils.getInstance().put(Constants.USER_INFO, new Gson().toJson(userInfo));
                            ToastUtils.showShort("登陆成功");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHPNE_STATUTS) {
            LogUtils.w("dyc", permissions);
            LogUtils.w("dyc", grantResults);
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String imei = TelephonyMgr.getDeviceId();
            Utils.setImei(imei);

        }
    }
}
