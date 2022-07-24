package cn.hc.service;

import cn.hc.pojo.SeckillOrder;
import cn.hc.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
