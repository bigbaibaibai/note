package com.visionet.service;

import com.visionet.entity.DUserDiscountCoupon;
import com.visionet.entity.DiscountCoupon;
import com.visionet.support.ProjectException;
import com.visionet.vo.ReceiveDiscountCouponVo;
import com.visionet.vo.UserDiscountCouponVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 优惠券service
 *
 * @Time: 2019/5/28 10:32
 * @Author: bxx
 * @Description:
 */
public interface DiscountCouponService {

    /**
     * 添加优惠券到用户优惠券表
     *
     * @param phone
     * @param discountCouponCode
     * @param discountCoupon
     */
    void receiveDiscountCoupon(String phone, DiscountCoupon discountCoupon);

    /**
     * 通过手机号获取用户优惠券
     *
     * @param phone
     * @return
     */
    List<DUserDiscountCoupon> getDiscountCouponForUser(String phone);

    /**
     * 添加优惠券
     *
     * @param discountCoupon
     */
    void addDiscountCoupon(DiscountCoupon discountCoupon);

    /**
     * 更新优惠券信息
     *
     * @param discountCoupon
     */
    void updateDiscountCoupon(DiscountCoupon discountCoupon);

    /**
     * 删除优惠券信息
     *
     * @param discountCouponCode
     */
    void deleteDiscountCoupon(String discountCouponCode);

    /**
     * 分页查询优惠券
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<DiscountCoupon> getDiscountCoupon(int pageNo, int pageSize);

    /**
     * 查询单个优惠券
     *
     * @param discountCouponCode
     * @return
     */
    DiscountCoupon getDiscountCouponForId(String discountCouponCode);

    /**
     * 把用户优惠券转换为含有优惠券属性的用户优惠券对象
     *
     * @param dUserDiscountCoupons
     * @return
     */
    List<UserDiscountCouponVo> convertDisCouToVo(List<DUserDiscountCoupon> dUserDiscountCoupons);

    /**
     * 检查优惠券必要信息
     *
     * @param discountCoupon
     * @throws ProjectException
     */
    void checkDiscountCoupon(DiscountCoupon discountCoupon) throws ProjectException;

    /**
     * 检查这个用户能否领取该优惠券
     *
     * @param phone
     * @param discountCouponCode
     * @return 返回优惠券有效天数
     * @throws ProjectException
     */
    DiscountCoupon checkCanReceiveDiscountCoupon(String phone, String discountCouponCode) throws ProjectException;

    /**
     * 检查接收前端的数据是否有空值
     *
     * @param discountCouponVo
     */
    void checkReceiveDiscountCouponVo(ReceiveDiscountCouponVo discountCouponVo);

    /**
     * 检查优惠券是否过期，是否符合可叠加规则
     *
     * @param userDiscountCouponCodes
     * @param productId
     * @return
     */
    List<UserDiscountCouponVo> checkUsableDiscountCoupon(String productId, List<Integer> userDiscountCouponCodes);


    /**
     * 获取已过期的优惠券
     *
     * @param dUDcs
     * @return
     */
    List<DUserDiscountCoupon> getExpireUserDiscountCoupon(List<DUserDiscountCoupon> dUDcs);

    /**
     * 检查优惠券是否可叠加
     *
     * @param dcCodes
     * @param productId
     * @throws ProjectException
     */
    void checkDiscountCouponSuperposition(String productId, List<DiscountCoupon> discountCoupons) throws ProjectException;

    /**
     * 检查是否为微盟渠道
     *
     * @param orgCode
     * @return
     */
    boolean checkIsWeiMeng(String orgCode);

    /**
     * 获取所有已开发的优惠券类型
     *
     * @return
     */
    Set<String> getDiscountTypes();

    /**
     * 通过Id删除用户优惠券
     *
     * @param dUserDiscountCouponId
     */
    void deleteUserDiscountCoupon(Integer dUserDiscountCouponId);

    /**
     * 使用优惠券接口，传入原价，和用户所使用的用户优惠券ID
     *
     * @param sumPrice                传入原价
     * @param userDiscountCouponCodes 用户所使用的用户优惠券ID
     * @param productId                    产品ID
     * @return 返回减免后的价格
     */
    BigDecimal useDiscountCoupon(String productId, BigDecimal sumPrice, List<Integer> userDiscountCouponCodes);

}