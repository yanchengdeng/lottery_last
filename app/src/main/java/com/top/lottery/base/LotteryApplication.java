package com.top.lottery.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.top.lottery.R;
import com.top.lottery.activities.TrendChartActivity;
import com.top.lottery.beans.GetCart;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.VerisonInfo;
import com.top.lottery.events.AwardIDExperidEvent;
import com.top.lottery.events.NeedUploadVersionEvent;
import com.top.lottery.events.NoticeToDoNewTermCodeEvent;
import com.top.lottery.services.UpdateService;
import com.top.lottery.utils.NewsCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import okhttp3.OkHttpClient;

public class LotteryApplication extends Application {

    private  Context context;
    private AlertDialog successDialog;
    private View viewSuccess;
    private String currentClassName,methodName;

    private  MaterialDialog dialog;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, R.color.color_date);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
//        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
//            }
//        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof AwardIDExperidEvent) {
            if (com.top.lottery.utils.Utils.context!=null && com.top.lottery.utils.Utils.context instanceof Activity) {
                methodName = ((AwardIDExperidEvent) event).getMethodName();
                onShowExpire();
            }
        }else if (event instanceof NeedUploadVersionEvent){
            if (com.top.lottery.utils.Utils.context!=null && com.top.lottery.utils.Utils.context instanceof Activity && !com.top.lottery.utils.Utils.context.isDestroyed()) {
                checkVersion();
            }
        }
    }

    //检查版本更新
    private void checkVersion() {


        HashMap<String, String> data = new HashMap<>();
        data.put("device_type", "android");
        OkGo.<LotteryResponse<VerisonInfo>>post(Constants.Net.CLIENT_CHECKVERSION)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(com.top.lottery.utils.Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<VerisonInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<VerisonInfo>> response) {
                        LogUtils.w("dyc", response + "-----");

                        VerisonInfo verisonInfo = response.body().body;
                        if (verisonInfo!=null){
                            showUploadDialog(verisonInfo);
                        }
                    }

                    @Override
                    public void onError(Response response) {

                    }
                });
    }


    //版本升级对话框
    private void showUploadDialog(final VerisonInfo verisonInfo) {
        if (verisonInfo.update_level.equals("0")){
            return;
        }

        if (dialog!=null && dialog.isShowing()){
            return;
        }


        MaterialDialog.Builder builder = new MaterialDialog.Builder(com.top.lottery.utils.Utils.context);
        View view = LayoutInflater.from(com.top.lottery.utils.Utils.context).inflate(R.layout.dialog_verison_view,null,false);

        TextView tvContent = view.findViewById(R.id.tv_content);
        tvContent.setText(""+verisonInfo.update_content);

        builder.customView(view,false);
         dialog = builder.build();

        view.findViewById(R.id.tv_continu_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_continu_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUpdateVersion(verisonInfo);
                dialog.dismiss();
            }
        });

        if (verisonInfo.update_level.equals("2")){
            view.findViewById(R.id.tv_continu_left).setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();

    }


    private void goUpdateVersion(VerisonInfo verisonInfo) {
        Intent intent = new Intent(com.top.lottery.utils.Utils.context, UpdateService.class);
        intent.putExtra("apkUrl", verisonInfo.app_url);
        startService(intent);
    }


    private void onShowExpire() {
        viewSuccess = LayoutInflater.from(com.top.lottery.utils.Utils.context).inflate(R.layout.dialog_pay_success_view, null);
        successDialog = new AlertDialog.Builder(com.top.lottery.utils.Utils.context)
                .setView(viewSuccess)
                .create();

        ((TextView) viewSuccess.findViewById(R.id.tips)).setText("使用最新期号进行投注");
        ((TextView) viewSuccess.findViewById(R.id.tv_continu_left)).setText("放弃投注");
        ((TextView) viewSuccess.findViewById(R.id.tv_continu_right)).setText("使用最新期号");
        viewSuccess.findViewById(R.id.tv_continu_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
                doUserNewTermCode("0");
            }
        });

        viewSuccess.findViewById(R.id.tv_continu_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
                doUserNewTermCode("1");
            }
        });

        successDialog.setCancelable(false);
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.show();
    }

    /**
     * 0——不使用最新期数投注，以前购物车的信息全部清空
     1——使用最新期数投注，保留以前购物车的信息
     */
    private void doUserNewTermCode(final String flag) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", com.top.lottery.utils.Utils.getUserInfo().uid);
        data.put("use_new_award_id", flag);
        data.put("lid","1");
        OkGo.<LotteryResponse<GetCart>>post(Constants.Net.CART_SAVECART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(com.top.lottery.utils.Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<GetCart>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<GetCart>> response) {
//                        ToastUtils.showShort(""+response.body().msg);
                        if (flag.equals("1") && !TextUtils.isEmpty(currentClassName) && !TextUtils.isEmpty(methodName)) {
                            NoticeToDoNewTermCodeEvent noticeToDoNewTermCodeEvent = new NoticeToDoNewTermCodeEvent();
                            noticeToDoNewTermCodeEvent.className = currentClassName;
                            noticeToDoNewTermCodeEvent.methodName = methodName;
                            EventBus.getDefault().post(noticeToDoNewTermCodeEvent);
                        }
                    }


                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(com.top.lottery.utils.Utils.toastInfo(response));
                    }
                });

    }


    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        EventBus.getDefault().register(this);
        ToastUtils.setBgResource(R.drawable.normal_toast_black);
        ToastUtils.setGravity(Gravity.CENTER,0,0);
        initTextSize(this);
        //侧滑初始化
        BGASwipeBackHelper.init(this, null);
        initOkGo(this);
        initContext(this);
    }




    private  void initContext(Application application) {
        context = application.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                currentClassName = activity.getClass().getName();
                LogUtils.w("dyc","---------"+currentClassName+"----"+ TrendChartActivity.class.getName());
                if (activity.getParent() != null) {
                    com.top.lottery.utils.Utils.context = activity.getParent();
                } else {
                    com.top.lottery.utils.Utils.context = activity;
                }

            }

            public void onActivityStarted(Activity activity) {
                if (activity.getParent() != null) {
                    com.top.lottery.utils.Utils.context = activity.getParent();
                } else {
                    com.top.lottery.utils.Utils.context = activity;
                }

            }

            public void onActivityResumed(Activity activity) {
                if (activity.getParent() != null) {
                    com.top.lottery.utils.Utils.context = activity.getParent();
                } else {
                    com.top.lottery.utils.Utils.context = activity;
                }

            }

            public void onActivityPaused(Activity activity) {
            }

            public void onActivityStopped(Activity activity) {
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            public void onActivityDestroyed(Activity activity) {
            }
        });
    }


    /**
     * 网络请求初始化
     */
    private static void initOkGo(Application context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //超时时间设置，默认60秒
        builder.readTimeout(10 * 1000, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(10 * 1000, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(10 * 1000, TimeUnit.MILLISECONDS);   //全局的连接超时时间
        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        try {
            HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(context.getAssets().open("ddzx.key"));
//            builder.certificatePinner(sslParams3);
            builder.sslSocketFactory(sslParams3.sSLSocketFactory, sslParams3.trustManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(context)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0);                          //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
//                .addCommonParams(params);                       //全局公共参数
    }


    /**
     * 使其系统更改字体大小无效
     */
    private static void initTextSize(Application context) {
        Resources res = context.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Constants.HAS_VESRSION_TIPS = false;
    }


    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Constants.HAS_VESRSION_TIPS = false;
        super.onTrimMemory(level);
    }

}
