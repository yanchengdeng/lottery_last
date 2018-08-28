package com.top.lottery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.lzy.okgo.OkGo;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.TokenTimeOut;
import com.top.lottery.utils.StatusBarUtil;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

public abstract class BaseActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate, View.OnClickListener {
    protected BGASwipeBackHelper mSwipeBackHelper;
    private View viewRoot, contentView;
    private View tittleUi, tvTtittleLine;
    private View refresh;
    private MaterialDialog loadDialog;
    public ImageView ivBack, ivRightFunction;
    public TextView tvTittle, tvRightFunction;
    public TextView tvErrorTips;
    public Activity mContext;
    private MaterialDialog materialDialog;

    public boolean isCanTouzhu;//是否可以投注
    //TODO 不确定
    public boolean isInDeepNight;//是否处于凌晨时刻   22：59：59 ~07：59：59 不允许投注
    public long curretDifServer;// 本次开奖时间和当前系统时间差
    public int awardId;//本期开奖期号


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setContentView(int layoutResID) {
        initSwipeBackFinish();
        this.mContext = this;
//        StatusBarUtil.setLightMode(this);
        EventBus.getDefault().register(this);
        viewRoot = getLayoutInflater().inflate(R.layout.activity_base, null);
//        viewRoot =   LayoutInflater.from(BaseActivity.this).inflate(R.layout.activity_base,null,false);
        contentView = LayoutInflater.from(BaseActivity.this).inflate(layoutResID, null, false);
        // content
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.getRootView().setLayoutParams(params);
        RelativeLayout mContainer = (RelativeLayout) viewRoot.getRootView().findViewById(R.id.container);
        mContainer.addView(contentView.getRootView());
        getWindow().setContentView(viewRoot.getRootView());

        contentView.getRootView().setVisibility(View.GONE);


        ivBack = findViewById(R.id.iv_back);
        ivRightFunction = findViewById(R.id.iv_right_function);
        tvTittle = findViewById(R.id.tv_title);
        tvRightFunction = findViewById(R.id.tv_right_function);
        refresh = getView(R.id.ll_error_refresh);
        tvErrorTips = getView(R.id.tv_error_tips);
        tittleUi = getView(R.id.rl_tittle_ui);
        tvTtittleLine = getView(R.id.tv_tittle_ui_line);


        setToolBar();
        // 点击加载失败布局
//        refresh.setOnClickListener(new PerfectClickListener() {
//            @Override
//            protected void onNoDoubleClick(View v) {
//                showLoading();
//                onRefresh();
//            }
//        });
    }


    /**
     * 获取下次开奖时间 与当前系统时间差
     *
     * @param serverTime      服务器时间
     * @param nextLotteryTime 下次开奖时间
     * @return
     */
    public long getCurrentDifServer(String serverTime, String nextLotteryTime) {
        long nextTime = TimeUtils.string2Millis(nextLotteryTime, new SimpleDateFormat(Constants.DATE_FORMAR, Locale.CHINA));
        long currentTime = TimeUtils.string2Millis(serverTime, new SimpleDateFormat(Constants.DATE_FORMAR, Locale.CHINA));
        //HH 返回24 小时制   hh  返回12小时
        String currntHourse = TimeUtils.millis2String(currentTime, new SimpleDateFormat("HH", Locale.CHINA));
        //时间段在  22：59：59  ~ 7：59：59：  不允许投注
        if (currntHourse.equals("23") || currntHourse.equals("00") || currntHourse.equals("01") ||
                currntHourse.equals("02") || currntHourse.equals("03") || currntHourse.equals("04")
                || currntHourse.equals("05") || currntHourse.equals("06") || currntHourse.equals("07")) {
            isInDeepNight = true;
            isCanTouzhu =false;
        } else {
            isInDeepNight = false;
        }
        return nextTime - currentTime;
    }

    //获取倒计时
    public long getCountDownMillions() {

        if (isInDeepNight) {
            return curretDifServer;
        } else {
            LogUtils.w("dyc---接口返回时间差"+isCanTouzhu+"========"+ Utils.millis2FitTimeSpan(curretDifServer-Constants.TIME_CAN_NOT_TOUZHU,4));
            return isCanTouzhu?curretDifServer - Constants.TIME_CAN_NOT_TOUZHU:curretDifServer;//isCanTouzhu ? curretDifServer  : Constants.TIME_CAN_NOT_TOUZHU ;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof TokenTimeOut) {
            onShowExpire();

        }
    }

    public void onShowExpire() {
        if (materialDialog != null) {
            return;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .title("温馨提示")
                .content("您的账号已过期，请重新登陆")
                .positiveColor(getResources().getColor(R.color.color_blue)).positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        SPUtils.getInstance().put(Constants.USER_INFO, "");
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .build();


        materialDialog.setCancelable(false);
        materialDialog.setCanceledOnTouchOutside(false);
        if (materialDialog.isShowing()) {
            materialDialog.dismiss();
        } else {
            materialDialog.show();
        }
    }

    //影藏标题
    public void hideTittle() {
        tittleUi.setVisibility(View.GONE);
        tvTtittleLine.setVisibility(View.GONE);
    }


    /**
     * 设置titlebar
     */
    protected void setToolBar() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void setTitle(CharSequence text) {
        tvTittle.setText(text);
    }

    protected void showLoading() {
        if (loadDialog == null) {
            loadDialog = new MaterialDialog.Builder(this).show();
        } else {
            loadDialog.show();
        }

        if (contentView.getVisibility() != View.GONE) {
            contentView.setVisibility(View.GONE);
        }
        if (refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
    }

    /**
     * 显示正常数据
     */
    protected void showContentView() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }

        if (refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 出现异常 显示错误提示
     */
    protected void showError() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }

        if (refresh.getVisibility() != View.VISIBLE) {
            refresh.setVisibility(View.VISIBLE);
        }
        if (contentView.getVisibility() != View.GONE) {
            contentView.setVisibility(View.GONE);
        }
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError(String errorTips) {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }

        if (refresh.getVisibility() != View.VISIBLE) {
            refresh.setVisibility(View.VISIBLE);
        }
        if (contentView.getVisibility() != View.GONE) {
            contentView.setVisibility(View.GONE);
        }
        tvErrorTips.setText(errorTips);

    }

    /**
     * 失败后点击刷新
     */
    protected void onRefresh() {

    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(false);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setStatusBarColor(@ColorInt int color) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     * @param statusBarAlpha 透明度
     */
    public void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    /**
     * 查找View
     *
     * @param id 控件的id
     * @return
     */
    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelAll();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
