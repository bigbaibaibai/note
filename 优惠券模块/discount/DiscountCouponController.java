package com.visionet.controller.discount;

import com.visionet.entity.DUserDiscountCoupon;
import com.visionet.entity.DiscountCoupon;
import com.visionet.entity.OrgUser;
import com.visionet.service.DiscountCouponService;
import com.visionet.service.Impl.LactobacillusProductBaseServiceImpl;
import com.visionet.sms.SmsUtil;
import com.visionet.support.BaseController;
import com.visionet.support.ProjectException;
import com.visionet.support.ResultJSON;
import com.visionet.vo.ReceiveDiscountCouponVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Time: 2019/5/28 14:47
 * @Author: bxx
 * @Description: 优惠券Controller，期待您优化此代码
 */
@RestController
@RequestMapping("/discountCoupon")
@CrossOrigin(origins = "*", maxAge = 3600)
@Api(description = "优惠券接口")
public class DiscountCouponController extends BaseController {

    @Autowired
    private DiscountCouponService discountCouponService;

    @Autowired
    private LactobacillusProductBaseServiceImpl lactobacillusProductBaseService;

    /**
     * 判断是否是微盟渠道
     *
     * @return
     */
    @GetMapping("/isWeiMeng")
    @ApiOperation(value = "判断是否是微盟渠道", notes = "判断是否是微盟渠道")
    public ResultJSON isWeiMeng() {
        OrgUser orgUser = getCurrentExtendDataOrgUser();
        //判断是否属于微盟渠道
        if (orgUser != null && discountCouponService.checkIsWeiMeng(orgUser.getOrgCode())) {
            return ResultJSON.success();
        }

        return ResultJSON.error("非微盟下渠道！");
    }

    /**
     * 通过手机号查询用户优惠券
     *
     * @param phone
     * @return
     */
    @GetMapping("/user/{phone}")
    @ApiOperation(value = "查询用户优惠券接口", notes = "根据手机号查询用户优惠券")
    @ApiImplicitParam(name = "phone", value = "用户手机号", required = true, dataType = "String")
    public ResultJSON getDiscountCoupon(@PathVariable String phone) {
        try {
            //先查询用户所有的优惠券
            List<DUserDiscountCoupon> dUserDiscountCoupons = discountCouponService.getDiscountCouponForUser(phone);

            //把用户优惠券转换为返回前端的优惠券
            return ResultJSON.success(discountCouponService.convertDisCouToVo(dUserDiscountCoupons));
        } catch (Exception e) {
            return ResultJSON.error("查询失败！");
        }

    }

    /**
     * 我的查询优惠券
     *
     * @return
     */
    @ApiOperation(value = "查询已登录账户的优惠券", notes = "根据手机号查询已登录用户优惠券")
    @GetMapping("/getMy")
    public ResultJSON getMyDiscountCoupon() {
        //直接调用通过手机号查询优惠券
        return getDiscountCoupon(super.getCurrentOrgUser().getUserAccount());
    }

    /**
     * 发送验证码接口
     *
     * @param phone
     * @return
     */
    @GetMapping("/sendPhoneCode/{phone}")
    @ApiOperation(value = "发送验证码接口", notes = "发送验证码接口")
    public ResultJSON sendPhoneCode(@PathVariable String phone) {
        Map<String, String> map = new HashMap<>(5);
        map.put("sign", "金蒜狮");
        try {
            //发送验证码
            lactobacillusProductBaseService.sendQueryOrderSmsCode(phone, SmsUtil.DISCOUNT_COUPON, map);
            return ResultJSON.success("发送成功！");
        } catch (ProjectException e) {
            return ResultJSON.error(e.getMessage());
        } catch (Exception e) {
            return ResultJSON.error("发送失败！");
        }

    }

    /**
     * 领取优惠券
     *
     * @param discountCouponVo
     * @return
     */
    @PostMapping("/receive")
    @ApiOperation(value = "用户领取优惠券接口", notes = "根据手机号为用户添加优惠券")
    public ResultJSON receiveDiscountCoupon(@RequestBody ReceiveDiscountCouponVo discountCouponVo) {
        try {
            //校验接收数据，如果信息有误，会抛出ProjectException异常，并把信息错误原因设置到Message属性
            discountCouponService.checkReceiveDiscountCouponVo(discountCouponVo);
            //获取信息
            String discountCouponCode = discountCouponVo.getDiscountCouponCode();
            String phone = discountCouponVo.getPhone();
            //校验验证码，抛出异常同上
            lactobacillusProductBaseService.vilidaSmsCode(discountCouponVo.getPhoneCode(), phone);
            //校验优惠券领取规则，返回优惠券对象，抛出异常同上
            DiscountCoupon discountCoupon = discountCouponService.checkCanReceiveDiscountCoupon(phone, discountCouponCode);
            //添加优惠券到用户优惠券表，优惠券总数量-1
            discountCouponService.receiveDiscountCoupon(phone, discountCoupon);
            return ResultJSON.success("领取成功！");
        } catch (ProjectException e) {
            //特殊处理，如果code为1表示优惠券已经领取最大限度，需要有特殊的提示语
            if ("1".equals(e.getCode())){
                return ResultJSON.error(e.getMessage(), e.getCode());
            }

            return ResultJSON.error(e.getMessage());
        } catch (Exception e) {
            return ResultJSON.error("领取失败！");
        }

    }

