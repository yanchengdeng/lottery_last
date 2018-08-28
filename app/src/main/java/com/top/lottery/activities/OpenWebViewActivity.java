package com.top.lottery.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.top.lottery.R;
import com.top.lottery.base.Constants;

//打开网页
public class OpenWebViewActivity extends BaseActivity {

    private String name;
    private String url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web_view);

        webView = getView(R.id.webview);
        name = getIntent().getStringExtra(Constants.PASS_NAME);
        url = getIntent().getStringExtra(Constants.PASS_STRING);
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
        }
        initwebview();
//        url="https://www.baidu.com";
//        url="http://game.dadaozx.com/graph/index.html?token=f1ff2028832000c26da416ee511e591b7a9890445afa622b1da3b&lesson_id=654&content_id=2976&user_id=26462";
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl("file:///android_asset/"+url);
        }
    }

    private void initwebview() {
        WebSettings webSettings = webView.getSettings();
        // 让WebView能够执行javaScript
        webSettings.setJavaScriptEnabled(true);
        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
//        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(true);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        super.onDestroy();
    }
}
