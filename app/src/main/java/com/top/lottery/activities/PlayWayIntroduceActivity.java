package com.top.lottery.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.top.lottery.R;

/**
 * Author: 邓言诚  Create at : 2018/8/19  17:04
 * Email: yanchengdeng@gmail.com
 * Describle: 玩法说明
 */
public class PlayWayIntroduceActivity extends BaseActivity {

    private WebView webView;

    private String htmlData =
            "<table class=\"prizeIntro_table\"" +
                    "\t<tbody>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th width=\"11%\">\n" +
                    "\t\t\t\t玩法\n" +
                    "\t\t\t</th>\n" +
                    "\t\t\t<th width=\"16%\">\n" +
                    "\t\t\t\t开奖号码示例\n" +
                    "\t\t\t</th>\n" +
                    "\t\t\t<th width=\"23%\">\n" +
                    "\t\t\t\t投注号码示例\n" +
                    "\t\t\t</th>\n" +
                    "\t\t\t<th width=\"36%\">\n" +
                    "\t\t\t\t中奖规则\n" +
                    "\t\t\t</th>\n" +
                    "\t\t\t<th>\n" +
                    "\t\t\t\t调整后奖金\n" +
                    "\t\t\t</th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选二\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td rowspan=\"12\">\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 05\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选2个号码，猜中开奖号码任意2个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t8元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选三\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 04\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选3个号码，猜中开奖号码任意3个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t24元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选四\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 04 05\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选4个号码，猜中开奖号码任意4个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t95元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选五\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 03 04 05\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选5个号码，猜中开奖号码的全部5个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t650元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选六\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 03 04 05 06\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选6个号码，猜中开奖号码的全部5个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t110元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选七\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 03 04 05 06 07\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选7个号码，猜中开奖号码的全部5个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t32元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t任选八\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 03 04 05 06 07 08\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选8个号码，猜中开奖号码的全部5个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t12元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t前一直选\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选1个号码，猜中开奖号码第1个数字\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t16元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t前二直选\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选2个号码与开奖的前2个号码相同且顺序一致\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t160元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t前二组选\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t02 01\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选2个号码与开奖的前2个号码相同\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t80元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t前三直选\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t01 02 03\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选3个号码与开奖的前3个号码相同且顺序一致\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t1410元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t前三组选\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t02 01 03\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t选3个号码与开奖的前3个号码相同\n" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td class=\"fwb c_ba2636\">\n" +
                    "\t\t\t\t235元\n" +
                    "\t\t\t</td>\n" +
                    "\t\t</tr>\n" +
                    "\t</tbody>\n" +
                    "</table>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web_view);
        setTitle("玩法说明");
        webView = getView(R.id.webview);

        initwebview();
//        url="https://www.baidu.com";
//        url="http://game.dadaozx.com/graph/index.html?token=f1ff2028832000c26da416ee511e591b7a9890445afa622b1da3b&lesson_id=654&content_id=2976&user_id=26462";
        webView.loadData(htmlData,"text/html","utf-8");

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
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(true);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(15);
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        super.onDestroy();
    }


}
