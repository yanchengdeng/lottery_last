package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.MemberDetail;
import com.top.lottery.beans.UseAuth;
import com.top.lottery.beans.UserInfo;
import com.top.lottery.events.MemberSuccess;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

//充值
public class RechargeWithdrawActivity extends BaseActivity {

    @BindView(R.id.tv_user_ID)
    TextView tvUserID;
    @BindView(R.id.tv_user_intergray)
    TextView tvUserIntergray;
    @BindView(R.id.et_user_integry_withdraw)
    EditText etUserIntegryWithdraw;
    @BindView(R.id.tv_manage_interger)
    TextView tvManageInterger;
    @BindView(R.id.tv_corfirm)
    TextView tvCorfirm;
    @BindView(R.id.tv_input_tips)
    TextView tvInputTips;
    @BindView(R.id.rl_manage_ui)
    RelativeLayout rlManageUi;
    @BindView(R.id.rb_intergary_account)
    RadioButton rbIntergaryAccount;
    @BindView(R.id.rb_creidt_account)
    RadioButton rbCreidtAccount;
    @BindView(R.id.ll_recharge_ui)
    LinearLayout llRechargeUi;
    private ManageMemberItem manageMemberItem;
    private String operate_type = "1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_withdraw);
        ButterKnife.bind(this);
        manageMemberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        setTitle("" + manageMemberItem.title);

        tvUserID.setText("" + manageMemberItem.uid);

        tvUserIntergray.setText("" + manageMemberItem.score);
        tvManageInterger.setText("" + Utils.getUserInfo().score);
        rbIntergaryAccount.setText("可用积分账户余额："+ Utils.getUserInfo().score);
        rbCreidtAccount.setText("信用积分账户余额："+Utils.getUserInfo().credit_balance_score);


        rbIntergaryAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rbIntergaryAccount.setChecked(isChecked);
                rbCreidtAccount.setChecked(!isChecked);
                operate_type="1";
            }
        });

        rbCreidtAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rbCreidtAccount.setChecked(isChecked);
                rbIntergaryAccount.setChecked(!isChecked);
                operate_type="22";
            }
        });


        tvCorfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doConfirmAction(etUserIntegryWithdraw.getEditableText().toString());
            }
        });

        if (!TextUtils.isEmpty(manageMemberItem.type)) {
            if (manageMemberItem.type.equals("deposit")) {
                //充值
                tvInputTips.setText("充值金额：");
                etUserIntegryWithdraw.setHint("请输入充值金额");
                rlManageUi.setVisibility(View.GONE);
                llRechargeUi.setVisibility(View.VISIBLE);

            } else if (manageMemberItem.type.equals("withdraw")) {
                //取现
                rlManageUi.setVisibility(View.VISIBLE);
                llRechargeUi.setVisibility(View.GONE);

            }
        }
    }


    private void doConfirmAction(String money) {
        if (TextUtils.isEmpty(money)) {
            ToastUtils.showShort("请输入金额");
        }

        if (money.startsWith("0")) {
            ToastUtils.showShort("请输入整数");
            return;
        }

        if (!TextUtils.isEmpty(manageMemberItem.type)) {
            if (manageMemberItem.type.equals("deposit")) {
                //充值
                doRechargeMoney();
            } else if (manageMemberItem.type.equals("withdraw")) {
                //取现
                operate_type = "3";
                doWithDrawMoney();

            }
        }

    }

    //1：给会员充值，
    //3：给会员提现，
    //19：给会员信用分授权，
    //21：会员信用分收回
    //22 给会员充值时扣除自身信用余额
    private void doRechargeMoney() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("member_uid", manageMemberItem.uid);
        data.put("score", etUserIntegryWithdraw.getEditableText().toString().trim());
        data.put("operate_type", operate_type);
        OkGo.<LotteryResponse<UseAuth[]>>post(Constants.Net.LEADER_CHILDSCOREOPERATE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UseAuth[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UseAuth[]>> response) {
                        if (response.body().code == 1) {
                            etUserIntegryWithdraw.setText("");
                            doGetMemberDetail();
                            getMemberDetailSub();
                        }
                        ToastUtils.showShort("" + response.body().msg);
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


    //1：给会员充值，
    //3：给会员提现，
    //19：给会员信用分授权，
    //21：会员信用分收回
    private void doWithDrawMoney() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("operate_type", operate_type);
        data.put("member_uid", manageMemberItem.uid);
        data.put("score", etUserIntegryWithdraw.getEditableText().toString().trim());
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.LEADER_CHILDSCOREOPERATE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        if (response.body().code == 1) {
                            etUserIntegryWithdraw.setText("");
                            doGetMemberDetail();
                            getMemberDetailSub();
                        }
                        ToastUtils.showShort("" + response.body().msg);
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    //获取子会员信息
    private void getMemberDetailSub() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("member_uid", manageMemberItem.uid);
        OkGo.<LotteryResponse<MemberDetail>>post(Constants.Net.LEADER_GETUSERINFO)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MemberDetail>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MemberDetail>> response) {
                        MemberDetail   memberDetail = response.body().body;
                        if (memberDetail != null) {
                            tvUserIntergray.setText("" + memberDetail.score);

                        }
                    }

                    @Override
                    public void onError(Response response) {
                    }
                });
    }

    private void doGetMemberDetail() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UserInfo>>post(Constants.Net.USER_DETAIL)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo>> response) {
                        UserInfo    userInfo = response.body().body;
                        if (userInfo != null) {
                            tvUserIntergray.setText("" + manageMemberItem.score);
                            tvManageInterger.setText("" + userInfo.score);
                            rbIntergaryAccount.setText("可用积分账户余额："+ userInfo.score);
                            rbCreidtAccount.setText("信用积分账户余额："+userInfo.credit_balance_score);
                            Utils.saveUserInfo(userInfo);
                            EventBus.getDefault().post(new MemberSuccess());
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


}
