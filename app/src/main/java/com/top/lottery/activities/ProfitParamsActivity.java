package com.top.lottery.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ProfitParams;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/27  01:44
 * Email: yanchengdeng@gmail.com
 * Describle: 收益参数
 */
public class ProfitParamsActivity extends BaseActivity {

    @BindView(R.id.tv_fanlijifen)
    TextView tvFanlijifen;
    @BindView(R.id.tv_fenhong)
    TextView tvFenhong;
    @BindView(R.id.tv_credit_rollin)
    TextView tvCreditRollin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_params);
        ButterKnife.bind(this);
        setTitle("收益参数");

        getStaticParams();

    }

    private void getStaticParams() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<ProfitParams>>post(Constants.Net.STATISTICS_GETPARAMS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<ProfitParams>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<ProfitParams>> response) {
                        ProfitParams profitParams = response.body().body;
                        dismissLoadingBar();
                        if (profitParams != null) {
                            tvFanlijifen.setText("配额返利："+profitParams.rebate_ratio);
                            tvFenhong.setText("配额奖金："+profitParams.reward_ratio+"  配额责任分红："+profitParams.bonus_ratio);
                            tvCreditRollin.setText("配额信用："+profitParams.credit_line_score);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        showError(Utils.toastInfo(response));
                    }
                });
    }


}
