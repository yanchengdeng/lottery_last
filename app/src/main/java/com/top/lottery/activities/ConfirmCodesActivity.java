package com.top.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.GetCartAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.GetCart;
import com.top.lottery.beans.GetCartSimple;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MechineChoosInfoSimple;
import com.top.lottery.beans.UserInfo;
import com.top.lottery.events.NoticeToDoNewTermCodeEvent;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.SoftKeyboardStateWatcher;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//选号确认界面
public class ConfirmCodesActivity extends BaseActivity {


    @BindView(R.id.tv_add_auto)
    TextView tvAddAuto;
    @BindView(R.id.tv_add_machine)
    TextView tvAddMachine;
    @BindView(R.id.ck_agree_deal)
    CheckBox ckAgreeDeal;
    @BindView(R.id.iv_clear_car)
    ImageView ivClearCar;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.iv_add_term)
    AppCompatImageView ivAddTerm;
    @BindView(R.id.tv_term_count)
    EditText tvTermCount;
    @BindView(R.id.iv_minus_times)
    AppCompatImageView ivMinusTimes;
    @BindView(R.id.iv_add_times)
    AppCompatImageView ivAddTimes;
    @BindView(R.id.tv_term_times)
    EditText tvTermTimes;
    @BindView(R.id.iv_minus_term)
    AppCompatImageView ivMinusTerm;
    @BindView(R.id.ck_stop_touzhu)
    CheckBox ckStopTouzhu;
    @BindView(R.id.tv_intergry)
    TextView tvIntergry;
    @BindView(R.id.tv_note_numbers)
    TextView tvNoteNumbers;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_deal)
    TextView tvDeal;
    @BindView(R.id.ll_confirm_ui)
    LinearLayout llConfirmUi;
    @BindView(R.id.ll_time_record_ui)
    LinearLayout llTimeRecordUi;
    private LotteryInfo lotteryInfo;
    private GetCart getCart;
    private int record_times = 1;//倍数
    private int append_terms = 1;//追加期数
    private int MAX_TIMES = 1000;
    private int Max_TERMS = 80;//最大期数 80

    private GetCartAdapter getCartAdapter;
    private View viewSuccess, viewEditCount;
    private AlertDialog successDialog, editCountDialog;
    private boolean isFromTrentChart;//是否从走势图过来
    private SoftKeyboardStateWatcher softKeyboardStateWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_codes);
        ButterKnife.bind(this);
//        AndroidAdjustResizeBugFix.assistActivity(this);
        setTitle("选号确认");
        lotteryInfo = (LotteryInfo) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        isFromTrentChart = getIntent().getBooleanExtra(Constants.PASS_BOLLEAN, false);
        getCartAdapter = new GetCartAdapter(R.layout.adapter_get_cart, new ArrayList<GetCart.CartItem>());
        recycle.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recycle.setLayoutManager(new LinearLayoutManager(mContext));
        recycle.setAdapter(getCartAdapter);
        viewSuccess = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_success_view, null);
        tvEndTime.setText("截止投注时间：" + (lotteryInfo.lottery_type.equals("1")?Constants.LASTER_AWARD_END_TIME:Constants.LASTER_AWARD_END_TIME_THREE));

        getCartAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_dele_item) {
                    doDeletItem(getCartAdapter.getData().get(position).record_key);
                }
            }
        });
        getCart();

        ckStopTouzhu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCartChange(false);
            }
        });

        //期数
        tvTermCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvTermCount.setText("");
                }else{
                    tvTermCount.setFocusable(true);
                }
            }
        });

        tvTermCount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvTermCount.setFocusable(true);
                KeyboardUtils.showSoftInput(tvTermCount);
                return false;
            }
        });

        tvTermCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtils.w("dyc", "onTextChanged:" + s.toString() + "---start:" + start + "---befor:");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
//                    append_terms = 1;
//                    tvTermCount.setHint("1");
                    countNativeTouzhuJushIntergray();
                } else {
                    if (s.toString().startsWith("0")) {
                        tvTermCount.setText("1");
                        append_terms = 1;
                    } else {
                        if (Integer.parseInt(s.toString()) > Max_TERMS) {
                            append_terms = Max_TERMS;
                            tvTermCount.setText(String.valueOf(Max_TERMS));
                        } else {
                            append_terms = Integer.parseInt(s.toString());
                        }
                    }
                    countNativeTouzhuJushIntergray();
                }
                if (s.toString().length() == tvTermCount.getEditableText().toString().length()) {
                    tvTermCount.setSelection(s.toString().length());
                    countNativeTouzhuJushIntergray();
                }
            }
        });


        //倍数
        tvTermTimes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvTermTimes.setText("");
                }else{
                    tvTermTimes.setFocusable(true);
                }
            }
        });

        tvTermTimes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvTermTimes.setFocusable(true);
                KeyboardUtils.showSoftInput(tvTermTimes);
                return false;
            }
        });


        tvTermTimes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
