package com.top.lottery.activities;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Author: 邓言诚  Create at : 2018/8/19  11:26
 * Email: yanchengdeng@gmail.com
 * Describle: 修改密码
 */
public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.et_current_password)
    EditText etCurrentPassword;
    @BindView(R.id.iv_current_eye)
    ImageView ivCurrentEye;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.iv_new_eye)
    ImageView ivNewEye;
    @BindView(R.id.et_new_repeat_password)
    EditText etNewRepeatPassword;
    @BindView(R.id.iv_new_repeat_eye)
    ImageView ivNewRepeatEye;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private boolean isCurrentOpen,isNewOpen,isNewRepeatOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        setTitle("修改登录密码");


    }

    @OnClick({R.id.iv_current_eye, R.id.iv_new_eye, R.id.iv_new_repeat_eye, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_current_eye:
                isCurrentOpen = !isCurrentOpen;
                if (isCurrentOpen) {
                    //明文
                    etCurrentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //密文
                    etCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                ivCurrentEye.setSelected(isCurrentOpen);
                break;
            case R.id.iv_new_eye:
                isNewOpen = !isNewOpen;
                if (isNewOpen) {
                    //明文
                    etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //密文
                    etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                ivNewEye.setSelected(isNewOpen);
                break;
            case R.id.iv_new_repeat_eye:
                isNewRepeatOpen = !isNewRepeatOpen;
                if (isNewRepeatOpen) {
                    //明文
                    etNewRepeatPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //密文
                    etNewRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                ivNewRepeatEye.setSelected(isNewRepeatOpen);
                break;
            case R.id.tv_confirm:
                if (TextUtils.isEmpty(etCurrentPassword.getEditableText().toString())){
                    ToastUtils.showShort(getString(R.string.input_current_password));
                }else if (TextUtils.isEmpty(etNewPassword.getEditableText().toString())){
                    ToastUtils.showShort(getString(R.string.input_new_password));
                }else if (TextUtils.isEmpty(etNewRepeatPassword.getEditableText().toString())){
                    ToastUtils.showShort(getString(R.string.input_new_repeat_password));
                }else if (!etNewPassword.getEditableText().toString().equals(etNewRepeatPassword.getEditableText().toString())){
                    ToastUtils.showShort("两次新密码输入不一致");
                }else {
                    doModifyPwd(etCurrentPassword.getEditableText().toString(),etNewPassword.getEditableText().toString());
                }
                break;
        }
    }

    private void doModifyPwd(String currentPwd, String newPwd) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("old_password",currentPwd);
        data.put("new_password",newPwd);
        data.put("re_password",newPwd);
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.USER_SETPASSWORD)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        ToastUtils.showShort(""+response.body().msg);
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }
}
