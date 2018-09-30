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
import com.top.lottery.beans.UserInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
*
* Author: 邓言诚  Create at : 2018/9/26  22:27
* Email: yanchengdeng@gmail.com
* Describle: 分红积分转入
*/
public class ShareBounsRollInActivity extends BaseActivity {

    @BindView(R.id.tv_fanlijifen)
    TextView tvFanlijifen;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.tv_useable_total)
    TextView tvUseableTotal;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bouns_roll_in);
        ButterKnife.bind(this);
        setTitle("分红积分转入");

        tvUseableTotal.setText(String.valueOf(Utils.getUserInfo().score));
        tvFanlijifen.setText("-"+String.valueOf(Utils.getUserInfo().bonus_score));

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etInput.getEditableText().toString())) {
                    ToastUtils.showShort("请输入金额");
                } else {
                    doConfirm();
                }
            }
        });
    }

    /**
     *  /**
     * 10：返利转出
     15：分红积分转出
     16：分红积分转入
     17：信用积分转入
     */
    private void doConfirm() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("score", etInput.getEditableText().toString());
        data.put("operate_type", "16");
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.USER_SCOREOPERATE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        ToastUtils.showShort("" + response.body().msg);
                        if (response.body().code == 1) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }
}
