package com.top.lottery.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.top.lottery.R;
import com.top.lottery.utils.Utils;

import razerdp.basepopup.BasePopupWindow;

public class BubblePopup extends BasePopupWindow {
    public RecyclerView recyclerView;
    private Context context;

    public BubblePopup(Context context) {
        super(context);
        this.context  = context;
        recyclerView = findViewById(R.id.recycle);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.recycle);
    }

    public RecyclerView getContentView() {
        return recyclerView;
    }

    @Override
    protected Animation onCreateShowAnimation() {

        return AnimationUtils.loadAnimation(Utils.context, com.orhanobut.dialogplus.R.anim.slide_in_top);
//        return getShowAnimation();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationUtils.loadAnimation(Utils.context, com.orhanobut.dialogplus.R.anim.slide_out_top);
//        return getDismissAnimation();
    }
}