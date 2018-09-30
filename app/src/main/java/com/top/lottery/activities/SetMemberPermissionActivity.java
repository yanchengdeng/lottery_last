package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.LotteryPermissionAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.MemberPermission;
import com.top.lottery.beans.MemberPermissionItemOptions;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.GridSpacingItemDecoration;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;


/**
 * Author: 邓言诚  Create at : 2018/9/13  23:24
 * Email: yanchengdeng@gmail.com
 * Describle: 设置会员权限
 */
public class SetMemberPermissionActivity extends BaseActivity {

    @BindView(R.id.tv_member_id)
    TextView tvMemberId;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.rb_fenhong)
    RadioButton rbFenhong;
    @BindView(R.id.rb_bufenhong)
    RadioButton rbBufenhong;
    @BindView(R.id.tv_fenhong_tips)
    TextView tvFenhongTips;
    @BindView(R.id.et_reward_scale)
    EditText etRewardScale;
    @BindView(R.id.tv_reward_scale_max)
    TextView tvRewardScaleMax;
    @BindView(R.id.tv_reward_scale_max_tips)
    TextView tvRewardScaleMaxTips;
    @BindView(R.id.ll_reward_scal_ui)
    LinearLayout llRewardScalUi;
    @BindView(R.id.et_bonus_scale)
    EditText etBonusScale;
    @BindView(R.id.tv_bonus_scale_max)
    TextView tvBonusScaleMax;
    @BindView(R.id.tv_bonus_scale_max_tips)
    TextView tvBonusScaleMaxTips;
    @BindView(R.id.ll_bonus_scal_ui)
    LinearLayout llBonusScalUi;
    @BindView(R.id.rb_rebate)
    RadioButton rbRebate;
    @BindView(R.id.rb_not_rebate)
    RadioButton rbNotRebate;
    @BindView(R.id.tv_rebate_tips)
    TextView tvRebateTips;
    @BindView(R.id.et_rebate_scale)
    EditText etRebateScale;
    @BindView(R.id.tv_rebate_scale_max)
    TextView tvRebateScaleMax;
    @BindView(R.id.tv_rebate_scale_max_tips)
    TextView tvRebateScaleMaxTips;
    @BindView(R.id.ll_rebate_scal_ui)
    LinearLayout llRebateScalUi;
    @BindView(R.id.rb_open_member)
    RadioButton rbOpenMember;
    @BindView(R.id.rb_not_open_member)
    RadioButton rbNotOpenMember;
    @BindView(R.id.tv_open_member_tips)
    TextView tvOpenMemberTips;
    @BindView(R.id.rb_deposit)
    RadioButton rbDeposit;
    @BindView(R.id.rb_not_deposit)
    RadioButton rbNotDeposit;
    @BindView(R.id.tv_deposit_tips)
    TextView tvDepositTips;
    @BindView(R.id.rb_purchase)
    RadioButton rbPurchase;
    @BindView(R.id.rb_not_purchase)
    RadioButton rbNotPurchase;
    @BindView(R.id.tv_purchase_tips)
    TextView tvPurchaseTips;
    @BindView(R.id.rb_withdraw)
    RadioButton rbWithdraw;
    @BindView(R.id.rb_not_withdraw)
    RadioButton rbNotWithdraw;
    @BindView(R.id.tv_withdraw_tips)
    TextView tvWithdrawTips;
    @BindView(R.id.rb_rollout)
    RadioButton rbRollout;
    @BindView(R.id.rb_not_rollout)
    RadioButton rbNotRollout;
    @BindView(R.id.tv_rollout_tips)
    TextView tvRolloutTips;
    @BindView(R.id.rb_bonus_rollout)
    RadioButton rbBonusRollout;
    @BindView(R.id.rb_not_bonus_rollout)
    RadioButton rbNotBonusRollout;
    @BindView(R.id.tv_bonus_rollout_tips)
    TextView tvBonusRolloutTips;
    @BindView(R.id.tv_do_add)
    TextView tvDoAdd;
    @BindView(R.id.ll_show_fenhong)
    LinearLayout llShowFenhong;
    @BindView(R.id.ll_show_fanli)
    LinearLayout llShowFanli;
    @BindView(R.id.ll_show_kaihao)
    LinearLayout llShowKaihao;
    @BindView(R.id.ll_show_goucaichongzhi)
    LinearLayout llShowGoucaichongzhi;
    @BindView(R.id.ll_show_goucaigongneng)
    LinearLayout llShowGoucaigongneng;
    @BindView(R.id.ll_show_jifentixian)
    LinearLayout llShowJifentixian;
    @BindView(R.id.ll_show_goucaifanli)
    LinearLayout llShowGoucaifanli;
    @BindView(R.id.ll_show_fenhongzhuanchu)
    LinearLayout llShowFenhongzhuanchu;
    private ManageMemberItem manageMemberItem;

    private MemberPermission memberPermission;
    private LotteryPermissionAdapter lotteryPermissionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_member_permission);
        ButterKnife.bind(this);
        manageMemberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        tvMemberId.setText("" + manageMemberItem.uid);
        setTitle("会员权限设置");
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 2);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        lotteryPermissionAdapter = new LotteryPermissionAdapter(R.layout.adapter_lottery_permission_setting, new ArrayList<MemberPermissionItemOptions>());
        recycle.setLayoutManager(linearLayoutManager);
        recycle.addItemDecoration(new GridSpacingItemDecoration(2, 16, false));

