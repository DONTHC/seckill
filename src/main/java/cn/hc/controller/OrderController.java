package cn.hc.controller;


import cn.hc.pojo.User;
import cn.hc.service.IOrderService;
import cn.hc.vo.OrderDetailVo;
import cn.hc.vo.RespBean;
import cn.hc.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    IOrderService orderService;

    /**
     * 订单详情
     *
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
