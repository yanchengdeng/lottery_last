package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.UserInfo;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/8/30  21:51
 * Email: yanchengdeng@gmail.com
 * Describle: 积分出
 */
public class IntergrayRollOutActivity extends BaseActivity {


    @BindView(R.id.tv_intergray)
    TextView tvIntergray;
    @BindView(R.id.et_intergray_rollout)
    EditText etIntergrayRollout;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    private float dailijf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intergray_roll_out);
        ButterKnife.bind(this);
        setTitle("转出返利积分");
        Bundle bundle = getIntent().getExtras();
        dailijf =   bundle.getFloat(Constants.PASS_STRING);


        tvIntergray.setText("" + dailijf);

        tvConfirm.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                doRollOut(etIntergrayRollout.getEditableText().toString().trim());
            }
        });

    }

    private void doRollOut(final String intergay) {
        if (TextUtils.isEmpty(intergay)) {
            ToastUtils.showShort("请输入转出积分");
            return;
        }

        if (intergay.startsWith("0")) {
            ToastUtils.showShort("请输入整数");
            return;
        }


        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("score", intergay);
        OkGo.<LotteryResponse<UserInfo[]>>post(Constants.Net.USER_ROLLOUT)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo[]>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo[]>> response) {
                        ToastUtils.showShort("" + response.body().msg);
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }
}
