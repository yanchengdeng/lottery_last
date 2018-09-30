package com.top.lottery.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.ManageMemberItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: 邓言诚  Create at : 2018/9/13  23:29
 * Email: yanchengdeng@gmail.com
 * Describle: 会员设置
 */
public class MemberSettingActivity extends BaseActivity {

    @BindView(R.id.tv_settin_base)
    TextView tvSettinBase;
    @BindView(R.id.tv_settin_permission)
    TextView tvSettinPermission;
    @BindView(R.id.tv_settin_credit)
    TextView tvSettinCredit;

    private ManageMemberItem manageMemberItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_setting);
        ButterKnife.bind(this);

        setTitle("设置");
        manageMemberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);

        if (manageMemberItem!=null ){
            if (manageMemberItem.user_type==1){
                tvSettinCredit.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.tv_settin_base, R.id.tv_settin_permission, R.id.tv_settin_credit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_settin_base:
                ActivityUtils.startActivity(getIntent().getExtras(), MemberBaseSettingActivity.class);
                break;
            case R.id.tv_settin_permission:
                ActivityUtils.startActivity(getIntent().getExtras(), SetMemberPermissionActivity.class);
                break;
            case R.id.tv_settin_credit:
                ActivityUtils.startActivity(getIntent().getExtras(), MemberCreditSettingActivity.class);
                break;
        }
    }
}
