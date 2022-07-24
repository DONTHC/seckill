package cn.hc.service;

import cn.hc.pojo.Order;
import cn.hc.pojo.User;
import cn.hc.vo.GoodsVo;
import cn.hc.vo.OrderDetailVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(String path, User user, Long goodsId);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
