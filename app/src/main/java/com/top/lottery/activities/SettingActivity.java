package com.top.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.top.lottery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: 邓言诚  Create at : 2018/8/19  11:15
 * Email: yanchengdeng@gmail.com
 * Describle:设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.rl_pwd_protect_setting)
    RelativeLayout rlPwdProtectSetting;
    @BindView(R.id.tv_modify_pwd)
    TextView tvModifyPwd;
    @BindView(R.id.tv_modify_pay_pwd)
    TextView tvModifyPayPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setTitle("设置");
    }

    @OnClick({R.id.rl_pwd_protect_setting, R.id.tv_modify_pwd, R.id.tv_modify_pay_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_pwd_protect_setting:
                ToastUtils.showShort("待开发");
                break;
            case R.id.tv_modify_pwd:
                ActivityUtils.startActivityForResult(mContext,ModifyPasswordActivity.class,200);
                break;
            case R.id.tv_modify_pay_pwd:
                ToastUtils.showShort("待开发");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200){
            if (resultCode==RESULT_OK){
                finish();
            }
        }
    }
}
