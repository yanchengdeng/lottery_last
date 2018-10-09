package com.top.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.ForgetPasswordParams;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/10/9  09:42
 * Email: yanchengdeng@gmail.com
 * Describle: 忘记密码
 */
public class ForgetPasswordStepTwoActivity extends BaseActivity {

    @BindView(R.id.et_safe_ask)
    TextView etSafeAsk;
    @BindView(R.id.et_safe_answer)
    EditText etSafeAnswer;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    private ForgetPasswordParams forgetPasswordParams;

    private OptionsPickerView optionsPickerViewMember;
    private String question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_step_two);
        ButterKnife.bind(this);
        setTitle("忘记密码(2/3)");
        forgetPasswordParams = (ForgetPasswordParams) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        getQuestionList();


        etSafeAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionsPickerViewMember != null) {
                    optionsPickerViewMember.show();
                } else {
                    ToastUtils.showShort("您没有设置安全问题，请联系管理员");
                }
            }
        });


        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionsPickerViewMember != null) {
                    if (TextUtils.isEmpty(etSafeAnswer.getEditableText().toString())) {
                        ToastUtils.showShort("请输入回答内容");
                    } else {
                        checkQuestion();
                    }
                } else {
                    ToastUtils.showShort("您没有设置安全问题，请联系管理员");
                }

            }
        });
    }

    //校验问题
    private void checkQuestion() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", forgetPasswordParams.uid);
        data.put("question",question);
        data.put("answer",etSafeAnswer.getEditableText().toString());
        OkGo.<LotteryResponse<String[]>>post(Constants.Net.PUBLIC_CHECKANSWER)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String[]>> response) {
                        dismissLoadingBar();
                        forgetPasswordParams.answer = etSafeAnswer.getEditableText().toString();
                        forgetPasswordParams.quesiton = question;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.PASS_OBJECT, forgetPasswordParams);
                        ActivityUtils.startActivityForResult(bundle, mContext, ForgetPasswordStepThreeActivity.class, 200);

                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


    private void getQuestionList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", forgetPasswordParams.uid);
        OkGo.<LotteryResponse<String[]>>post(Constants.Net.PUBLIC_GETQUESTIONS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String[]>> response) {
                        String[] answers = response.body().body;
                        if (answers != null && answers.length > 0) {
                            etSafeAsk.setText(answers[0]);
                            initAskSelecte(answers);
                        } else {
                            ToastUtils.showShort("" + response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });

    }

    private void initAskSelecte(final String[] answers) {
        optionsPickerViewMember = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                etSafeAsk.setText(answers[options1]);
                question = answers[options1];


            }
        }).build();

        optionsPickerViewMember.setPicker(Arrays.asList(answers));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
