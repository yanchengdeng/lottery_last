package com.top.lottery.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.top.lottery.R;


/**
*
* Author: 邓言诚  Create at : 2018/6/13  10:52
* Email: yanchengdeng@gmail.com
* Describle:
*/
public abstract class BaseFragment extends Fragment {

    // 布局view
    protected View bindingView;
    // fragment是否显示了
    protected boolean mIsVisible = false;
    // 加载中
    private View loadingView;
    // 加载失败
    private LinearLayout mRefresh;
    // 内容布局
    protected RelativeLayout mContainer;
    // 动画
    private AnimationDrawable mAnimationDrawable;
    private TextView tvErrorTips;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_base, null);
        bindingView =inflater.inflate( setContent(), null, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRootView().setLayoutParams(params);
        tvErrorTips = ll.findViewById(R.id.tv_error_tips);
        mContainer = (RelativeLayout) ll.findViewById(R.id.container);
        mContainer.addView(bindingView.getRootView());
        return ll;
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    protected void onInvisible() {
    }

    /**
     * 显示时加载数据,需要这样的使用
     * 注意声明 isPrepared，先初始化
     * 生命周期会先执行 setUserVisibleHint 再执行onActivityCreated
     * 在 onActivityCreated 之后第一次显示加载数据，只加载一次
     */
    protected void loadData() {

    }

    protected void onVisible() {
        loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadingView = ((ViewStub) getView(R.id.vs_loading)).inflate();
        ImageView img = loadingView.findViewById(R.id.img_progress);

        // 加载动画
        mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        // 默认进入页面就开启动画
        if (mAnimationDrawable!=null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        mRefresh = getView(R.id.ll_error_refresh);
//        // 点击加载失败布局
//        mRefresh.setOnClickListener(new PerfectClickListener() {
//            @Override
//            protected void onNoDoubleClick(View v) {
//                showLoading();
//                onRefresh();
//            }
//        });
        bindingView.setVisibility(View.GONE);

    }

    protected <T extends View> T getView(int id) {
        return (T) getView().findViewById(id);
    }

    /**
     * 布局
     */
    public abstract int setContent();

//    /**
//     * 加载失败后点击后的操作
//     */
//    protected void onRefresh() {
//
//    }

    /**
     * 显示加载中状态
     */
    protected void showLoading() {
        if (loadingView !=null &&loadingView.getVisibility() != View.VISIBLE) {
            loadingView.setVisibility(View.VISIBLE);
        }
        // 开始动画
        if (mAnimationDrawable!=null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        if (bindingView.getVisibility() != View.GONE) {
            bindingView.setVisibility(View.GONE);
        }
        if (mRefresh.getVisibility() != View.GONE) {
            mRefresh.setVisibility(View.GONE);
        }
    }

    /**
     * 加载完成的状态
     */
    protected void showContentView() {

        if (loadingView !=null &&loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable!=null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mRefresh.getVisibility() != View.GONE) {
            mRefresh.setVisibility(View.GONE);
        }
        if (bindingView.getVisibility() != View.VISIBLE) {
            bindingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError() {
        if (loadingView !=null &&loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable!=null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mRefresh.getVisibility() != View.VISIBLE) {
            mRefresh.setVisibility(View.VISIBLE);
        }
        if (bindingView.getVisibility() != View.GONE) {
            bindingView.setVisibility(View.GONE);
        }

    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError(String errorTips) {
        if (loadingView !=null &&loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable!=null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mRefresh.getVisibility() != View.VISIBLE) {
            mRefresh.setVisibility(View.VISIBLE);
        }
        if (bindingView.getVisibility() != View.GONE) {
            bindingView.setVisibility(View.GONE);
        }

        tvErrorTips.setText(errorTips);

    }
}
