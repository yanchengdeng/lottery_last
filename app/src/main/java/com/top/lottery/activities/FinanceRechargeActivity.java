package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/26  00:06
 * Email: yanchengdeng@gmail.com
 * Describle: 财务充值
 */
public class FinanceRechargeActivity extends BaseActivity {

    @BindView(R.id.tv_current_score_total)
    TextView tvCurrentScoreTotal;
    @BindView(R.id.et_autor_id)
    EditText etAutorId;
    @BindView(R.id.et_autor_money)
    EditText etAutorMoney;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_recharge);
        ButterKnife.bind(this);
        setTitle("财务充值");

        tvCurrentScoreTotal.setText("您当前可操作积分为："+ Utils.getUserInfo().score);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etAutorId.getEditableText().toString())){
                    ToastUtils.showShort("请输入ID");
                    return;
                }

                if (etAutorId.getEditableText().toString().length()>11 || etAutorId.getEditableText().toString().length()<8){
                    ToastUtils.showShort("ID 为8~11位");
                    return;

                }

                if (TextUtils.isEmpty(etAutorMoney.getEditableText().toString())){
                    ToastUtils.showShort("请输入积分");
                    return;
                }

                doRecharge();
            }
        });




    }

    //充值
    private void doRecharge() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("add_uid", etAutorId.getEditableText().toString());
        data.put("add_score", etAutorMoney.getEditableText().toString());
        OkGo.<LotteryResponse<String>>post(Constants.Net.USER_DEPOSIT)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String>> response) {
                       if (response.body().code==1){
                           setResult(RESULT_OK);
                           finish();
                       }
                        ToastUtils.showShort(""+response.body().msg);
                    }

                    @Override
                    public void onError(Response response) {
                       ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }
}
