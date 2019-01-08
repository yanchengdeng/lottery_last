package com.top.lottery.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.top.lottery.R;


/**
*
* Author: 邓言诚  Create at : 2018/7/6  11:49
* Email: yanchengdeng@gmail.com
* Describle: RecycleView 工具
*/
public class RecycleViewUtils {


    //1个dp 横线
    public static DividerItemDecoration getItemDecoration(Context context) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_line_diver));
        return dividerItemDecoration;
    }


    //1个dp 横线
    public static DividerItemDecoration getItemDecorationDeep(Context context) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_line_diver_deep));
        return dividerItemDecoration;
    }


    //1个dp 横线
    public static DividerItemDecoration getItemDecorationHorizontal(Context context) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_line_diver));
        return dividerItemDecoration;
    }


    //九宫格 间隔
    public static DividerItemDecoration getItemDecorationGrid(Context context) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.gridview_line_transe));
        return dividerItemDecoration;
    }


    //一个dp 横线
    public static RecyclerView.ItemDecoration getItemDecorationLine(Context context) {
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_line_diver));
        return getNoBottomLineDecoration(context);
    }


    public static RecyclerView.ItemDecoration getNoBottomLineDecoration(Context context) {
        MultiItemDivider itemDivider = new MultiItemDivider(context, MultiItemDivider.VERTICAL_LIST, R.drawable.list_line_diver);
        itemDivider.setDividerMode(MultiItemDivider.INSIDE);//最后一个item下没有分割线
//        itemDivider.setDividerMode(MultiItemDivider.END);//最后一个item下有分割线
//        itemDivider.addExcepts(itemList.size()-1,itemList.size()-2);//取消倒数第一第二个分割线
        return itemDivider;
    }

    //RecycleView  无数据提示
    public static View getEmptyView(Activity mContext, RecyclerView recycle) {
        if (mContext==null){
            return  recycle.getChildAt(-1);
        }
        return mContext.getLayoutInflater().inflate(R.layout.layout_empty_view, (ViewGroup) recycle.getParent(), false);
    }

    public static View getEmptyView(Activity mContext, RecyclerView recycle, String tips) {
        if (mContext==null){
            return  recycle.getChildAt(-1);
        }
        View view = mContext.getLayoutInflater().inflate(R.layout.layout_empty_view, (ViewGroup) recycle.getParent(), false);
        TextView tvTips = view.findViewById(R.id.tv_error_tips);
        tvTips.setText(tips);
        return view;
    }
}
