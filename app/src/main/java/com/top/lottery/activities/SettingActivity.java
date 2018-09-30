package com.top.lottery.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import butterknife.OnClick;

import static com.top.lottery.utils.Utils.getUserInfo;

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
    @BindView(R.id.tv_is_setting_secret)
    TextView tvIsSettingSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setTitle("设置");

        if (!TextUtils.isEmpty(Utils.getUserInfo().nickname)) {
            tvUsername.setText(Utils.getUserInfo().nickname);
        }

        tvModifyPayPwd.setVisibility(View.GONE);

        getIsSetSecret();
    }

    @OnClick({R.id.tv_username, R.id.rl_pwd_protect_setting, R.id.tv_modify_pwd, R.id.tv_modify_pay_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_username:
                showModifyUsername();
                break;
            case R.id.rl_pwd_protect_setting:
                ActivityUtils.startActivityForResult(mContext, PasswordManageActivity.class, 100);
                break;
            case R.id.tv_modify_pwd:
                ActivityUtils.startActivityForResult(mContext, ModifyPasswordActivity.class, 200);
                break;
            case R.id.tv_modify_pay_pwd:
                ToastUtils.showShort("待开发");
                break;
        }
    }

    private void showModifyUsername() {
        final EditText etNickname = new EditText(mContext);
        etNickname.setHint("请输入昵称");
        if (!TextUtils.isEmpty(Utils.getUserInfo().nickname)) {
            etNickname.setHint(Utils.getUserInfo().nickname);
        }

        etNickname.setPadding(20, 20, 20, 20);
//        etNickname.setTextColor(getResources().getColor(R.color.color_tittle));
//        etNickname.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(20,20,20,20);
//        etNickname.setLayoutParams(params);


        AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("修改昵称").setView(etNickname).create();

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                doMoidfyNickname(etNickname.getEditableText().toString());
            }
        });

        alertDialog.show();
    }


    private void getIsSetSecret() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.USER_ISSETSECURITY)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        if (response.body().code == 1) {
                            tvIsSettingSecret.setText("已设置");
                        } else {
                            tvIsSettingSecret.setText("未设置");
                        }
                    }

                    @Override
                    public void onError(Response response) {

                    }
                });
    }

    //修改昵称
    private void doMoidfyNickname(final String nickname) {

        if (TextUtils.isEmpty(nickname)) {
            ToastUtils.showShort("请输入昵称");
            return;
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("nickname", nickname);
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.USER_SETNICKNAME)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        ToastUtils.showShort(response.body().msg);
                        if (response.body().code == 1) {
                            tvUsername.setText(nickname);
                            UserInfo userInfo = Utils.getUserInfo();
                            userInfo.nickname = nickname;
                            Utils.saveUserInfo(userInfo);
                            setResult(RESULT_OK);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                tvIsSettingSecret.setText("已设置");
            }
        }
    }
}
