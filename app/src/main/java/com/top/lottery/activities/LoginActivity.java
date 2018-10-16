package com.top.lottery.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
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
import com.top.lottery.utils.AppManager;
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
    @BindView(R.id.iv_clear_username)
    ImageView ivClearUsername;
    @BindView(R.id.iv_clear_password)
    ImageView ivClearPassword;

    private int REQUEST_PHPNE_STATUTS = 0x110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        hideTittle();

        if (Utils.isLogin()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            getImei();
            showContentView();
        }

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.USER_NAME))) {
            etUserName.setText(SPUtils.getInstance().getString(Constants.USER_NAME));
        }
        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.PASSWORD))) {
            etPassword.setText(SPUtils.getInstance().getString(Constants.PASSWORD));
        }

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    ivClearUsername.setVisibility(View.GONE);
                }else{
                    ivClearUsername.setVisibility(View.VISIBLE);
                }

            }
        });



        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    ivClearPassword.setVisibility(View.GONE);
                }else{
                    ivClearPassword.setVisibility(View.VISIBLE);
                }

            }
        });

        ivClearUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserName.setText("");
            }
        });

        ivClearPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setText("");
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.checkUseID(etUserName,mContext)) {
                    if (TextUtils.isEmpty(etPassword.getEditableText().toString())) {
                        ToastUtils.showShort(getString(R.string.input_user_pwd));
                        return;
                    }
                    doLoginAction();
                }
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(ForgetPasswordActivity.class);
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
                            SPUtils.getInstance().put(Constants.TOKEN,new Gson().toJson(userInfo.tokens));
                            SPUtils.getInstance().put(Constants.USER_NAME, etUserName.getEditableText().toString());
                            SPUtils.getInstance().put(Constants.PASSWORD, etPassword.getEditableText().toString());
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

    @Override
    public void onBackPressed() {
        doubleClickExist();
    }

    private long mExitTime;

    /****
     * 连续两次点击退出
     */
    private boolean doubleClickExist() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort(R.string.double_click_exit);
            mExitTime = System.currentTimeMillis();
            return true;
        } else {
            Constants.HAS_VESRSION_TIPS = false;
            AppManager.getAppManager().AppExit(this);
            finish();
        }
        return false;
    }
}
