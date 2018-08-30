package com.top.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.GridPersonAdapter;
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
 * Author: 邓言诚  Create at : 2018/8/29  00:15
 * Email: yanchengdeng@gmail.com
 * Describle:我的账户
 */
public class PercentInfoActivity extends BaseActivity {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_accout)
    TextView tvAccout;
    @BindView(R.id.tv_intergray)
    TextView tvIntergray;
    @BindView(R.id.grid)
    GridView grid;
    @BindView(R.id.tv_login_out)
    TextView tvLoginOut;
    @BindView(R.id.tv_intergray_proxy)
    TextView tvIntergrayProxy;
    @BindView(R.id.tv_intergray_proxy_out)
    TextView tvIntergrayProxyOut;
    @BindView(R.id.ll_proxy_ui)
    LinearLayout llProxyUi;


    //<!-- A级用户才有员工管理，这些员工只有充值以及加人的功能 -->
    private String[] actions = new String[]{"购彩记录", "中奖记录", "追号记录", "账户明细", "站内信", "设置"};
    private int[] icons = new int[]{R.mipmap.setting1, R.mipmap.setting2, R.mipmap.setting3
            , R.mipmap.setting4, R.mipmap.setting5, R.mipmap.setting6};


    private String[] actionsalL = new String[]{"购彩记录", "中奖记录", "追号记录", "账户明细", "站内信", "设置", "后台管理"};
    private int[] iconsAll = new int[]{R.mipmap.setting1, R.mipmap.setting2, R.mipmap.setting3
            , R.mipmap.setting4, R.mipmap.setting5, R.mipmap.setting6, R.mipmap.setting7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percent_info);
        ButterKnife.bind(this);
        setTitle("个人中心");
        showContentView();
        tvLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });

        grid.setAdapter(new GridPersonAdapter(mContext, actions, icons));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                switch (i) {
                    case 0:
                        bundle.putString(Constants.PASS_STRING,"1");
                        Utils.openActivity(mContext,BuyLotteryRecordActivity.class,bundle);
                        break;
                    case 1:
                        bundle.putString(Constants.PASS_STRING,"2");
                        Utils.openActivity(mContext,BuyLotteryRecordActivity.class,bundle);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        ActivityUtils.startActivity(ModifyPasswordActivity.class);

                        break;
                }
            }
        });


        getUserDetailInfo();

    }

    //用户详情

    /**
     * 用户类型，
     * 1：为会员
     * 2：店家
     * 3：区代
     * 4：大代
     */
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
                            if (!TextUtils.isEmpty(userInfo.username)) {
                                tvAccout.setText(userInfo.username);
                            }

                            if (!TextUtils.isEmpty(userInfo.score)) {
                                tvIntergray.setText("账户积分：" + userInfo.score);
                            }

                            if (!TextUtils.isEmpty(userInfo.daili_score)) {
                                tvIntergrayProxy.setText("代理返利积分："+userInfo.daili_score);
                                llProxyUi.setVisibility(View.VISIBLE);
                            }else{
                                llProxyUi.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        llProxyUi.setVisibility(View.VISIBLE);
                    }
                });
    }

    //退出登陆
    private void showExitDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("温馨提示")
                .content("确定退出登录？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        doLoginOutAction();

                    }
                }).negativeText("取消").negativeColor(getResources().getColor(R.color.color_info)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }


    //接口退出登录
    private void doLoginOutAction() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.LOGIN_OUT)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        ToastUtils.showShort("退出登录");
                        SPUtils.getInstance().put(Constants.USER_INFO, "");
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
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
