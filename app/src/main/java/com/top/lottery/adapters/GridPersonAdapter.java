package com.top.lottery.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.top.lottery.R;

public class GridPersonAdapter extends BaseAdapter {
    private Activity mContext;
    private String[] actions;
    private int[] icons;

    public GridPersonAdapter(Activity mContext, String[] actions, int[] icons) {
        this.mContext = mContext;
        this.actions = actions;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return actions.length;
    }

    @Override
    public Object getItem(int i) {
        return actions[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextViewHold viewHold;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_grid_person_centre, null, false);
            viewHold = new TextViewHold();
            viewHold.textView = view.findViewById(R.id.tv_actions);
            view.setTag(viewHold);

        } else {
            viewHold = (TextViewHold) view.getTag();
        }

        viewHold.textView.setText(actions[i]);
        viewHold.textView.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(icons[i]), null, null);

        return view;
    }


    class TextViewHold {

        public TextView textView;
    }
}