//        mLinearLayoutManager.setSmoothScrollbarEnabled(true);
        recycle.setHasFixedSize(true);
        recycle.setNestedScrollingEnabled(false);
        recycle.setAdapter(lotteryPermissionAdapter);


        getMeberPermissions();


        tvDoAdd.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                checkHasChooseLottery();
            }
        });

    }

    private void checkHasChooseLottery() {
        if (memberPermission != null) {
            boolean hasCheckLottery = false;
            List<String> ids = new ArrayList<>();
            for (MemberPermissionItemOptions item : lotteryPermissionAdapter.getData()) {
                if (item.isSelected) {
                    hasCheckLottery = true;
                    ids.add(item.id);
                }
            }


            if (hasCheckLottery) {
                doSubmitPromissionSetting(ids);

            } else {
                ToastUtils.showShort("请选择彩种权限");
            }
        }
    }


    //设置权限
    private void doSubmitPromissionSetting(List<String> ids) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i == ids.size() - 1) {
                stringBuilder.append(ids.get(i));
            } else {
                stringBuilder.append(ids.get(i)).append(",");
            }
        }


        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("id", manageMemberItem.uid);
        data.put("lottery_list", stringBuilder.toString());
        data.put("bonus", rbFenhong.isChecked() ? "1" : "0");
        if (rbFenhong.isChecked()) {
            data.put("reward_ratio", etRewardScale.getEditableText().toString().trim());
            data.put("bonus_ratio", etBonusScale.getEditableText().toString().trim());
        }

        data.put("rebate", rbRebate.isChecked() ? "1" : "0");
        if (rbRebate.isChecked()) {
            data.put("rebate_ratio", etRebateScale.getEditableText().toString().trim());
        }
        data.put("open_member", rbOpenMember.isChecked() ? "1" : "0");
        data.put("deposit", rbDeposit.isChecked() ? "1" : "0");
        data.put("purchase", rbPurchase.isChecked() ? "1" : "0");
        data.put("withdraw", rbWithdraw.isChecked() ? "1" : "0");
        data.put("rollout", rbRollout.isChecked() ? "1" : "0");
        data.put("bonus_rollout", rbBonusRollout.isChecked() ? "1" : "0");
        OkGo.<LotteryResponse<String[]>>post(Constants.Net.LEADER_SETAUTH)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String[]>> response) {
                        ToastUtils.showShort("" + response.body().msg);

                    }

                    @Override
                    public void onError(Response response) {
                        showError(Utils.toastInfo(response));
                    }
                });
    }


    //获取权限
    private void getMeberPermissions() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("id", manageMemberItem.uid);
        OkGo.<LotteryResponse<MemberPermission>>post(Constants.Net.LEADER_GETAUTHOPTION)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MemberPermission>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MemberPermission>> response) {
                        showContentView();
                        memberPermission = response.body().body;
                        if (memberPermission != null) {
                            intData();
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        showError(Utils.toastInfo(response));
                    }
                });
    }

    private void intData() {
        if (memberPermission.lottery_list != null && memberPermission.lottery_list.option != null) {
            //彩种权限
            if (!TextUtils.isEmpty(memberPermission.lottery_list.value)){
                String[] ids;
                if (memberPermission.lottery_list.value.contains(",")){
                    ids = memberPermission.lottery_list.value.split(",");
                }else{
                    ids = new String[]{memberPermission.lottery_list.value};
                }

                for (MemberPermissionItemOptions item:memberPermission.lottery_list.option){
                    for (String id:ids){
                        if (item.id.equals(id)){
                            item.isSelected = true;
                        }
                    }
                }

            }

            lotteryPermissionAdapter.setNewData(memberPermission.lottery_list.option);
        }

        if (memberPermission.bonus != null) {
            tvFenhongTips.setText("" + memberPermission.bonus.tips);
            rbFenhong.setChecked("1".equals(memberPermission.bonus.value) ? true : false);
            rbBufenhong.setChecked("0".equals(memberPermission.bonus.value) ? true : false);
            if ("0".equals(memberPermission.bonus.value)) {
                llRewardScalUi.setVisibility(View.GONE);
                llBonusScalUi.setVisibility(View.GONE);
                rbFenhong.setChecked(false);
                rbBufenhong.setChecked(true);
            }else{
                rbFenhong.setChecked(true);
                rbBufenhong.setChecked(false);
            }
            rbFenhong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbFenhong.setChecked(isChecked);
                    rbBufenhong.setChecked(!isChecked);
                    if (isChecked) {
                        llRewardScalUi.setVisibility(View.VISIBLE);
                        llBonusScalUi.setVisibility(View.VISIBLE);
                    } else {
                        llRewardScalUi.setVisibility(View.GONE);
                        llBonusScalUi.setVisibility(View.GONE);
                    }
                }
            });

            rbBufenhong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbFenhong.setChecked(!isChecked);
                    rbBufenhong.setChecked(isChecked);
                    if (!isChecked) {
                        llRewardScalUi.setVisibility(View.VISIBLE);
                        llBonusScalUi.setVisibility(View.VISIBLE);
                    } else {
                        llRewardScalUi.setVisibility(View.GONE);
                        llBonusScalUi.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            llShowFenhong.setVisibility(View.GONE);
            llRewardScalUi.setVisibility(View.GONE);
            llBonusScalUi.setVisibility(View.GONE);
        }


        if (memberPermission.bonus_ratio != null) {
            if (!TextUtils.isEmpty(memberPermission.bonus_ratio.tips)) {
                tvBonusScaleMaxTips.setText(memberPermission.bonus_ratio.tips);
            }
        }


        if (memberPermission.reward_ratio != null) {
            tvRewardScaleMax.setText("" + memberPermission.reward_ratio.tips);
        }


        if (memberPermission.rebate != null) {
            tvRebateTips.setText("" + memberPermission.rebate.tips);
            rbRebate.setChecked("1".equals(memberPermission.rebate.value) ? true : false);
            rbNotRebate.setChecked("0".equals(memberPermission.rebate.value) ? true : false);

            if ("0".equals(memberPermission.rebate.value)) {
                llRebateScalUi.setVisibility(View.GONE);
            }else{
                llRebateScalUi.setVisibility(View.VISIBLE);
            }

            rbRebate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbRebate.setChecked(isChecked);
                    rbNotRebate.setChecked(!isChecked);
                    llRebateScalUi.setVisibility(View.VISIBLE);
                }
            });

            rbNotRebate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbRebate.setChecked(!isChecked);
                    rbNotRebate.setChecked(isChecked);
                    llRebateScalUi.setVisibility(View.GONE);
                }
            });
        } else {
            llRebateScalUi.setVisibility(View.GONE);
            llShowFanli.setVisibility(View.GONE);
        }


        if (memberPermission.rebate_ratio != null) {
            tvRebateScaleMax.setText("" + memberPermission.rebate_ratio.tips);
        }


        if (memberPermission.open_member != null) {
            tvOpenMemberTips.setText("" + memberPermission.open_member.tips);
            if ("0".equals(memberPermission.open_member.value)){
                rbOpenMember.setChecked(false);
                rbNotOpenMember.setChecked(true);
            }else{
                rbNotOpenMember.setChecked(false);
                rbOpenMember.setChecked(true);
            }
            rbOpenMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbOpenMember.setChecked(isChecked);
                    rbNotOpenMember.setChecked(!isChecked);

                }
            });
            rbNotOpenMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbNotOpenMember.setChecked(isChecked);
                    rbOpenMember.setChecked(!isChecked);
                }
            });
        }else{
            llShowKaihao.setVisibility(View.GONE);
        }

        if (memberPermission.deposit != null) {
            tvDepositTips.setText("" + memberPermission.deposit.tips);
            if ("0".equals(memberPermission.deposit.value)){
                rbDeposit.setChecked(false);
                rbNotDeposit.setChecked(true);
            }else{
                rbNotDeposit.setChecked(false);
                rbDeposit.setChecked(true);
            }

            rbDeposit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbDeposit.setChecked(isChecked);
                    rbNotDeposit.setChecked(!isChecked);
                }
            });
            rbNotDeposit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbNotDeposit.setChecked(isChecked);
                    rbDeposit.setChecked(!isChecked);
                }
            });
        }else{
            llShowGoucaichongzhi.setVisibility(View.GONE);
        }

        if (memberPermission.purchase != null) {
            tvPurchaseTips.setText("" + memberPermission.purchase.tips);
            if ("0".equals(memberPermission.purchase.value)){
                rbPurchase.setChecked(false);
                rbNotPurchase.setChecked(true);
            }else{
                rbPurchase.setChecked(true);
                rbNotPurchase.setChecked(false);
            }
            rbPurchase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbPurchase.setChecked(isChecked);
                    rbNotDeposit.setChecked(!isChecked);
                }
            });

            rbNotPurchase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbPurchase.setChecked(!isChecked);
                    rbNotPurchase.setChecked(isChecked);
                }
            });

        }else{
            llShowGoucaigongneng.setVisibility(View.GONE);
        }

        if (memberPermission.withdraw != null) {
            tvWithdrawTips.setText("" + memberPermission.withdraw.tips);
            if ("0".equals(memberPermission.withdraw.value)){
                rbWithdraw.setChecked(false);
                rbNotWithdraw.setChecked(true);
            }else{
                rbWithdraw.setChecked(true);
                rbNotWithdraw.setChecked(false);
            }
            rbWithdraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbWithdraw.setChecked(isChecked);
                    rbNotWithdraw.setChecked(!isChecked);
                }
            });

            rbNotWithdraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbWithdraw.setChecked(!isChecked);
                    rbNotWithdraw.setChecked(isChecked);
                }
            });


        }else{
            llShowJifentixian.setVisibility(View.GONE);
        }

        if (memberPermission.rollout != null) {
            tvRolloutTips.setText("" + memberPermission.rollout.tips);
            if ("0".equals(memberPermission.rollout.value)) {
                rbRollout.setChecked(false);
                rbNotRollout.setChecked(true);
            }else{
                rbRollout.setChecked(true);
                rbNotRollout.setChecked(false);
            }

            rbRollout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbRollout.setChecked(isChecked);
                    rbNotRollout.setChecked(!isChecked);
                }
            });

            rbNotRollout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbRollout.setChecked(!isChecked);
                    rbNotRollout.setChecked(isChecked);
                }
            });
        }else{
            llShowFanli.setVisibility(View.GONE);
        }

        if (memberPermission.bonus_rollout != null) {
            tvBonusRolloutTips.setText("" + memberPermission.bonus_rollout.tips);
            if ("0".equals(memberPermission.bonus_rollout.value)) {
                rbBonusRollout.setChecked(false);
                rbNotBonusRollout.setChecked(true);
            }else{
                rbNotBonusRollout.setChecked(false);
                rbBonusRollout.setChecked(true);
            }
            rbBonusRollout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbBonusRollout.setChecked(isChecked);
                    rbNotBonusRollout.setChecked(!isChecked);
                }
            });

            rbNotBonusRollout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbBonusRollout.setChecked(!isChecked);
                    rbNotBonusRollout.setChecked(isChecked);
                }
            });

        }else{
            llShowFenhongzhuanchu.setVisibility(View.GONE);
        }

    }
}
