package com.top.lottery.utils;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultiItemDivider extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable mDivider;
    private int mOrientation;


    //    public final static int START = 0x02;//第一个item顶部无偏移，没有实现第一个上方分割线
    public final static int INSIDE = 0x00;
    public final static int END = 0x01;

    private int dividerMode = INSIDE;
    private boolean clipChild;

    public MultiItemDivider(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);
        init(orientation, divider);
        a.recycle();
    }

    public MultiItemDivider(Context context, int orientation, int drawableId) {
        Drawable divider = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            divider = context.getDrawable(drawableId);
        } else {
            divider = context.getResources().getDrawable(drawableId);
        }
        init(orientation, divider);
    }

    public MultiItemDivider(Context context, int orientation, Drawable divider) {
        init(orientation, divider);
    }

    private void init(int orientation, Drawable divider) {
        mDivider = divider;
        exceptList = new ArrayList<>();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        if (mOrientation == HORIZONTAL_LIST) {//以recyclerView的布局设置为标准
            drawHorizontal(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    /**
     * 获取分割线模式
     * @return {@link #INSIDE },{@link #END}
     */
    public int getDividerMode() {
        return dividerMode;
    }
    /**
     * 设置分割线模式
     * @param dividerMode {@link #INSIDE } or {@link #END}
     */
    public void setDividerMode(int dividerMode) {
        this.dividerMode = dividerMode;
    }

    /**
     * 是否卷进padding中
     * @param clipChild
     */
    public void clipToChildPadding(boolean clipChild) {
        this.clipChild = clipChild;
    }

    /**
     * 垂直方向分割线（横线）收卷到子view的计算
     * @param child
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void clipChildVertical(View child, int left, int top, int right, int bottom) {
        //同上
        if (clipChild) {
            int l = left + child.getPaddingLeft();
            int r = right - child.getPaddingRight();
            int t = top - child.getPaddingBottom();
            int b = bottom - child.getPaddingBottom();
            mDivider.setBounds(l, t, r, b);
        } else {
            mDivider.setBounds(left, top, right, bottom);
        }
    }

    /**
     * 绘制垂直方向分割线（横线）
     * @param c
     * @param parent
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();

        Object layout = parent.getLayoutManager();
        LinearLayoutManager lin = layout instanceof LinearLayoutManager ? ((LinearLayoutManager) layout) : null;

        //下边画线
        if (lin != null && lin.getOrientation() == LinearLayoutManager.HORIZONTAL && dividerMode == END) {
            View child = parent.getChildAt(0);
            if (child == null || exceptList.contains(0)) {

                return;
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + getDividerOffH(parent,child);
            clipChildVertical(child, left, top, right, bottom);
            mDivider.draw(c);
            return;
        }


        for (int i = 0; i < childCount; i++) {

            final View child = parent.getChildAt(i);


            int position = parent.getChildAdapterPosition(child);
            if (child == null || exceptList.contains(position)) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

//            if (dividerMode == START) {
//                final int bottom = child.getTop() + params.bottomMargin;
//                final int top = bottom - mDivider.getIntrinsicHeight();
//                ;
//
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);


            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + getDividerOffH(parent,child);
            if (dividerMode == END) {
                clipChildVertical(child, left, top, right, bottom);
                mDivider.draw(c);
            } else {//默认绘制 INSIDE
                if (i < childCount - 1) {
                    clipChildVertical(child, left, top, right, bottom);
                    mDivider.draw(c);
                }
            }


        }
    }

    private List<Integer> exceptList;

    /**
     * 添加排除绘制
     * @param index 排除绘制的分割线的索引下标
     */
    public void addExcept(int index) {
        if (exceptList == null) {
            exceptList = new ArrayList<>();
        }
        exceptList.add(index);
    }

    /**
     * 添加排除绘制
     * @param indexList 排除绘制的分割线的索引下标数组
     */
    public void addExcepts(int... indexList) {
        if (indexList == null) {
            return;
        }
        if (exceptList == null) {
            exceptList = new ArrayList<>();
        }
        for (int i = 0; i < indexList.length; i++) {
            exceptList.add(indexList[i]);
        }
    }

    /**
     * 水平方向分割线（竖线）收卷到子view的计算
     * @param child
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void clipHorizontal(View child, int left, int top, int right, int bottom) {

        if (clipChild) {
            int l = left - child.getPaddingRight();
            int r = right - child.getPaddingRight();
            int t = top + child.getPaddingTop();
            int b = bottom - child.getPaddingBottom();
            mDivider.setBounds(l, t, r, b);
        } else {
            mDivider.setBounds(left, top, right, bottom);
        }
    }

    /**
     * 绘制水平方向分割线（竖线）
     * @param c      画布
     * @param parent RecyclerView
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();

        Object layout = parent.getLayoutManager();
        LinearLayoutManager lin = layout instanceof LinearLayoutManager ? ((LinearLayoutManager) layout) : null;

        //右边画线
        if (lin != null && lin.getOrientation() == LinearLayoutManager.VERTICAL && dividerMode == END) {
            View child = parent.getChildAt(0);
            if (child == null || exceptList.contains(0)) {
                return;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + getDividerOffW(parent, child);
            clipHorizontal(child, left, top, right, bottom);
            mDivider.draw(c);
            return;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (child == null || exceptList.contains(position)) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//            if (dividerMode == START) {
//                final int right = child.getRight() + params.rightMargin;
//                final int left = right - mDivider.getIntrinsicHeight();
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);

            final int left = child.getRight() + params.rightMargin;
            final int right = left + getDividerOffW(parent, child);

            if (dividerMode == END) {
                clipHorizontal(child, left, top, right, bottom);
                mDivider.draw(c);

            } else {//(dividerMode == INSIDE) 和其它值
                if (i < childCount - 1) {
                    clipHorizontal(child, left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mOrientation == HORIZONTAL_LIST) {
            outRect.set(0, 0, getDividerOffW(parent,view), 0);
        } else {
            outRect.set(0, 0, 0, getDividerOffH(parent,view));
        }
    }

    private int getDividerOffH(RecyclerView parent,View child) {
        int h = mDivider.getIntrinsicHeight();
        if (h == -1) {
            h = 0;
        }
        if (cleanOffset){
            int position = parent.getChildLayoutPosition(child);
            if (exceptList.contains(position)){
                h = 0;
            }
        }
        return h;
    }

    private int getDividerOffW(RecyclerView parent, View child) {
        int w = mDivider.getIntrinsicWidth();
        if (w == -1) {
            w = 0;
        }
        if (cleanOffset){
            int position = parent.getChildLayoutPosition(child);
            if (exceptList.contains(position)){
                w = 0;
            }
        }
        return w;
    }

    private boolean cleanOffset = true;
    /**
     * 清理未使用的item 装饰，默认清理。offset 位移，装饰view
     * @param c
     */
    public void cleanBlankOffset(boolean c) {
        this.cleanOffset = c;
    }

    /**
     *  清理不绘制列表
     */
    public void clearExpects(){
        if (exceptList!=null){
            exceptList.clear();
        }
    }
}
