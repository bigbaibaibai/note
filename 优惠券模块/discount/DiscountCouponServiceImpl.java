package com.visionet.service.Impl;

import com.visionet.constant.BusinessStatus;
import com.visionet.discount.DiscountRuleContext;
import com.visionet.entity.DUserDiscountCoupon;
import com.visionet.entity.DiscountCoupon;
import com.visionet.repository.DUserDiscountCouponRepository;
import com.visionet.repository.DiscountCouponRepository;
import com.visionet.service.DiscountCouponService;
import com.visionet.support.ProjectException;
import com.visionet.utils.BeanUtils;
import com.visionet.utils.yishengjun.YiShengJunConfig;
import com.visionet.vo.ReceiveDiscountCouponVo;
import com.visionet.vo.UserDiscountCouponVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * 优惠券service
 *
 * @Time: 2019/5/28 9:46
 * @Author: bxx
 * @Description:
 */
@Service
public class DiscountCouponServiceImpl implements DiscountCouponService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    @Autowired
    private DUserDiscountCouponRepository dUserDiscountCouponRepository;

    @Autowired
    private DiscountRuleContext discountRuleContext;

    @Autowired
    private YiShengJunConfig yiShengJunConfig;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiveDiscountCoupon(String phone, DiscountCoupon discountCoupon) {
        Date date = new Date();
        //封装信息
        DUserDiscountCoupon dUserDiscountCoupon = new DUserDiscountCoupon();
        dUserDiscountCoupon.setGetDate(date);

        //设置为正常使用状态
        dUserDiscountCoupon.setdStatus(DUserDiscountCoupon.NORMAL_STATUS);
        dUserDiscountCoupon.setUserPhone(phone);
        dUserDiscountCoupon.setDcCode(discountCoupon.getDcCode());

        //计算该优惠券过期时间
        Integer efficaciousDay = discountCoupon.getEfficaciousDay();
        //如果优惠券天数为-1，则设置为优惠券领取结束时间
        if (-1 == efficaciousDay) {
            dUserDiscountCoupon.setExpireDate(discountCoupon.getEndDate());
        }else {
            //设置过期时间，当前时间的efficaciousDay后的时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, efficaciousDay);
            dUserDiscountCoupon.setExpireDate(calendar.getTime());
        }

        //保存到数据库
        try {
            dUserDiscountCouponRepository.save(dUserDiscountCoupon);
            //更新优惠券数量
            discountCoupon.setDcSum(discountCoupon.getDcSum() - 1);
            discountCouponRepository.save(discountCoupon);
        } catch (Exception e) {
            logger.info("领取优惠券异常！添加数据到d_user_discount_coupon表时发生异常！", e);
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<DUserDiscountCoupon> getDiscountCouponForUser(String phone) {
        try {
            return dUserDiscountCouponRepository.findByUserPhoneAndDStatus(phone, DUserDiscountCoupon.NORMAL_STATUS);
        } catch (Exception e) {
            logger.info("根据手机号查询用户优惠券异常！查询d_user_discount_coupon表时发生异常！", e);
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addDiscountCoupon(@RequestBody DiscountCoupon discountCoupon) {
        //如果优惠券状态为null，则将优惠券状态设置为1，表示已启用
        if (null == discountCoupon.getDcStatus()) {
            discountCoupon.setDcStatus(1);
        }
        //如果可叠加数量为null，则设置为1，表示只可以叠加1张
        if (null == discountCoupon.getSuperpositionNum()) {
            discountCoupon.setSuperpositionNum(1);
        }
        //如果用户类型为null，则设置为-1，表示不限制用户类型
        if (null == discountCoupon.getUserType()) {
            discountCoupon.setUserType(-1);
        }
        //如果优惠券总数量为null，则为最大正整数
        if (null == discountCoupon.getDcSum()) {
            discountCoupon.setDcSum(Integer.MAX_VALUE);
        }
        //如果每人限领数量limit_num为null，则为最大正整数
        if (null == discountCoupon.getLimitNum()) {
            discountCoupon.setLimitNum(Integer.MAX_VALUE);
        }

        Date date = new Date();
        //如果开始时间为null，则设置当前时间
        if (null == discountCoupon.getBeginDate()) {
            discountCoupon.setBeginDate(date);
        }
        //如果有效天数为null，则默认设置为优惠券领取开始时间到优惠券领取结束时间的天数
        if (null == discountCoupon.getEfficaciousDay()) {
            long dayLong = discountCoupon.getBeginDate().getTime() - discountCoupon.getEndDate().getTime();
            //从毫秒转化为天数
            discountCoupon.setEfficaciousDay((int) (dayLong / 1000 / 60 / 60 / 24));
        }

        //补全时间戳
        discountCoupon.setCreateDate(date);
        discountCoupon.setUpdateDate(date);
        try {
            discountCouponRepository.save(discountCoupon);
        } catch (Exception e) {
            logger.info("添加优惠券失败！添加discount_coupon表数据出现异常！", e);
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateDiscountCoupon(DiscountCoupon discountCoupon) {
        DiscountCoupon coupon;
        try {
            //先从数据库中查出优惠券数据
            coupon = discountCouponRepository.findById(discountCoupon.getDcCode()).get();
        } catch (Exception e) {
            logger.info("查询失败！查询discount_coupon表数据出现异常！", e);
            throw e;
        }

        if (coupon == null) {
            throw new ProjectException(BusinessStatus.PARAM_ERROR + "", "优惠券ID不存在");
        }

        try {
            //把前端传过来的优惠券数据中不为null的值赋值到数据库中对应的信息中
            BeanUtils.copyPropertiesInnoreNull(discountCoupon, coupon);
        } catch (Exception e) {
            logger.info("BeanUtils.copyProperties发生异常！", e);
            throw e;
        }

        try {
            //更新时间戳
            coupon.setUpdateDate(new Date());
            //更新到数据库
            discountCouponRepository.save(coupon);
        } catch (Exception e) {
            logger.info("更新失败！更新discount_coupon表数据出现异常！", e);
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteDiscountCoupon(String discountCouponCode) {
        try {
            discountCouponRepository.deleteById(discountCouponCode);
        } catch (Exception e) {
            logger.info("删除优惠券失败！删除discount_coupon表数据出现异常！", e);
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<DiscountCoupon> getDiscountCoupon(int pageNo, int pageSize) {
        Pageable pageable = new QPageRequest(pageNo, pageSize);
        //分页查询
        try {
            Page<DiscountCoupon> all = discountCouponRepository.findAll(pageable);
            return all.getContent();
        } catch (Exception e) {
            logger.info("分页查询优惠券失败！查询discount_coupon表出现异常！");
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public DiscountCoupon getDiscountCouponForId(String discountCouponCode) {
        try {
            Optional<DiscountCoupon> byId = discountCouponRepository.findById(discountCouponCode);
            return byId.isPresent() ? byId.get() : null;
        } catch (Exception e) {
            logger.info("查询优惠券失败！查询discount_coupon表出现异常！", e);
            throw e;
        }

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<UserDiscountCouponVo> convertDisCouToVo(List<DUserDiscountCoupon> dUserDiscountCoupons) {
        List<UserDiscountCouponVo> userDiscountCouponVos = new ArrayList<>(dUserDiscountCoupons.size());
        for (DUserDiscountCoupon dUserDiscountCoupon : dUserDiscountCoupons) {
            //把值赋值到新的vo中
            UserDiscountCouponVo couponVo = new UserDiscountCouponVo();
            BeanUtils.copyProperties(dUserDiscountCoupon, couponVo);
            //查询数据库，封装优惠券对象
            Optional<DiscountCoupon> byId;
            try {
                byId = discountCouponRepository.findById(dUserDiscountCoupon.getDcCode());
            } catch (Exception e) {
                logger.info("查询优惠券失败！查询discount_coupon表出现异常！", e);
                throw e;
            }

            if (!byId.isPresent()) {
                throw new ProjectException("优惠券不存在！");
            }

            couponVo.setDiscountCoupon(byId.get());
            //添加到list
            userDiscountCouponVos.add(couponVo);
        }

        return userDiscountCouponVos;
    }

    /**
     * 优惠券编号必须,（额度必须，启用额度必须）或（折扣必须），开始时间必须，结束时间必须，名称必须，产品代码，组织代码必须
     * 优惠券类型必须且存在
     *
     * @param discountCoupon
     * @throws ProjectException
     */
    @Override
    public void checkDiscountCoupon(DiscountCoupon discountCoupon) throws ProjectException {
        if (StringUtils.isBlank(discountCoupon.getDcCode())) {
            throw new ProjectException("优惠券编号不可为空！");
        }

        if (StringUtils.isBlank(discountCoupon.getDcName())) {
            throw new ProjectException("优惠券名称不可为空！");
        }

        if (StringUtils.isBlank(discountCoupon.getProductCode())) {
            throw new ProjectException("可用产品代码不可为空！");
        }

        if (StringUtils.isBlank(discountCoupon.getUnionCode())) {
            throw new ProjectException("组织代码不可为空！");
        }

        if (null == discountCoupon.getEndDate()) {
            throw new ProjectException("结束领取优惠券时间不可为空！");
        }

        if (null == discountCoupon.getBeginDate()) {
            throw new ProjectException("开始领取优惠券时间不可为空！");
        }


        //启动金额或减去金额为空 并且 打折为空，即两种优惠策略，启动金额搭配减去金额策略，打折策略，两种必须使用一种，如果两种都为null则失败
        if (null == discountCoupon.getEnablePrice() || null == discountCoupon.getPreferentialPrice()) {
            if (null == discountCoupon.getDiscountPercentage()) {
                throw new ProjectException("两种优惠策略\n1.启动金额 和 减去金额 策略（两个值都不为空）\n2.打折策略\n两种必须使用一种");
            }
        }

        if (StringUtils.isBlank(discountCoupon.getDcType())) {
            throw new ProjectException("优惠券类型不能为空");
        }

        if (!discountRuleContext.isExistsDiscountType(discountCoupon.getDcType())) {
            throw new ProjectException("优惠券类型不存在，需要开发规则");
        }

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public DiscountCoupon checkCanReceiveDiscountCoupon(String phone, String discountCouponCode) {
        //查询优惠券配置
        Optional<DiscountCoupon> byId;
        try {
            byId = discountCouponRepository.findById(discountCouponCode);
        } catch (Exception e) {
            logger.info("查询优惠券失败！查询discount_coupon表出现异常！", e);
            throw e;
        }

        if (!byId.isPresent()) {
            throw new ProjectException("优惠券不存在！");
        }

        DiscountCoupon discountCoupon = byId.get();

        //是否到达领取时间
        if (System.currentTimeMillis() < discountCoupon.getBeginDate().getTime()) {
            throw new ProjectException("优惠券领取时间未开始！敬请期待~");
        }

        //是否超过可领取时间
        if (System.currentTimeMillis() > discountCoupon.getEndDate().getTime()) {
            throw new ProjectException("优惠券已失效！");
        }

        //判断优惠券数量
        if (discountCoupon.getDcSum() <= 0) {
            throw new ProjectException("优惠券被领完啦！下次记得早点来哦！");
        }

        List<DUserDiscountCoupon> dUserDiscountCoupons;
        try {
            //查询用户该类型所有优惠券
            dUserDiscountCoupons = dUserDiscountCouponRepository.findByUserPhoneAndDcCodeAndDStatus(phone, discountCouponCode, DUserDiscountCoupon.NORMAL_STATUS);
        } catch (Exception e) {
            logger.info("查询用户优惠券失败！查询discount_coupon表出现异常！", e);
            throw e;
        }

        //  --------------------------------如果需要对用户类型判断，可以在此处添加逻辑----------------------
        // |                                                                                            |
        // |                                                                                            |
        // |                                                                                            |
        // |                                                                                            |
        // |                                                                                            |
        //  --------------------------------------------------------------------------------------------

        //判断用户已领取数量是否超出每个人限制可领取最大数量
        if (dUserDiscountCoupons.size() >= discountCoupon.getLimitNum()) {
            throw new ProjectException("1", "每人最多拥有" + discountCoupon.getLimitNum() + "张优惠券，使用后才可以继续领呦！");
        }

        return discountCoupon;
    }

    @Override
    public void checkReceiveDiscountCouponVo(ReceiveDiscountCouponVo discountCouponVo) {
        //判空
        if (StringUtils.isBlank(discountCouponVo.getPhone())) {
            throw new ProjectException("手机号或优惠券类型不能为空！");
        }

        if (StringUtils.isBlank(discountCouponVo.getPhoneCode())) {
            throw new ProjectException("验证码不能为空!");
        }

        if (StringUtils.isBlank(discountCouponVo.getDiscountCouponCode())) {
            throw new ProjectException("优惠券类型不能为空！");
        }

    }

    @Override
    public List<UserDiscountCouponVo> checkUsableDiscountCoupon(String productId, List<Integer> userDiscountCouponCodes) {
        //判断用户优惠券是否过期
        List<DUserDiscountCoupon> dUDcs = dUserDiscountCouponRepository.findAllById(userDiscountCouponCodes);
        if (getExpireUserDiscountCoupon(dUDcs).size() > 0) {
            throw new ProjectException("优惠券已过期！");
        }

        //获取所有的dcCodes
        List<String> dcCodes = new ArrayList<>(dUDcs.size());
        for (DUserDiscountCoupon dUDc : dUDcs) {
            dcCodes.add(dUDc.getDcCode());
        }

        //检查优惠券使用是否符合配置表
        List<DiscountCoupon> discountCoupons = discountCouponRepository.findAllById(dcCodes);
        for (DiscountCoupon discountCoupon : discountCoupons) {
            //1.判断优惠券是否开启,状态是否开启，0禁用，1开启
            if (discountCoupon.getDcStatus() == 0) {
                throw new ProjectException("\"" + discountCoupon.getDcName() + "\"优惠券已被禁用");
            }

            //2.判断优惠券和商品ID是否匹配
            if (!productId.equals(discountCoupon.getProductCode())){
                throw new ProjectException("优惠券不支持该商品！");
            }

        }

        //3.如果优惠券数量大于1，判断优惠券优惠券是否符合叠加规则
        if (discountCoupons.size() > 1) {
            checkDiscountCouponSuperposition(productId, discountCoupons);
        }

        //把用户优惠券对象转化为包含优惠券配置的优惠券对象，并返回
        return convertDisCouToVo(dUDcs);
    }

    @Override
    public List<DUserDiscountCoupon> getExpireUserDiscountCoupon(List<DUserDiscountCoupon> dUDcs) {
        List<DUserDiscountCoupon> expires = new ArrayList<>();
        for (DUserDiscountCoupon dUserDiscountCoupon : dUDcs) {
            if (dUserDiscountCoupon.getExpireDate().getTime() < System.currentTimeMillis()) {
                expires.add(dUserDiscountCoupon);
            }

        }

        return expires;
    }

    /**
     * 检查优惠券是否可叠加
     *
     * @param discountCoupons
     * @throws ProjectException
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public void checkDiscountCouponSuperposition(String productId, List<DiscountCoupon> discountCoupons) throws ProjectException {
        Map<DiscountCoupon, Integer> dcAndNum = new HashMap<>(discountCoupons.size());
        Set<String> dcCodes = new HashSet<>(dcAndNum.size());
        //按优惠券分数量
        for (DiscountCoupon discountCoupon : discountCoupons) {
            //如果包含，则加数量
            if (dcAndNum.containsKey(discountCoupon)) {
                Integer num = dcAndNum.get(discountCoupon);
                dcAndNum.put(discountCoupon, num + 1);
            } else {
                //如果不包含，则添加到map并设置数量为1
                dcAndNum.put(discountCoupon, 1);
                //保存优惠券ID
                dcCodes.add(discountCoupon.getDcCode());
            }

        }

        //获取优惠券集合（不重复的）
        Set<DiscountCoupon> dcs = dcAndNum.keySet();
        for (DiscountCoupon discountCoupon : dcs) {
            //判断同一编号优惠券叠加数量是否超出限制
            if (dcAndNum.get(discountCoupon) > discountCoupon.getSuperpositionNum()) {
                throw new ProjectException("同种优惠券使用超出限制，请查看优惠券相关叠加规则~~");
            }

            List<String> superpositionDcCode = discountCoupon.getSuperpositionDcCodeList();
            //把自己添加进去
            superpositionDcCode.add(discountCoupon.getDcCode());
            //判断不同编号优惠券是否可叠加
            if (!superpositionDcCode.containsAll(dcCodes)) {
                throw new ProjectException("多种优惠券不符合叠加规则，请查看优惠券相关叠加规则~~");
            }

        }

    }

    @Override
    public boolean checkIsWeiMeng(String orgCode) {
        return yiShengJunConfig.getWeimengCode().equals(orgCode);
    }

    @Override
    public Set<String> getDiscountTypes() {
        return discountRuleContext.getDiscountTypes();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUserDiscountCoupon(Integer dUserDiscountCouponId) {
        try {
            Optional<DUserDiscountCoupon> dUserDcOp = dUserDiscountCouponRepository.findById(dUserDiscountCouponId);
            if (!dUserDcOp.isPresent()) {
                throw new ProjectException("优惠券不存在！");
            }

            DUserDiscountCoupon dUserDiscountCoupon = dUserDcOp.get();
            //把状态设置为不可用
            dUserDiscountCoupon.setdStatus(DUserDiscountCoupon.FORBIDDEN_STATUS);
            dUserDiscountCouponRepository.save(dUserDiscountCoupon);
        } catch (Exception e) {
            logger.info("设置用户优惠券状态失败！", e);
            throw e;
        }

    }

    @Override
    public BigDecimal useDiscountCoupon(String productId, BigDecimal sumPrice, List<Integer> userDiscountCouponCodes) {
        if (userDiscountCouponCodes == null || userDiscountCouponCodes.size() <= 0) {
            return sumPrice;
        }

        //检查优惠券是否可用
        List<UserDiscountCouponVo> userDiscountCouponVos = checkUsableDiscountCoupon(productId, userDiscountCouponCodes);

        //遍历累加所有优惠券折扣金额
        BigDecimal discountMoney = new BigDecimal(0);
        for (UserDiscountCouponVo discountCouponVo : userDiscountCouponVos) {
            //通过优惠券对象，折扣前额度，获取优惠券折扣金额
            discountMoney = discountMoney.add(discountRuleContext.getDiscountMoney(discountCouponVo.getDiscountCoupon(), sumPrice));
            //删除用户优惠券
            deleteUserDiscountCoupon(discountCouponVo.getId());
        }

        //用原价减去折扣金额并返回
        return sumPrice.subtract(discountMoney);
    }

}