//                    record_times = 1;
//                    tvTermTimes.setHint("1");
                    countNativeTouzhuJushIntergray();
                } else {
                    if (s.toString().startsWith("0")) {
                        tvTermTimes.setText("1");
                        record_times = 1;
                    } else {
                        if (Integer.parseInt(s.toString()) > MAX_TIMES) {
                            record_times = MAX_TIMES;
                            tvTermTimes.setText(String.valueOf(MAX_TIMES));
                        } else {
                            record_times = Integer.parseInt(s.toString());

                        }
                    }
                    countNativeTouzhuJushIntergray();
                }
                if (s.toString().length() == tvTermTimes.getEditableText().toString().length()) {
                    tvTermTimes.setSelection(s.toString().length());
                    countNativeTouzhuJushIntergray();
                }
            }
        });


        softKeyboardStateWatcher
                = new SoftKeyboardStateWatcher(findViewById(R.id.ll_root));

        softKeyboardStateWatcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int height) {
                if (height > Constants.KEY_BORAD_HIEGHT) {
                    //键盘弹出
//                    llConfirmUi.setVisibility(View.GONE);
                    softKeyboardStateWatcher.setIsSoftKeyboardOpened(true);
                }
            }

            @Override
            public void onSoftKeyboardClosed() {
//                llConfirmUi.setVisibility(View.VISIBLE);
//                setCartChange(false);


                tvTermTimes.setFocusable(false);
                tvTermCount.setFocusable(false);
                if (TextUtils.isEmpty(tvTermTimes.getEditableText().toString())) {
                    tvTermTimes.setText("" + record_times);
                }

                if (TextUtils.isEmpty(tvTermCount.getEditableText().toString())) {
                    tvTermCount.setText("" + append_terms);
                }

            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeToDoNewTermCodeEvent event) {
        if (event != null) {
            String className = event.className;
            String methodName = event.methodName;
            if (className.equals(ConfirmCodesActivity.class.getName())) {
                if (methodName.equals(Constants.Net.CART_PAY)) {
                    doCardPay();
                }
            }
        }
    }


    //删除购物侧 某个彩票信息

    /**
     * 要删除的键值,
     * 值为数字时，如0，1，2，删除单条数据
     * 值为all,清空购物车
     */
    private void doDeletItem(String record_key) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfo.lid);// 最新彩种期数id
        data.put("record_key", record_key);
        OkGo.<LotteryResponse<GetCartSimple>>post(Constants.Net.CART_DELETE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<GetCartSimple>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<GetCartSimple>> response) {
                        getCart();
                    }

                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }

                    }
                });
    }


    //获取购物车信息
    private void getCart() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfo.lid);// 最新彩种期数id
        OkGo.<LotteryResponse<GetCart>>post(Constants.Net.CART_GETCART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<GetCart>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<GetCart>> response) {
                        getCart = response.body().body;
                        if (getCart != null) {
                            initCarts();
                        }
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });
    }

    //初始化购物侧信息
    private void initCarts() {

        getCartAdapter.setNewData(getCart.records);

        tvIntergry.setText("积分：" + getCart.total_cost_score);
        tvNoteNumbers.setText("注数：" + getCart.total_number);

        tvTermCount.setText(getCart.total_chase_awards);
        tvTermTimes.setText(getCart.record_times);
        //0,追号中奖中止 追号中奖中止 0-否 1-是，默认为1
        if (getCart.is_win_stop_chase == 0) {
            ckStopTouzhu.setChecked(false);
        } else {
            ckStopTouzhu.setChecked(true);
        }
    }

    @OnClick({R.id.tv_deal, R.id.tv_add_auto, R.id.tv_term_count, R.id.tv_term_times, R.id.tv_add_machine, R.id.iv_clear_car, R.id.iv_add_term, R.id.iv_minus_times, R.id.iv_add_times, R.id.iv_minus_term, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_deal:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_NAME, "委托投注协议");
                bundle.putString(Constants.PASS_STRING, Constants.Net.WEB_DEAL);
                ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);
                break;
            case R.id.tv_add_auto:
                if (isFromTrentChart) {
                    ActivityUtils.startActivity(LotteryFunnyActivity.class);
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            //编辑
            case R.id.tv_term_count:
//                showEditNumDialog(false);
                break;
            case R.id.tv_term_times:
//                showEditNumDialog(true);
                break;
            case R.id.tv_add_machine:
                doMechineAction();
                break;
            case R.id.iv_clear_car:
                doDeletItem("all");
                break;
            case R.id.iv_add_term:
                //增加期数
                if (append_terms < Max_TERMS) {
                    append_terms++;
                    countNativeTouzhu();
                } else {
                    ToastUtils.showShort("最多" + MAX_TIMES + "期");
                }
//                setCartChange(false);

                break;
            case R.id.iv_minus_times:
                //减少倍数
                if (record_times > 1) {
                    record_times--;
                    countNativeTouzhu();
//                    setCartChange(true);
                }
                break;
            case R.id.iv_add_times:
                //增加倍数
                if (record_times < MAX_TIMES) {
                    record_times++;
                    countNativeTouzhu();
                } else {
                    ToastUtils.showShort("最多" + MAX_TIMES + "倍");
                }
//                setCartChange(true);
                break;
            case R.id.iv_minus_term:
                //减少期数
                if (append_terms > 1) {
                    append_terms--;
//                    setCartChange(false);
                    countNativeTouzhu();
                }
                break;
            case R.id.tv_confirm:
                if (ckAgreeDeal.isChecked()) {
                    showLoadingBar();
                    setCarChangeSetting();

                } else {
                    ToastUtils.showShort("请确认阅读与同意");
                }
                break;
        }
    }

    //本地结算投注数据结果
    private void countNativeTouzhu() {

        if (getCart == null) {
            ToastUtils.showShort("暂无注数");
        }

        if (getCart != null) {
            int touzhuCount = getCart.pre_number * record_times * append_terms;
            tvIntergry.setText("积分：" + touzhuCount * 2);
            tvNoteNumbers.setText("注数：" + touzhuCount);
        } else {
            tvIntergry.setText("积分：" + 0);
            tvNoteNumbers.setText("注数：" + 0);
        }

        LogUtils.w("dyc", append_terms + "-------" + record_times);
        tvTermCount.setText("" + append_terms);
        tvTermTimes.setText("" + record_times);

    }

    //仅仅计算积分
    private void countNativeTouzhuJushIntergray() {

        if (getCart == null) {
            ToastUtils.showShort("暂无注数");
        }

        if (getCart != null) {
            int touzhuCount = getCart.pre_number * record_times * append_terms;
            tvIntergry.setText("积分：" + touzhuCount * 2);
            tvNoteNumbers.setText("注数：" + touzhuCount);
        } else {
            tvIntergry.setText("积分：" + 0);
            tvNoteNumbers.setText("注数：" + 0);
        }

        LogUtils.w("dyc", append_terms + "-------" + record_times);
//        tvTermCount.setText(""+append_terms);
//        tvTermTimes.setText(""+record_times);
    }


    private void showEditNumDialog(final boolean isTimes) {

        viewEditCount = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_num_view, null);
        editCountDialog = new AlertDialog.Builder(Utils.context)
                .setView(viewEditCount)
                .create();

        if (!isTimes) {
            ((TextView) viewEditCount.findViewById(R.id.tips)).setText("输入期数");
        } else {
            ((TextView) viewEditCount.findViewById(R.id.tips)).setText("输入倍数");
        }

        final EditText editText = viewEditCount.findViewById(R.id.et_input);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    if (isTimes) {
                        record_times = 1;
                    } else {
                        append_terms = 1;
                    }
                } else {
                    if (s.toString().startsWith("0")) {
                        editText.setText("1");
                        if (isTimes) {
                            record_times = 1;
                        } else {
                            append_terms = 1;
                        }
                    } else {
                        if (Integer.parseInt(s.toString()) > MAX_TIMES) {
                            if (isTimes) {
                                record_times = MAX_TIMES;
                            } else {
                                append_terms = MAX_TIMES;
                            }
                            editText.setText(String.valueOf(MAX_TIMES));
                        } else {
                            if (isTimes) {
                                record_times = Integer.parseInt(s.toString());
                            } else {
                                append_terms = Integer.parseInt(s.toString());
                            }
                        }
                    }
                }
            }
        });
        viewEditCount.findViewById(R.id.tv_continu_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCountDialog.dismiss();
            }
        });

        viewEditCount.findViewById(R.id.tv_continu_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCountDialog.dismiss();
                setCartChange(isTimes);
            }
        });

        editCountDialog.setCancelable(false);
        editCountDialog.setCanceledOnTouchOutside(false);
        editCountDialog.show();
    }


    //随机投注
    private void doMechineAction() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("add", "1");//1-随机投注
        data.put("lid", lotteryInfo.lid);
