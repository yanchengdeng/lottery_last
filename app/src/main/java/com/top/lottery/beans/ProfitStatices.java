package com.top.lottery.beans;

import java.io.Serializable;

/**
 * profit_and_loss
 body

 盈亏状况
 total_daili_score
 body

 返利积分
 total_bonus_score
 body

 分红积分
 score
 body

 账户积分
 credit_balance_score
 body

 信用分余额
 credit_line_score
 body

 信用分额度
 child_total
 body

 下级会员人数
 child_cost_score
 body

 总购彩积分
 child_reward_score
 body

 总中奖积分
 child_daili_score
 body

 下级返利总积分
 child_bonus_score
 body

 下级盈利总分红
 child_recharge_score
 body

 总充值积分
 child_score
 body

 剩余可用积分总数

 */
public class ProfitStatices implements Serializable {

    public String total_daili_score;// 10.50,
    public String total_bonus_score;// 80.32,
    public String score;// 2.00,
    public String credit_balance_score;// 0.00,
    public String credit_line_score;// 0.00,
    public String profit_and_loss;// 90.82,
    public String child_total;// 7,
    public String child_cost_score;// 1424.00,
    public String child_reward_score;// 832.00,
    public String child_daili_score;// 213.60,
    public String child_bonus_score;// 284.02,
    public String child_recharge_score;// 1.00,
    public String child_score;// 8775.00
}
