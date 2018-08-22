package com.top.lottery.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.top.lottery.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/8/21  22:02
 * Email: yanchengdeng@gmail.com
 * Describle: 购彩记录
 */
public class BuyLotteryRecordActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.btn_change)
    Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_lottery_record);
        ButterKnife.bind(this);


        webview.getSettings().setJavaScriptEnabled(true);
        WebSettings settings = webview.getSettings();
        webview.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 自适应屏幕
        WebSettings webSetting = webview.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webview.addJavascriptInterface(new JavaScriptinterface(this), "android");
        webview.loadUrl("http://api.dadaodata.com/evaluate2/api/Result/getInfo?show_type=1&result_id=11446&from=expert&time=1534863453443&token=51bae2644344e3a152933e76e398a41225f9f2b25b7c1a415e937&sign=9d7a3ce6fe843d8bc793aa35f0f0bfb2");


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.loadUrl("http://api.dadaodata.com/evaluate2/api/Result/getInfo?show_type=1&result_id=11446&from=expert&time=1534863473523&token=51bae2644344e3a152933e76e398a41225f9f2b25b7c1a415e937&sign=1fb828fc59fe555bbadfda7399efe862");
            }
        });
    }
}
