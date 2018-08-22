package com.top.lottery.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.top.lottery.R;

/**
*
* Author: 邓言诚  Create at : 2018/8/23  01:18
* Email: yanchengdeng@gmail.com
* Describle: 任意选
*/
public class LotteryFunnyAnySelectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view  = inflater.inflate(R.layout.fragment_lottery_funny_type,container,false);
        return view;
    }
}
