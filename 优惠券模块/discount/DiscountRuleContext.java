package com.visionet.discount;

import com.visionet.entity.DiscountCoupon;
import com.visionet.support.ProjectException;
import com.visionet.utils.yishengjun.YiShengJunConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Time: 2019/5/28 17:18
 * @Author: bxx
 * @Description: 优惠规则上下文
 */
@Component
public class DiscountRuleContext implements InitializingBean {

    @Autowired
    private YiShengJunConfig yiShengJunConfig;

    /**
     * 用来存放所有的优惠券减免规则，key为优惠券类型，value为优惠券减免规则对象
     */
    private Map<String, DiscountByDcTypeEnum> discountByDcTypeMap;

    /**
     * Bean初始化方法，在Bean创建并赋值之后
     */
    @Override
    public void afterPropertiesSet() {
        discountByDcTypeMap = new HashMap<>(2);
        discountByDcTypeMap.put(yiShengJunConfig.getFullReductionType(), DiscountByDcTypeEnum.FULL_REDUCTION);
        discountByDcTypeMap.put(yiShengJunConfig.getDirectSubtractType(), DiscountByDcTypeEnum.DIRECT_SUBTRACT);
    }

    /**
     * 传入优惠券和总金额，返回可以优惠的金额，在这里把所有优惠券规则添加到map中，优惠券类型为键
     *
     * @param discountCoupon
     * @param sumPrice
     * @return
     */
    public BigDecimal getDiscountMoney(DiscountCoupon discountCoupon, BigDecimal sumPrice) {
        //判断优惠券类型是否存在
        if (!isExistsDiscountType(discountCoupon.getDcType())) {
            throw new ProjectException("优惠规则不存在！");
        }

        //获取相应的优惠规则，计算优惠金额并返回
        return discountByDcTypeMap.get(discountCoupon.getDcType()).discountByDcType(discountCoupon, sumPrice);
    }

    /**
     * 是否存在该类型的优惠规则
     *
     * @param discountType
     * @return
     */
    public boolean isExistsDiscountType(String discountType) {
        return discountByDcTypeMap.keySet().contains(discountType);
    }

    /**
     * 获取所有已开发的优惠券类型
     *
     * @return
     */
    public Set<String> getDiscountTypes() {
        return discountByDcTypeMap.keySet();
    }

}
