package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.LotteryOpenCodeAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardOrderDetail;
import com.top.lottery.beans.AwardOrderScore;
import com.top.lottery.beans.AwardRecordItem;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.TicketOutInfo;
import com.top.lottery.events.NoticeToDoNewTermCodeEvent;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/8/30  10:25
 * Email: yanchengdeng@gmail.com
 * Describle: 投注记录详情
 */
public class LotteryRecordDetailActivity extends BaseActivity {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_lid_title)
    TextView tvLidTitle;
    @BindView(R.id.tv_award_id)
    TextView tvAwardId;
    @BindView(R.id.tv_touzhu_integery)
    TextView tvTouzhuIntegery;
    @BindView(R.id.tv_touzhu_status)
    TextView tvTouzhuStatus;
    @BindView(R.id.tv_touzhu_jiali)
    TextView tvTouzhuJiali;
    @BindView(R.id.recycle_records)
    RecyclerView recycleRecords;
    @BindView(R.id.tv_times)
    TextView tvTimes;
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;
    @BindView(R.id.tv_award_status)
    TextView tvAwardStatus;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.tv_is_chase)
    TextView tvIsChase;
    @BindView(R.id.tv_is_win_chase)
    TextView tvIsWinChase;
    @BindView(R.id.tv_cancle_chase)
    TextView tvCancleChase;
    @BindView(R.id.tv_goon_touzhu)
    TextView tvGoonTouzhu;
    @BindView(R.id.tv_cancle_order)
    TextView tvCancleOrder;
    private AwardRecordItem awardRecordItem;
    private  AwardOrderDetail awardOrderDetail;
    private LotteryOpenCodeAdapter lotteryOpenCodeAdapter;
    private  LotteryInfo lotteryInfo = new LotteryInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_record_detail);
        ButterKnife.bind(this);
        setTitle("投注记录详情");
        awardRecordItem = (AwardRecordItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        lotteryOpenCodeAdapter = new LotteryOpenCodeAdapter(R.layout.adapter_award_open_item,new ArrayList<AwardOrderScore>());
        recycleRecords.setLayoutManager(new LinearLayoutManager(this));
        recycleRecords.setNestedScrollingEnabled(false);
        recycleRecords.addItemDecoration(RecycleViewUtils.getNoBottomLineDecoration(this));
        recycleRecords.setAdapter(lotteryOpenCodeAdapter);

        getOrderDetail();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeToDoNewTermCodeEvent event) {
        if (event!=null){
            String className = event.className;
            String methodName = event.methodName;
            if (className.equals(LotteryRecordDetailActivity.class.getName())){
                if (methodName.equals(Constants.Net.CART_ADDBYORDERID)) {
                    doGoOnTouzhu();
                }
            }
        }
    }

    //获取订单详情
    private void getOrderDetail() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("order_id", awardRecordItem.order_id);//
        OkGo.<LotteryResponse<AwardOrderDetail>>post(Constants.Net.RECORD_DETAIL)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<AwardOrderDetail>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<AwardOrderDetail>> response) {
                        dismissLoadingBar();

                         awardOrderDetail = response.body().body;
                        initData(awardOrderDetail);


                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        dismissLoadingBar();

                    }
                });
    }

    private void initData(AwardOrderDetail awardOrderDetail) {
        Glide.with(mContext).load(awardOrderDetail.lid_icon).into(ivLogo);
        tvLidTitle.setText(""+awardOrderDetail.lid_title);
        tvAwardId.setText("第"+awardOrderDetail.award_id+"期");
        tvTouzhuIntegery.setText(awardOrderDetail.cost_score+"积分");
        tvAwardStatus.setText(""+awardOrderDetail.reward_title);
        tvTouzhuStatus.setText(""+awardOrderDetail.reward_title);
        tvTouzhuJiali.setText(""+awardOrderDetail.reward_score);
        tvTimes.setText(awardOrderDetail.record_times+"倍");
        tvOrderTitle.setText(awardOrderDetail.order_title+"");
        tvOrderId.setText(""+awardOrderDetail.order_id);
        tvCreateTime.setText(""+awardOrderDetail.create_time);
        tvIsChase.setText(awardOrderDetail.is_chase==0?"否":"是");
        tvIsWinChase.setText(awardOrderDetail.is_win_stop_chase== 0 ?"否":"是");
        tvCancleChase.setVisibility(awardOrderDetail.show_cancel_chase_button.equals("hide")?View.GONE:View.VISIBLE);
        tvCancleOrder.setVisibility(awardOrderDetail.show_cancel_order_button.equals("hide")?View.GONE:View.VISIBLE);
        lotteryOpenCodeAdapter.setNewData(awardOrderDetail.records);


        lotteryInfo = new LotteryInfo();
        lotteryInfo.lottery_type = awardOrderDetail.lottery_type;
        lotteryInfo.lid = awardOrderDetail.lid;



    }

    @OnClick({R.id.tv_order_title, R.id.tv_award_status, R.id.tv_cancle_chase, R.id.tv_goon_touzhu, R.id.tv_cancle_order})
    public void onViewClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASS_OBJECT,awardOrderDetail);
        switch (view.getId()) {
            case R.id.tv_order_title:
                if (awardOrderDetail==null){
                    return;
                }
                if (awardOrderDetail.is_chase==1) {
                    ActivityUtils.startActivity(bundle, TicketOutListActivity.class);
                }else{
                    Bundle bundTieckt = new Bundle();
                    TicketOutInfo ticketOutInfo = new TicketOutInfo();
                    ticketOutInfo.reward_score = awardOrderDetail.reward_score;
                    ticketOutInfo.award_id = awardOrderDetail.award_id;
                    bundTieckt.putSerializable(Constants.PASS_OBJECT,ticketOutInfo);
                    ActivityUtils.startActivity(bundTieckt, TicketDetailActivity.class);
                }
                break;
            case R.id.tv_award_status:
                if (awardOrderDetail==null){
                    return;
                }
                ActivityUtils.startActivity(bundle,AwardGotDetailActivity.class);
                break;
            case R.id.tv_cancle_chase:
                doCancleChase();
                break;
            case R.id.tv_goon_touzhu:
                doGoOnTouzhu();
                break;
            case R.id.tv_cancle_order:
                doCancleOrder();
                break;
        }
    }

    //停止追号
    private void doCancleChase() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("order_id", awardOrderDetail.order_id);
        OkGo.<LotteryResponse<String>>post(Constants.Net.RECORD_CANCELCHASE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String>> response) {
                        ToastUtils.showShort(""+response.body().msg);
                        if (response.body().code==1) {
                            tvCancleChase.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        LogUtils.w("dyc", response + "");
                    }
                });
    }

    //继续本次投注

    private void doGoOnTouzhu(){
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("order_id", awardOrderDetail.order_id);
        OkGo.<LotteryResponse<String>>post(Constants.Net.CART_ADDBYORDERID)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String>> response) {
                        if (response.body().code==1){
                            Bundle bundle = new Bundle();
                            if (lotteryInfo==null){
                                return;
                            }

                            bundle.putSerializable(Constants.PASS_OBJECT,lotteryInfo);
                            bundle.putBoolean(Constants.PASS_BOLLEAN,true);
                            ActivityUtils.startActivity(bundle, ConfirmCodesActivity.class);
                            finish();
                        }

                    }

                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });


    }


    //取消订单
    public void doCancleOrder(){
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("order_id", awardOrderDetail.order_id);
        OkGo.<LotteryResponse<String>>post(Constants.Net.RECORD_CANCELORDER)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String>> response) {
                        ToastUtils.showShort(""+response.body().msg);
                        if (response.body().code==1) {
                            tvCancleOrder.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        LogUtils.w("dyc", response + "");
                    }
                });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==101){
//            if (resultCode==RESULT_OK){
//
//            }
//        }
//    }
}
