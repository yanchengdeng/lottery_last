package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
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

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/8/30  13:56
 * Email: yanchengdeng@gmail.com
 * Describle: 密保管理
 */
public class PasswordManageActivity extends BaseActivity {


    @BindView(R.id.et_safe_ask)
    EditText etSafeAsk;
    @BindView(R.id.et_safe_answer)
    EditText etSafeAnswer;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manage);
        ButterKnife.bind(this);
        setTitle("密保管理");
        showContentView();

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etSafeAsk.getEditableText().toString().trim())) {
                    ToastUtils.showShort("请输入安全问题");
                    return;
                }

                if (TextUtils.isEmpty(etSafeAnswer.getEditableText().toString().trim())) {
                    ToastUtils.showShort("请输入回答内容");
                    return;
                }
                doSavePasswordQuesiton(etSafeAsk.getEditableText().toString(), etSafeAnswer.getEditableText().toString());
            }
        });

    }

    private void doSavePasswordQuesiton(String question, String answer) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("question", question);
        data.put("answer", answer);
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.USER_SETSECURITY)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        ToastUtils.showShort("" + response.body().msg);
                        KeyboardUtils.hideSoftInput(etSafeAnswer);
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
