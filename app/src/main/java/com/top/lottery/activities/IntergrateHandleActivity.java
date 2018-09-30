package com.top.lottery.activities;

import android.os.Bundle;
import android.view.View;
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
 * Author: 邓言诚  Create at : 2018/9/26  00:04
 * Email: yanchengdeng@gmail.com
 * Describle: 积分操作
 */
public class IntergrateHandleActivity extends BaseActivity {

    @BindView(R.id.tv_fanlijifen)
    TextView tvFanlijifen;
    @BindView(R.id.tv_fanlijifen_rollout)
    TextView tvFanlijifenRollout;
    @BindView(R.id.tv_fenhong)
    TextView tvFenhong;
    @BindView(R.id.tv_fenhong_rollout)
    TextView tvFenhongRollout;
    @BindView(R.id.tv_fenhong_rollin)
    TextView tvFenhongRollin;
    @BindView(R.id.tv_credit)
    TextView tvCredit;
    @BindView(R.id.tv_credit_rollin)
    TextView tvCreditRollin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intergrate_handle);
        ButterKnife.bind(this);
        setTitle("积分操作");


    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetailInfo();
    }

    private void getUserDetailInfo() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UserInfo>>post(Constants.Net.USER_DETAIL)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo>> response) {
                        UserInfo userInfo = response.body().body;
                        if (userInfo != null) {

                            tvFanlijifen.setText(String.valueOf(userInfo.daili_score));
                            tvFenhong.setText("-"+String.valueOf(userInfo.bonus_score));
                            tvCredit.setText(userInfo.credit_line_score);
                            Utils.saveUserInfo(userInfo);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    @OnClick({R.id.tv_fanlijifen_rollout, R.id.tv_fenhong_rollout, R.id.tv_fenhong_rollin, R.id.tv_credit_rollin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fanlijifen_rollout:
                ActivityUtils.startActivity(FanliIntergaryRollOutActivity.class);
                break;
            case R.id.tv_fenhong_rollout:
                ActivityUtils.startActivity(SharebounsRollOutActivity.class);
                break;
            case R.id.tv_fenhong_rollin:
                ActivityUtils.startActivity(ShareBounsRollInActivity.class);
                break;
            case R.id.tv_credit_rollin:
                ActivityUtils.startActivity(CreditRollInActivity.class);
                break;
        }
    }
}