    /**
     * 添加优惠券
     *
     * @param discountCoupon
     * @return
     */
//    @PostMapping()
    @ApiOperation(value = "添加优惠券接口", notes = "传入优惠券必备信息，添加优惠券")
    public ResultJSON addDiscountCoupon(@RequestBody DiscountCoupon discountCoupon) {
        try {
            //必备信息判断，如果信息有误，会抛出ProjectException异常，并把信息错误原因设置到Message属性
            discountCouponService.checkDiscountCoupon(discountCoupon);
            //防止优惠券编号重复
            if (discountCouponService.getDiscountCouponForId(discountCoupon.getDcCode()) != null) {
                return ResultJSON.error("优惠券编号已存在！");
            }
            //添加到数据库
            discountCouponService.addDiscountCoupon(discountCoupon);
            return ResultJSON.success("添加成功！");
        } catch (ProjectException e) {
            return ResultJSON.error(e.getMessage());
        } catch (Exception e) {
            return ResultJSON.error("添加失败！");
        }

    }

    /**
     * 删除优惠券
     *
     * @return
     */
//    @DeleteMapping("/{discountCouponCode}")
    @ApiOperation(value = "删除优惠券接口", notes = "根据优惠券ID删除优惠券")
    @ApiImplicitParam(name = "discountCouponCode", value = "优惠券编号", required = true, dataType = "String")
    public ResultJSON deleteDiscountCoupon(@PathVariable String discountCouponCode) {
        try {
            discountCouponService.deleteDiscountCoupon(discountCouponCode);
            return ResultJSON.success("删除成功！");
        } catch (Exception e) {
            return ResultJSON.error("删除失败！");
        }

    }

    /**
     * 修改优惠券信息
     *
     * @param discountCoupon
     * @return
     */
//    @PutMapping()
    @ApiOperation(value = "修改优惠券接口", notes = "传入优惠券信息，修改优惠券")
    public ResultJSON updateDiscountCoupon(@RequestBody DiscountCoupon discountCoupon) {
        try {
            //必备信息判断，如果信息有误，会抛出ProjectException异常，并把信息错误原因设置到Message属性
            discountCouponService.checkDiscountCoupon(discountCoupon);
            //更新到数据库
            discountCouponService.updateDiscountCoupon(discountCoupon);
            return ResultJSON.success("修改成功！");
        } catch (ProjectException e) {
            return ResultJSON.error(e.getMessage());
        } catch (Exception e) {
            return ResultJSON.error("修改失败！");
        }

    }

    /**
     * 分页获取优惠券   -----------------------------分页模式修改---------------------------
     *
     * @return
     */
//    @GetMapping("/{pageNo}/{pageSize}")
    @ApiOperation(value = "分页获取优惠券接口", notes = "分页获取优惠券接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true, dataType = "int")
    })
    public ResultJSON getDiscountCouponAll(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        try {
            List<DiscountCoupon> discountCoupons = discountCouponService.getDiscountCoupon(pageNo, pageSize);
            return ResultJSON.success(discountCoupons);
        } catch (Exception e) {
            return ResultJSON.error("查询失败！");
        }

    }

    /**
     * 通过优惠券编号获取优惠券
     *
     * @return
     */
//    @GetMapping("/one/{discountCouponCode}")
    @ApiOperation(value = "通过优惠券编号获取优惠券接口", notes = "通过优惠券编号获取优惠券接口")
    @ApiImplicitParam(name = "discountCouponCode", value = "优惠券编号", required = true, dataType = "String")
    public ResultJSON getDiscountCouponForId(@PathVariable String discountCouponCode) {
        try {
            DiscountCoupon discountCoupon = discountCouponService.getDiscountCouponForId(discountCouponCode);
            return ResultJSON.success(discountCoupon);
        } catch (Exception e) {
            return ResultJSON.error("查询失败！");
        }

    }

    /**
     * 获取所有已开发的优惠券类型
     *
     * @return
     */
//    @GetMapping("/types")
    @ApiOperation(value = "获取所有已开发的优惠券类型", notes = "获取所有已开发的优惠券类型")
    public ResultJSON getDiscountTypes() {
        return ResultJSON.success(discountCouponService.getDiscountTypes());
    }

}
