package com.top.lottery.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.TicketOutDetailInfo;
import com.top.lottery.beans.TicketOutInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/2  21:18
 * Email: yanchengdeng@gmail.com
 * Describle: 出票详情
 */
public class TicketDetailActivity extends BaseActivity {

    @BindView(R.id.tv_term_id)
    TextView tvTermId;
    @BindView(R.id.tv_award_code)
    TextView tvAwardCode;
    @BindView(R.id.tv_award_intergry)
    TextView tvAwardIntergry;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    private TicketOutInfo ticketOutInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        ButterKnife.bind(this);
        setTitle("出票详情");

        ticketOutInfo = (TicketOutInfo) getIntent().getSerializableExtra(Constants.PASS_OBJECT);

        getTicketDetail();


    }

    //出票详情
    private void getTicketDetail() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("award_id", ticketOutInfo.award_id);
        data.put("lottery_type",ticketOutInfo.lottery_type);
        OkGo.<LotteryResponse<TicketOutDetailInfo>>post(Constants.Net.AWARD_DETAIL)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<TicketOutDetailInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<TicketOutDetailInfo>> response) {
                        showContentView();
                        dismissLoadingBar();
                        initData(response.body().body);
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        showError();
                        dismissLoadingBar();
                    }
                });
    }

    private void initData(TicketOutDetailInfo ticketOutDetailInfo) {
        tvTermId.setText("第" + ticketOutDetailInfo.award_id + "期");
        tvAwardCode.setText("" + ticketOutDetailInfo.prize_code);
        tvAwardIntergry.setText("" + ticketOutInfo.reward_score);
        tvCreateTime.setText("" + ticketOutDetailInfo.lottery_time);
    }
}
