package com.top.lottery.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.ManageMemberItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/9/16  22:56
 * Email: yanchengdeng@gmail.com
 * Describle: 创建会员成功
 */
public class CreateNewMemberSuccessActivity extends BaseActivity {

    @BindView(R.id.tv_member_id)
    TextView tvMemberId;
    @BindView(R.id.tv_member_type)
    TextView tvMemberType;
    @BindView(R.id.tv_do_setting)
    TextView tvDoSetting;
    private ManageMemberItem manageMemberItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_member_success);
        ButterKnife.bind(this);
        setTitle("新增会员");
        manageMemberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);

        tvMemberId.setText(""+manageMemberItem.uid);
        tvMemberType.setText(""+manageMemberItem.title);

        tvDoSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityUtils.startActivity(getIntent().getExtras(),SetMemberPermissionActivity.class);
                finish();
            }
        });
    }
}
