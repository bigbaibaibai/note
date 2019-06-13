package com.visionet.discount;

import com.visionet.entity.DiscountCoupon;
import com.visionet.support.ProjectException;

import java.math.BigDecimal;

/**
 * @Time: 2019/5/28 17:46
 * @Author: bxx
 * @Description: 优惠券减免规则的枚举类
 */
public enum DiscountByDcTypeEnum {
    /**
     * 在这里写优惠券折扣规则的枚举对象
     */
    //满减
    FULL_REDUCTION {
        @Override
        public BigDecimal discountByDcType(DiscountCoupon discountCoupon, BigDecimal sumPrice) {
            BigDecimal enablePrice = discountCoupon.getEnablePrice();
            //判断是否超过启用额度
            if (enablePrice.compareTo(sumPrice) > 0) {
                throw new ProjectException("订单额度不能启用该优惠券，多买点东西吧~~~");
            }

            //获取需要减去次数
            int num = sumPrice.divide(enablePrice).intValue();
            //返回每次减的额度乘次数
            return discountCoupon.getPreferentialPrice().multiply(new BigDecimal(num));
        }

    },
    //直减
    DIRECT_SUBTRACT {
        @Override
        public BigDecimal discountByDcType(DiscountCoupon discountCoupon, BigDecimal sumPrice) {
            if (discountCoupon.getEnablePrice().compareTo(sumPrice) > 0) {
                throw new ProjectException("订单额度不能启用该优惠券，多买点东西吧~~~");
            }

            //返回优惠额度
            return discountCoupon.getPreferentialPrice();
        }

    };

    /**
     * 通过优惠券编号和总价格，计算出使用该优惠券可以优惠多少金额
     *
     * @param discountCoupon 优惠券对象
     * @param sumPrice       总价格
     * @return 优惠金额
     */
    public abstract BigDecimal discountByDcType(DiscountCoupon discountCoupon, BigDecimal sumPrice);

}