//        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id//玩法id,为机选的时候需要这个参数，如果是随机投注则不需要该参数
        OkGo.<LotteryResponse<MechineChoosInfoSimple>>post(Constants.Net.CART_MECHINE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MechineChoosInfoSimple>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MechineChoosInfoSimple>> response) {
//                        getCart();
                        setCartChange(true);
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });
    }

    //下单结算
    private void doCardPay() {


        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfo.lid);// 最新彩种期数id
        OkGo.<LotteryResponse<UserInfo>>post(Constants.Net.CART_PAY)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<UserInfo>> response) {
                        int flag = response.body().code;
                        dismissLoadingBar();
                        showDialogTips(flag);

                    }


                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });
    }

    /**
     * -2
     * 在不能投注的时间范围内，如截止时间为75s的时间范围内，或者是明天的期数，这种情况是不能投注，直接提示用户当期还不可投注
     * -3
     * 提示信息为期数不正确或期号不存在或者该期不能投注或者或则投注已经结束，均建议客户使用最新期号进行投注
     * -4
     * 购物车为空，请重新选号
     * -5
     * 订单各种错误，如
     * 本次投注中第2组号码已经超过上限，请重新选择该组号码，具体的显示为第几组号码有问题
     * 订单出错，订单有问题
     *
     * @param flag
     */
    private void showDialogTips(int flag) {

        successDialog = new AlertDialog.Builder(mContext)
                .setView(viewSuccess)
                .create();
        if (flag == 1) {

            ((TextView) viewSuccess.findViewById(R.id.tips)).setText("投注成功");
            viewSuccess.findViewById(R.id.tv_continu_left).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    successDialog.dismiss();
                    finish();
                }
            });

            viewSuccess.findViewById(R.id.tv_continu_right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    successDialog.dismiss();
                    Intent intent = new Intent(mContext, MainNewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            successDialog.setCancelable(false);
            successDialog.setCanceledOnTouchOutside(false);
            successDialog.show();
        } else if (flag == -3) {
            ((TextView) viewSuccess.findViewById(R.id.tips)).setText("使用最新期号进行投注");
            ((TextView) viewSuccess.findViewById(R.id.tips)).setText("投注成功");
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

    }

    //使用最新期号投注

    /**
     * 0——不使用最新期数投注，以前购物车的信息全部清空
     * 1——使用最新期数投注，保留以前购物车的信息
     */
    private void doUserNewTermCode(final String flag) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("use_new_award_id", flag);
        OkGo.<LotteryResponse<GetCart>>post(Constants.Net.CART_SAVECART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<GetCart>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<GetCart>> response) {
                        ToastUtils.showShort("" + response.body().msg);
//                        if (flag.equals("1")){
//                            finish();
//                        }else{
                        setResult(Constants.BACK_TO_MAIN);
                        finish();
//                        }
                    }


                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });

    }

    /**
     * 用户uid
     * 用户tokens
     * 彩种id
     * 追号中奖中止 追号中奖中止 0-否 1-是，默认为1
     * 倍数，默认为1倍
     * 追加期数，默认为1期，就是不追加
     * <p>
     * isTimes  是否是操作倍数
     */
    private void setCartChange(final boolean isTimes) {
        ivAddTimes.setClickable(false);
        ivMinusTimes.setClickable(false);
        ivAddTerm.setClickable(false);
        ivMinusTerm.setClickable(false);
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfo.lid);// 最新彩种期数id
        data.put("is_win_stop_chase", ckStopTouzhu.isChecked() ? "1" : "0");
        data.put("record_times", String.valueOf(record_times));//倍数，默认为1倍
        data.put("chase_awards", String.valueOf(append_terms));//追加期数，默认为1期，就是不追加
        OkGo.<LotteryResponse<GetCart>>post(Constants.Net.CART_CHANGE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<GetCart>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<GetCart>> response) {
                        getCart();
                        ivAddTimes.setClickable(true);
                        ivMinusTimes.setClickable(true);
                        ivAddTerm.setClickable(true);
                        ivMinusTerm.setClickable(true);
                        tvTermCount.setText(String.valueOf(append_terms));
                        tvTermTimes.setText(String.valueOf(record_times));
                    }


                    @Override
                    public void onError(Response response) {
//                        ToastUtils.showShort(Utils.toastInfo(response));
                        if (isTimes) {
                            record_times--;
                        } else {
                            append_terms--;
                        }

                        ivAddTerm.setClickable(true);
                        ivMinusTerm.setClickable(true);
                        ivAddTimes.setClickable(true);
                        ivMinusTimes.setClickable(true);
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });
    }


    /**
     * 下单时候  记录设置倍数
     */
    private void setCarChangeSetting() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfo.lid);// 最新彩种期数id
        data.put("is_win_stop_chase", ckStopTouzhu.isChecked() ? "1" : "0");
        data.put("record_times", String.valueOf(record_times));//倍数，默认为1倍
        data.put("chase_awards", String.valueOf(append_terms));//追加期数，默认为1期，就是不追加
        OkGo.<LotteryResponse<GetCart>>post(Constants.Net.CART_CHANGE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<GetCart>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<GetCart>> response) {
                        doCardPay();
                    }


                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (softKeyboardStateWatcher != null) {
//            softKeyboardStateWatcher.removeSoftKeyboardStateListener(this);
        }
        setCartChange(true);

    }
}
