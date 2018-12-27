package com.top.lottery.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.top.lottery.R;
import com.top.lottery.activities.AccountBillsActivity;
import com.top.lottery.activities.BuyLotteryRecordActivity;
import com.top.lottery.activities.FinanceRechargeActivity;
import com.top.lottery.activities.IntergrateHandleActivity;
import com.top.lottery.activities.IntergrayRollOutActivity;
import com.top.lottery.activities.LoginActivity;
import com.top.lottery.activities.ManageActivity;
import com.top.lottery.activities.MessageListActivity;
import com.top.lottery.activities.ProfitActivity;
import com.top.lottery.activities.SettingActivity;
import com.top.lottery.adapters.PercentCenterMenuAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MenuCenterItem;
import com.top.lottery.beans.UnreadMsgSum;
import com.top.lottery.beans.UseAuth;
import com.top.lottery.beans.UserInfo;
import com.top.lottery.events.MemberSuccess;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/12/15  22:35
 * Email: yanchengdeng@gmail.com
 * Describle: v2.0 个人中心
 */
public class MeFragment extends Fragment {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_accout)
    TextView tvAccout;
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.tv_intergray)
    TextView tvIntergray;
    @BindView(R.id.tv_intergray_proxy)
    TextView tvIntergrayProxy;
    @BindView(R.id.tv_intergray_proxy_out)
    TextView tvIntergrayProxyOut;
    @BindView(R.id.ll_proxy_ui)
    LinearLayout llProxyUi;
    @BindView(R.id.grid)
    RecyclerView grid;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    private Unbinder unbinder;
    private UserInfo userInfo;

    private PercentCenterMenuAdapter percentCenterMenuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_percent_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {



        tvIntergrayProxyOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putFloat(Constants.PASS_STRING, userInfo.daili_score);
                    Intent intent = new Intent(Utils.context, IntergrayRollOutActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 200);
                }
            }
        });

        grid.setLayoutManager(new GridLayoutManager(Utils.context, 3));
//        grid.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
//        grid.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal(this));
        percentCenterMenuAdapter = new PercentCenterMenuAdapter(R.layout.adapter_grid_person_centre_new, new ArrayList<MenuCenterItem>());
        grid.setAdapter(percentCenterMenuAdapter);


        percentCenterMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                switch (percentCenterMenuAdapter.getData().get(position).type) {
                    case 1:
                        bundle.putString(Constants.PASS_STRING, "1");
                        Utils.openActivity(Utils.context, BuyLotteryRecordActivity.class, bundle);
                        break;
                    case 2:
                        bundle.putString(Constants.PASS_STRING, "2");
                        Utils.openActivity(Utils.context, BuyLotteryRecordActivity.class, bundle);
                        break;
                    case 3:
                        bundle.putString(Constants.PASS_STRING, "3");
                        Utils.openActivity(Utils.context, BuyLotteryRecordActivity.class, bundle);
                        break;
                    case 4:
                        ActivityUtils.startActivity(AccountBillsActivity.class);
                        break;
                    case 5:
                        ActivityUtils.startActivity(MessageListActivity.class);
                        break;
                    case 6:
                        ActivityUtils.startActivityForResult(Utils.context, SettingActivity.class, 400);
                        break;
                    case 7:
                        ActivityUtils.startActivity(ManageActivity.class);
                        break;
                    case 8:
                        ActivityUtils.startActivity(ProfitActivity.class);
                        break;
                    case 9:
                        ActivityUtils.startActivity(IntergrateHandleActivity.class);
                        break;
                    case 10:
                        UseAuth useAuth = Utils.getAuth();
                        if (useAuth != null) {
                            if (useAuth.caiwu_deposit == 1) {
                                ActivityUtils.startActivityForResult(Utils.context, FinanceRechargeActivity.class, 300);
                            } else {
                                ToastUtils.showShort("权限不足");
                            }
                        } else {
                            ToastUtils.showShort("权限不足");
                        }
                        break;
                }
            }
        });


        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getUerAuthor();

                getMenuCenter();
            }
        });
        smartRefresh.autoRefresh();


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MemberSuccess event) {
        getUserDetailInfo();
    }

    //后去用户权限
    private void getUerAuthor() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UseAuth>>post(Constants.Net.USER_GETAUTH)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UseAuth>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UseAuth>> response) {
                        UseAuth useAuth = response.body().body;
//                        if (useAuth.rollout == 1) {
//                            llProxyUi.setVisibility(View.VISIBLE);
//                        } else {
//                            llProxyUi.setVisibility(View.GONE);
//                        }
                        Utils.saveUserAuth(useAuth);

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });

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
                        userInfo = response.body().body;
                        if (userInfo != null) {
                            if (!TextUtils.isEmpty(userInfo.username)) {
                                tvAccout.setText(userInfo.username);
                                tvNickname.setText("" + userInfo.nickname);
                            }

                            if (!TextUtils.isEmpty(userInfo.score)) {
                                tvIntergray.setText("账户积分：" + userInfo.score);
                            }

                            if (userInfo.daili_score > 0) {
                                tvIntergrayProxy.setText("代理返利积分：" + userInfo.daili_score);
                            }


                            Utils.saveUserInfo(userInfo);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


    private void getMenuCenter() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<List<MenuCenterItem>>>post(Constants.Net.USER_GETCENTERMENULIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<MenuCenterItem>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<MenuCenterItem>>> response) {
                        smartRefresh.finishRefresh();
                        List<MenuCenterItem> menuCenterItems = response.body().body;
                        if (menuCenterItems != null && menuCenterItems.size() > 0) {
                            percentCenterMenuAdapter.setNewData(menuCenterItems);
                        }
                        getUnreadNum();
                    }

                    @Override
                    public void onError(Response response) {
                        smartRefresh.finishRefresh();
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    private void getUnreadNum() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UnreadMsgSum>>post(Constants.Net.MESSAGE_NEWMESSSAGESUM)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UnreadMsgSum>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UnreadMsgSum>> response) {
                        UnreadMsgSum unreadMsgSum = response.body().body;
                        if (percentCenterMenuAdapter != null) {
                            if (unreadMsgSum != null && unreadMsgSum.sum > 0) {
                                for (MenuCenterItem item : percentCenterMenuAdapter.getData()) {
                                    if (item.type == 5) {
                                        item.isShowRedPoint = true;
                                    } else {
                                        item.isShowRedPoint = false;
                                    }
                                }
                            } else {
                                for (MenuCenterItem item : percentCenterMenuAdapter.getData()) {
                                    item.isShowRedPoint = false;
                                }
                            }
                            percentCenterMenuAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Response response) {
                    }
                });
    }


    //退出登陆
    private void showExitDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(Utils.context)
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
                        SPUtils.getInstance().put(Constants.USER_INFO, "");
                        Intent intent = new Intent(Utils.context, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onError(Response response) {

                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 200 || requestCode == 300 || requestCode == 400) {
//            if (resultCode == getActivity().RESULT_OK) {
//                getUserDetailInfo();
//            }
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        getUserDetailInfo();
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getUserDetailInfo();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
