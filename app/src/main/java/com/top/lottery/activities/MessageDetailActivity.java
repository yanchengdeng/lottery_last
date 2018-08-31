package com.top.lottery.activities;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MessageItem;
import com.top.lottery.beans.MessageListInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/8/30  23:08
 * Email: yanchengdeng@gmail.com
 * Describle: 消息详情
 */
public class MessageDetailActivity extends BaseActivity {

    @BindView(R.id.tv_info_title)
    TextView tvInfoTitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_content)
    TextView tvContent;
    private MessageItem messageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        ButterKnife.bind(this);
        setTitle("消息详情");
        messageItem = (MessageItem) getIntent().getExtras().getSerializable(Constants.PASS_OBJECT);

        tvInfoTitle.setText("" + messageItem.title);
        tvDate.setText("" + messageItem.create_time);
        tvContent.setText("" + Html.fromHtml(messageItem.content));

        getDetail();


    }

    private void getDetail() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("id", messageItem.id);
        OkGo.<LotteryResponse<MessageListInfo>>post(Constants.Net.MESSAGE_DETAIL)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MessageListInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MessageListInfo>> response) {
                        LogUtils.w("dyc", response + "");
                    }

                    @Override
                    public void onError(Response response) {
                        LogUtils.w("dyc", response + "");
                    }
                });
    }


}
