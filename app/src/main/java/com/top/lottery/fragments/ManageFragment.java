package com.top.lottery.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.activities.AddNewMemberActivity;
import com.top.lottery.activities.ManageActivity;
import com.top.lottery.adapters.MemberManageAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberInfo;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.MemberTabs;
import com.top.lottery.beans.MemberTabsItem;
import com.top.lottery.beans.UseAuth;
import com.top.lottery.events.MemberSuccess;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.top.lottery.utils.Utils.getUserInfo;

public class ManageFragment extends BaseFragment {

    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private RecyclerView recyclerView;
    private TextView tvSearch;
    private EditText etInput;
    private TextView tvAddNewMember;
    private String search_key;
    private int page = 1;
    private MemberTabs memberTabs;
    private MemberManageAdapter memberManageAdapter;
    private View headerView;

    @Override
    public int setContent() {
        return R.layout.fragment_manage_member;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        memberTabs = (MemberTabs) getArguments().getSerializable(Constants.PASS_OBJECT);
        recyclerView = getView(R.id.recycle);
        EventBus.getDefault().register(this);
        tvSearch = getView(R.id.tv_do_search_aciton);
        etInput = getView(R.id.et_input_keys);
        tvAddNewMember = getView(R.id.tv_add_add_new_member);
        UseAuth useAuth = Utils.getAuth();
            if (useAuth.open_member==1){
                tvAddNewMember.setVisibility(View.VISIBLE);
            }

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_manage_member, null);


        memberManageAdapter = new MemberManageAdapter(R.layout.adapter_manage_member, new ArrayList<ManageMemberItem>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(getActivity()));
        memberManageAdapter.addHeaderView(headerView);
        recyclerView.setAdapter(memberManageAdapter);


        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_key = etInput.getEditableText().toString().trim();
                page = 1;
                doSearchMembers();
            }
        });

        //新增会员接口
        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(AddNewMemberActivity.class);
            }
        });


        memberManageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                doSearchMembers();
            }
        },recyclerView);

        // 准备就绪
        mIsPrepared = true;
        /**
         * 因为启动时先走loadData()再走onActivityCreated，
         * 所以此处要额外调用load(),不然最初不会加载内容
         */
        loadData();


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            getUerAuthor();
        }
    }

    //后去用户权限
    private void getUerAuthor() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<UseAuth>>post(Constants.Net.USER_GETAUTH)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UseAuth>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UseAuth>> response) {
                        UseAuth useAuth = response.body().body;
//
                        if (useAuth.open_member==1){
                            tvAddNewMember.setVisibility(View.VISIBLE);
                        }else{
                            tvAddNewMember.setVisibility(View.GONE);
                        }
                        Utils.saveUserAuth(useAuth);

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MemberSuccess event) {
        page=1;
        doSearchMembers();
    }

    private void doSearchMembers() {

        if (getActivity()!=null && !getActivity().isDestroyed()){
           ((ManageActivity) getActivity()).showLoadingBar();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("page", String.valueOf(page));
        if (!TextUtils.isEmpty(search_key)) {
            data.put("search_key", search_key);
        }
        data.put("user_type", "" + memberTabs.user_type);
        OkGo.<LotteryResponse<ManageMemberInfo>>post(Constants.Net.LEADER_GETCHILDLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<ManageMemberInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<ManageMemberInfo>> response) {
                        mIsFirst = false;
                        if (page==1){
                            memberManageAdapter.getData().clear();
                        }
                        if (getActivity()!=null && !getActivity().isDestroyed()){
                            ((ManageActivity) getActivity()).dismissLoadingBar();
                        }
                        showContentView();
                        ManageMemberInfo memberInfo = response.body().body;
                        if (memberInfo != null && memberInfo.list != null && memberInfo.list.size() > 0) {
                            parseDataToMember(memberInfo);
                            boolean isRefresh = page == 1;
                            setData(isRefresh, memberInfo.list );
                        } else {
                            if (memberManageAdapter.getData().size() > 0) {
                                memberManageAdapter.loadMoreComplete();
                                memberManageAdapter.loadMoreEnd();
                            } else {
                                memberManageAdapter.setNewData(new ArrayList<ManageMemberItem>());
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        showContentView();
                        if (getActivity()!=null && !getActivity().isDestroyed()){
                            ((ManageActivity) getActivity()).dismissLoadingBar();
                        }
                        ToastUtils.showShort(Utils.toastInfo(response));
                        mIsFirst = false;
                    }
                });
    }


    private void setData(boolean isRefresh, List data) {
        page++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            memberManageAdapter.setNewData(data);
        } else {
            if (size > 0) {
                memberManageAdapter.addData(data);
            } else {
                memberManageAdapter.loadMoreComplete();
                memberManageAdapter.loadMoreEnd();
            }
        }
        if (size < Constants.PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            memberManageAdapter.loadMoreEnd(isRefresh);
        } else {
            memberManageAdapter.loadMoreComplete();
        }
    }

    private void parseDataToMember(ManageMemberInfo memberInfos) {
        ManageMemberItem manageMemberItem = memberInfos.list.get(0);

        List<MemberTabsItem> selectTabs = new ArrayList<>();
        List<MemberTabs> tabs = memberInfos.tabs;
        if (tabs != null && tabs.size() > 0) {
            for (MemberTabs item : tabs) {
                if (item.user_type == manageMemberItem.user_type) {
                    selectTabs = item.buttons;
                }
            }
        }

        for (ManageMemberItem memberInfo : memberInfos.list) {
            memberInfo.buttons = selectTabs;
        }
    }

    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return;
        }

        doSearchMembers();
    }
}
