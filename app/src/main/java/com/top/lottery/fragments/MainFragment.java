package com.top.lottery.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.top.lottery.R;
import com.top.lottery.activities.LotteryFunnyActivity;
import com.top.lottery.activities.LotteryFunnyThreeActivity;
import com.top.lottery.adapters.MainLotteryKindsAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotterRecord;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.utils.AutoVerticalScrollTextViewUtil;
import com.top.lottery.utils.GridSpacingItemDecoration;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;
import com.top.lottery.views.AutoVerticalScrollTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * v2.0首页
 */
public class MainFragment extends Fragment {

    private AutoVerticalScrollTextView autoVerticalScrollTextView;
    private RecyclerView recyclerView;
    private MainLotteryKindsAdapter mainLotteryKindsAdapter;
    private SmartRefreshLayout smartRefreshLayout;
    private AutoVerticalScrollTextViewUtil autoVerticalScrollTextViewUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_main,container,false);
       autoVerticalScrollTextView = view.findViewById(R.id.autoVerticalScrollTextView);
       recyclerView = view.findViewById(R.id.recycle);
       smartRefreshLayout = view.findViewById(R.id.refresh);



       smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
           @Override
           public void onRefresh(RefreshLayout refreshLayout) {
               getData();
           }
       });

       smartRefreshLayout.autoRefresh();

       mainLotteryKindsAdapter = new MainLotteryKindsAdapter(R.layout.adapter_grid_lottery_kinds,new ArrayList<LotteryType>());
       recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
       recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,ConvertUtils.dp2px(16),true));
       recyclerView.setAdapter(mainLotteryKindsAdapter);

        mainLotteryKindsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_LOOTERY_TYPE,mainLotteryKindsAdapter.getData().get(position));
                if (mainLotteryKindsAdapter.getData().get(position).lottery_type.equals("1")) {
                    ActivityUtils.startActivity( bundle,LotteryFunnyActivity.class);
                }else if (mainLotteryKindsAdapter.getData().get(position).lottery_type.equals("2")){
                    ActivityUtils.startActivity(bundle,LotteryFunnyThreeActivity.class);
                }
            }
        });

        return view;
    }

    private void getData() {
        getRewardList();
        getLods();

    }

    //获取彩种
    private void getLods() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        OkGo.<LotteryResponse<List<LotteryType>>>post(Constants.Net.LOTTERY_GETLIDS)//
//                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
//                .cacheKey("lottery_kinds")
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotteryType>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotteryType>>> response) {
                        LogUtils.w("dyc",response);
                        smartRefreshLayout.finishRefresh();
                        if (response.body()!=null) {
                            mainLotteryKindsAdapter.setNewData(response.body().body);
                        }
                    }

//                    @Override
//                    public void onCacheSuccess(Response<LotteryResponse<List<LotteryType>>> response) {
//                        super.onCacheSuccess(response);
//                        onSuccess(response);
//                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        smartRefreshLayout.finishRefresh();
                    }
                });
    }

    //获取喜报
    private void getRewardList() {
        if (autoVerticalScrollTextViewUtil!=null){
            autoVerticalScrollTextViewUtil.stop();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        OkGo.<LotteryResponse<List<LotterRecord>>>post(Constants.Net.RECORD_GETREWARDLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotterRecord>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotterRecord>>> response) {
                        LogUtils.w("dyc",response);
                        autoVerticalScrollTextViewUtil = new AutoVerticalScrollTextViewUtil(autoVerticalScrollTextView, getStringData(response.body().body));
                        autoVerticalScrollTextViewUtil.start();

                    }


                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });

    }

    private ArrayList<CharSequence> getStringData(List<LotterRecord> body) {
        ArrayList<CharSequence> strins = new ArrayList<>();


        if (body != null && body.size() > 0) {
            for (int i = 0; i < body.size(); i++) {
                String item = "<p style=\"line-height:100%\">恭喜：<b><font color=\"#ffefbf\">" + "【" + body.get(i).uid + "】" + "</font></b>投" + body.get(i).lid_title + "中" + "<b><font  color=\"#ffefbf\">" + body.get(i).reward_score + "</font></b>分<br/></p>";

               StringBuilder stringBuilder = new StringBuilder();
               stringBuilder.append(item).append(item).append(item);

                strins.add(stringBuilder);
            }
        }
        return strins;
    }
}
