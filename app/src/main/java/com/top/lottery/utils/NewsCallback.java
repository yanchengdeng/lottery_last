package com.top.lottery.utils;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.TokenTimeOut;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

public abstract class NewsCallback<T> extends AbsDYCCallback<T> {

    /**
     * 这里的数据解析是根据 http://gank.io/api/data/Android/10/1 返回的数据来写的
     * 实际使用中,自己服务器返回的数据格式和上面网站肯定不一样,所以以下是参考代码,根据实际情况自己改写
     */
    @Override
    public T convertResponse(Response response) throws Throwable {
        //以下代码是通过泛型解析实际参数,泛型必须传
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        Type rawType = ((ParameterizedType) type).getRawType();
        if (rawType == LotteryResponse.class) {
            String decy = new String(response.body().bytes());
            if (Constants.DEBUG) {
                LogUtils.d("dyc", response.networkResponse().request().url().toString());
                LogUtils.d("dyc", decy);
            }
            LotteryResponse collegeResponseSimple = new Gson().fromJson(decy, LotteryResponse.class);
            if (collegeResponseSimple.code == 1) {
                LotteryResponse aaResponse = new Gson().fromJson(decy, type);
                response.close();
                return (T) aaResponse;
            } else {
                response.close();
                /**
                 * -1  参数错误
                 * '-100' => '接口地址或所请求的方法不存在',
                 '-101' => '发现新版本，请升级！',
                 '-102' => 'app_id不能为空.',
                 '-103' => 'app_imei不能为空.',
                 '-104' => 'app_sign不能为空.',
                 '-105' => 'version不能为空.',
                 '-106' => 'app_id不存在.',
                 '-107' => 'app_sign签名错误',
                 '-108' => 'app_token不存在',

                 '-110' => '请先登录系统',
                 '-111' => '该账号不存在',
                 '-112' => '该账号已失效，请联系客服或更换账号重试',
                 '-113' => '登录失败，请检查帐号密码',//一级认证出错的时候使用
                 '-114' => '登录失败，请检查帐号密码',//二级认证出错的时候使用
                 '-120' => '网站关闭，请稍后访问'
                 */
                if (collegeResponseSimple.code == -108
                        || collegeResponseSimple.code == -110
                        || collegeResponseSimple.code == -111
                        || collegeResponseSimple.code == -112
                        || collegeResponseSimple.code == -113
                        || collegeResponseSimple.code == -114
                        ) {
                    EventBus.getDefault().post(new TokenTimeOut());
                    throw new IllegalStateException("登陆超时");
                } else {
                    throw new IllegalStateException(collegeResponseSimple.msg);
                }
            }
        } else {
            response.close();
            throw new IllegalStateException("数据解析失败!");
        }
    }
}
