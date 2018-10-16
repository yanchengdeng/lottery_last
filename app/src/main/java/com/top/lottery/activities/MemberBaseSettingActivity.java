package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.MemberDetail;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

public class MemberBaseSettingActivity extends BaseActivity {

    @BindView(R.id.tv_member_id)
    TextView tvMemberId;
    @BindView(R.id.tv_member_type)
    TextView tvMemberType;
    @BindView(R.id.tv_member_nickname)
    TextView tvMemberNickname;
    @BindView(R.id.et_member_remark)
    EditText etMemberRemark;
    @BindView(R.id.et_member_phone)
    EditText etMemberPhone;
    @BindView(R.id.tv_do_setting)
    TextView tvDoSetting;
    private ManageMemberItem memberItem;
    private MemberDetail memberDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_base_setting);
        ButterKnife.bind(this);
        setTitle("基础设置");
        memberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);

        //1：为会员
        //2：店家
        //3：区代
        //4：大代
        tvMemberId.setText("" + memberItem.uid);

        tvDoSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doModifyActions();
            }
        });

        getMemberDetail();

    }

    private void getMemberDetail() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("member_uid", memberItem.uid);
        OkGo.<LotteryResponse<MemberDetail>>post(Constants.Net.LEADER_GETUSERINFO)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MemberDetail>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MemberDetail>> response) {
                        memberDetail = response.body().body;
                        if (memberDetail != null) {
                            tvMemberType.setText("" + memberDetail.user_type_title);
                            tvMemberNickname.setText("" + memberDetail.nickname);
                            etMemberPhone.setText("" + memberDetail.mobile);
                            if (!TextUtils.isEmpty(memberDetail.remark)) {
                                etMemberRemark.setText(memberDetail.remark);
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        showError(Utils.toastInfo(response));
                    }
                });
    }

    private void doModifyActions() {

//        if (TextUtils.isEmpty(etMemberPhone.getEditableText().toString().trim())) {
//            ToastUtils.showShort("请输入手机号");
//            return;
//        }

        if (!TextUtils.isEmpty(etMemberPhone.getEditableText().toString())) {
            if (!RegexUtils.isMobileSimple(etMemberPhone.getEditableText().toString().trim())) {
                ToastUtils.showShort("请输入正确手机号");
                return;
            }
        }


        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("id", memberItem.uid);
        if (!TextUtils.isEmpty(etMemberRemark.getEditableText().toString().trim())) {
            data.put("remark", etMemberRemark.getEditableText().toString().trim());
        }

        if (!TextUtils.isEmpty(etMemberPhone.getEditableText().toString())) {
            data.put("mobile", etMemberPhone.getEditableText().toString());
        }
        OkGo.<LotteryResponse<String[]>>post(Constants.Net.LEADER_SETUSER)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String[]>> response) {

                        ToastUtils.showShort("" + response.body().msg);
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }
}
