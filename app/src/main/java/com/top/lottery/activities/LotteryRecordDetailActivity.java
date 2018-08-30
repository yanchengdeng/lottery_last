package com.top.lottery.activities;

import android.os.Bundle;

import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardRecordItem;

/**
*
* Author: 邓言诚  Create at : 2018/8/30  10:25
* Email: yanchengdeng@gmail.com
* Describle: 投注记录详情
*/
public class LotteryRecordDetailActivity extends BaseActivity {

    private AwardRecordItem awardRecordItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_record_detail);
        setTitle("投注记录详情");
        awardRecordItem = (AwardRecordItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
    }
}
