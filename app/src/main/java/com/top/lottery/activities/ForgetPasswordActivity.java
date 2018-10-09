package com.top.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.ForgetPasswordParams;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/8/30  23:54
 * Email: yanchengdeng@gmail.com
 * Describle: 忘记密码
 */
public class ForgetPasswordActivity extends BaseActivity {


    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        setTitle("忘记密码(1/3)");

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etUserName.getEditableText().toString())) {
                    ToastUtils.showShort("请输入用户ID");
                }else{
                    Bundle bundle = new Bundle();
                    ForgetPasswordParams passwordParams = new ForgetPasswordParams();
                    passwordParams.uid = etUserName.getEditableText().toString();
                    bundle.putSerializable(Constants.PASS_OBJECT,passwordParams);
                    ActivityUtils.startActivityForResult(bundle,mContext,ForgetPasswordStepTwoActivity.class,100);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            if (resultCode==RESULT_OK){
                finish();
            }
        }
    }
}


