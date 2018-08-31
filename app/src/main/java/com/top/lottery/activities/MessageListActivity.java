package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.MessageAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MessageItem;
import com.top.lottery.beans.MessageListInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/8/30  22:36
 * Email: yanchengdeng@gmail.com
 * Describle: 站内信
 */
public class MessageListActivity extends BaseActivity {

    private int page = 1;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        setTitle("消息中心");
        recyclerView = getView(R.id.recycle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(mContext));
        messageAdapter = new MessageAdapter(R.layout.adapter_message_item, new ArrayList<MessageItem>());
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                messageAdapter.getData().get(position).is_read = 1;
                messageAdapter.notifyItemChanged(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_OBJECT, messageAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle, MessageDetailActivity.class);
            }
        });


        getMessage();
    }

    private void getMessage() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("page", String.valueOf(page));
        OkGo.<LotteryResponse<MessageListInfo>>post(Constants.Net.MESSAGE_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MessageListInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MessageListInfo>> response) {
                        MessageListInfo messageListInfo = response.body().body;
                        if (messageListInfo != null && messageListInfo.list != null && messageListInfo.list.size() > 0) {
                            setData(messageListInfo.list);
                        } else {
                            setLoadMore();
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        setLoadMore();
                    }
                });
    }


    private void setData(List data) {

        boolean isRefresh = page == 1;
        page++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            messageAdapter.setNewData(data);
        } else {
            if (size > 0) {
                messageAdapter.addData(data);
            } else {
                messageAdapter.loadMoreComplete();
                messageAdapter.loadMoreEnd();
            }
        }
        if (size < Constants.PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            messageAdapter.loadMoreEnd(isRefresh);
        } else {
            messageAdapter.loadMoreComplete();
        }
    }

    private void setLoadMore() {
        if (messageAdapter.getData() != null && messageAdapter.getData().size() > 0) {
            messageAdapter.loadMoreComplete();
            messageAdapter.loadMoreEnd();
        } else {
            messageAdapter.setNewData(null);
            messageAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
        }
    }

}
