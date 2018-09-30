package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.top.lottery.beans.UserInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/13  23:38
 * Email: yanchengdeng@gmail.com
 * Describle: 信用积分设置
 */
public class MemberCreditSettingActivity extends BaseActivity {

    @BindView(R.id.tv_current_credit_total)
    TextView tvCurrentCreditTotal;
    @BindView(R.id.tv_give_total)
    TextView tvGiveTotal;
    @BindView(R.id.ch_anthor)
    RadioButton chAnthor;
    @BindView(R.id.ch_recycle_back)
    RadioButton chRecycleBack;
    @BindView(R.id.et_autor_id)
    EditText etAutorId;
    @BindView(R.id.et_autor_money)
    EditText etAutorMoney;
    @BindView(R.id.tv_id_credit)
    TextView tvIdCredit;
    @BindView(R.id.tv_id_credit_left)
    TextView tvIdCreditLeft;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    private ManageMemberItem manageMemberItem;
    private String operateType = "19";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_credit_setting);
        ButterKnife.bind(this);
        manageMemberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        setTitle("信用积分设置");


        chAnthor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chAnthor.setChecked(isChecked);
                chRecycleBack.setChecked(!isChecked);
                operateType = "19";
            }
        });

        chRecycleBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chAnthor.setChecked(!isChecked);
                chRecycleBack.setChecked(isChecked);
                operateType = "20";
            }
        });

        etAutorId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(etAutorId.getEditableText().toString())) {
                    if (!hasFocus) {
                        if (etAutorId.getEditableText().toString().length() > 7 && etAutorId.getEditableText().toString().length() < 12) {
                            findUserInfo(etAutorId.getEditableText().toString());
                        } else {
                            tvIdCredit.setText("该ID目前信用分额度：暂无");
                            tvIdCreditLeft.setText("该ID目前信用分余额：暂无");
                        }
                    }
                }
            }
        });

        tvCurrentCreditTotal.setText("您当前的信用分额度：" + Utils.getUserInfo().credit_line_score);
        tvGiveTotal.setText("可授权额度：" + Utils.getUserInfo().credit_balance_score);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etAutorId.getEditableText().toString())) {
                    ToastUtils.showShort("请输入ID");
                    return;
                }
                if (etAutorId.getEditableText().toString().length() < 8 || etAutorId.getEditableText().toString().length() > 11) {
                    ToastUtils.showShort("ID是8~11位");
                    return;
                }

                if (TextUtils.isEmpty(etAutorMoney.getEditableText().toString())){
                    ToastUtils.showShort("输入金额");
                    return;
                }

                if (memberDetail != null) {
                        doRechargeMoney();
                } else {
                    ToastUtils.showShort("暂无该会员ID信息");
                }
            }
        });

    }

    MemberDetail memberDetail;

    //查找用户信息
    private void findUserInfo(String uid) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", manageMemberItem.uid);
        data.put("member_uid", uid);
        OkGo.<LotteryResponse<MemberDetail>>post(Constants.Net.LEADER_GETUSERINFO)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MemberDetail>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MemberDetail>> response) {
                        memberDetail = response.body().body;
                        if (memberDetail != null) {
                            tvIdCredit.setText("该ID目前信用分额度：" + memberDetail.credit_line_score);
                            tvIdCreditLeft.setText("该ID目前信用分余额：" + memberDetail.credit_balance_score);
                        } else {
                            tvIdCredit.setText("该ID目前信用分额度：暂无");
                            tvIdCreditLeft.setText("该ID目前信用分余额：暂无");
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        tvIdCredit.setText("该ID目前信用分额度：暂无");
                        tvIdCreditLeft.setText("该ID目前信用分余额：暂无");
                    }
                });
    }


    //1：给会员充值，
    //3：给会员提现，
    //19：给会员信用分授权，
    //21：会员信用分收回
    private void doRechargeMoney() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("member_uid", etAutorId.getEditableText().toString().trim());
        data.put("score", etAutorMoney.getEditableText().toString().trim());
        data.put("operate_type", operateType);
        OkGo.<LotteryResponse<String[]>>post(Constants.Net.LEADER_CHILDSCOREOPERATE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String[]>> response) {
                        if (response.body().code == 1) {
                            UserInfo userInfo = Utils.getUserInfo();
                            Float  myBalance =   Float.parseFloat(userInfo.credit_balance_score);
                            Float num = Float.parseFloat(etAutorMoney.getEditableText().toString().trim());
                            if (operateType.equals("19")){
                              userInfo.credit_balance_score =String.valueOf( myBalance-num);
                            }

                            if (operateType.equals("20")){
                                userInfo.credit_balance_score =String.valueOf( myBalance+num);
                            }

                            Utils.saveUserInfo(userInfo);

                            tvCurrentCreditTotal.setText("您当前的信用分额度：" + Utils.getUserInfo().credit_line_score);
                            tvGiveTotal.setText("可授权额度：" + Utils.getUserInfo().credit_balance_score);

                        }
                        ToastUtils.showShort("" + response.body().msg);
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }

                });
    }
}
