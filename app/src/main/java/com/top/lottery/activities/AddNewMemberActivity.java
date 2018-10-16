package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.MemberChooseAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.NewAddMember;
import com.top.lottery.beans.NewAddMemberTypeOptions;
import com.top.lottery.beans.NewAddMemberTypeOptionsChild;
import com.top.lottery.events.MemberSuccess;
import com.top.lottery.utils.GridSpacingItemDecoration;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.top.lottery.utils.Utils.getUserInfo;

//新增会员接口
public class AddNewMemberActivity extends BaseActivity {


    @BindView(R.id.et_member_id)
    EditText etMemberId;
    @BindView(R.id.et_member_nickname)
    EditText etMemberNickname;
    @BindView(R.id.et_member_remark)
    EditText etMemberRemark;
    @BindView(R.id.et_member_phone)
    EditText etMemberPhone;
    @BindView(R.id.et_member_password)
    EditText etMemberPassword;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_above_clsass)
    TextView tvAboveClsass;
    @BindView(R.id.tv_do_add)
    TextView tvDoAdd;
    //会员   店主   区域
    private OptionsPickerView<NewAddMemberTypeOptionsChild> optionsPickerViewMember;
    List<NewAddMemberTypeOptionsChild> optionData = new ArrayList<>();
    private List<List<NewAddMemberTypeOptionsChild>> optionData1 = new ArrayList<>();
    private MemberChooseAdapter memberChooseAdapter;
    private String user_type, use_type_name, p_uid;
    private NewAddMember newAddMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        ButterKnife.bind(this);
        setTitle("新增会员");

        recycle.setLayoutManager(new GridLayoutManager(mContext, 3));
        recycle.addItemDecoration(new GridSpacingItemDecoration(10, 3, false));
        memberChooseAdapter = new MemberChooseAdapter(R.layout.adapter_member_add_choose, new ArrayList<NewAddMemberTypeOptions>());
        recycle.setAdapter(memberChooseAdapter);


        optionsPickerViewMember = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if (optionData1 != null && optionData1.size() > 0) {
                    tvAboveClsass.setText("" + optionData1.get(options1).get(option2).uid);
                } else if (optionData != null && optionData.size() > 0) {
                    tvAboveClsass.setText("" + optionData.get(options1).uid);
                }

            }
        }).build();

        getUserOption();

        memberChooseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });


        memberChooseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (NewAddMemberTypeOptions item : memberChooseAdapter.getData()) {
                    item.isSelected = false;
                }
                memberChooseAdapter.getData().get(position).isSelected = true;
                user_type = memberChooseAdapter.getData().get(position).id;
                use_type_name = memberChooseAdapter.getData().get(position).title;
                memberChooseAdapter.notifyDataSetChanged();
                if (!TextUtils.isEmpty(memberChooseAdapter.getData().get(position).id)) {
                    initOptionsPickerViewMember(memberChooseAdapter.getData().get(position).id);
                }
            }
        });

    }

    //初始化会员选择
    private void initOptionsPickerViewMember(String id) {
        for (NewAddMemberTypeOptions item : newAddMember.user_type.option) {
            if (id.equals(item.id)) {
                optionData = item.option;
            }
        }


        if (optionData != null && optionData.size() > 0 && optionData.get(0) != null && optionData.get(0).child != null) {
            for (NewAddMemberTypeOptionsChild itemChild : optionData) {
                optionData1.add(itemChild.child);
            }
            optionsPickerViewMember.setPicker(optionData, optionData1);
        } else if (optionData.size() > 0) {
            optionsPickerViewMember.setPicker(optionData);
            optionData1 = new ArrayList<>();
        }


    }

    private void getUserOption() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("operate", "add");
        OkGo.<LotteryResponse<NewAddMember>>post(Constants.Net.LEADER_GETUSEROPTION)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<NewAddMember>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<NewAddMember>> response) {

                        newAddMember = response.body().body;
                        if (newAddMember != null) {
                            initData();
                        } else {
                            showError("" + response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        showError(Utils.toastInfo(response));
                    }
                });
    }

    private void initData() {
        if (newAddMember.id != null && !TextUtils.isEmpty(newAddMember.id.value)) {
            etMemberId.setText(newAddMember.id.value);
        }


        if (newAddMember.user_type != null && newAddMember.user_type.option != null) {
            memberChooseAdapter.setNewData(newAddMember.user_type.option);
        }


    }

    @OnClick({R.id.tv_above_clsass, R.id.tv_do_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_above_clsass:
                if (optionsPickerViewMember != null && optionData != null && optionData.size() > 0) {
                    if (!optionsPickerViewMember.isShowing()) {
                        optionsPickerViewMember.show();
                    } else {
                        optionsPickerViewMember.dismiss();
                    }
                } else {
                    ToastUtils.showShort("请选择用户类型");
                }
                break;
            case R.id.tv_do_add:
                doAdMember();
                break;
        }
    }

    private void doAdMember() {
        if (TextUtils.isEmpty(etMemberId.getEditableText().toString())) {
            ToastUtils.showShort("请输入用户ID");
            return;
        }


//        if (TextUtils.isEmpty(etMemberPhone.getEditableText().toString())) {
//            ToastUtils.showShort("请输入手机号");
//            return;
//        }

//        if (!RegexUtils.isMobileSimple(etMemberPhone.getEditableText().toString().trim())) {
//            ToastUtils.showShort("请输入正确手机号");
//        }

        if (TextUtils.isEmpty(etMemberPassword.getEditableText().toString())) {
            ToastUtils.showShort("请输入登录密码");
            return;
        }

        if (TextUtils.isEmpty(user_type)) {
            ToastUtils.showShort("请选择用户类型");
            return;
        }

        p_uid = tvAboveClsass.getText().toString();
        if (TextUtils.isEmpty(p_uid)) {
            ToastUtils.showShort("请选择上级ID");
            return;
        }


        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("id", etMemberId.getEditableText().toString().trim());
        data.put("password", etMemberPassword.getEditableText().toString().trim());
        if (RegexUtils.isMobileSimple(etMemberPhone.getEditableText().toString().trim())) {
            data.put("mobile", etMemberPhone.getEditableText().toString().trim());
        }
        data.put("user_type", user_type);
        data.put("p_uid", p_uid);
        if (!TextUtils.isEmpty(etMemberNickname.getEditableText().toString().trim())) {
            data.put("nickname", etMemberNickname.getEditableText().toString().trim());
        }
        if (!TextUtils.isEmpty(etMemberRemark.getEditableText().toString().trim())) {
            data.put("remark", etMemberRemark.getEditableText().toString().trim());
        }
        OkGo.<LotteryResponse<ManageMemberItem>>post(Constants.Net.LEADER_ADDUSER)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<ManageMemberItem>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<ManageMemberItem>> response) {
                        if (response.body().code == 1) {
                            ManageMemberItem manageMemberItem = response.body().body;
                            manageMemberItem.user_type = Integer.parseInt(user_type);
                            manageMemberItem.title = use_type_name;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.PASS_OBJECT, manageMemberItem);
                            EventBus.getDefault().post(new MemberSuccess());
                            ActivityUtils.startActivity(bundle, CreateNewMemberSuccessActivity.class);
                            finish();
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
