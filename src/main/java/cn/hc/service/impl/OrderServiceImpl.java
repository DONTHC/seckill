package cn.hc.service.impl;

import cn.hc.exception.GlobalException;
import cn.hc.mapper.OrderMapper;
import cn.hc.pojo.Order;
import cn.hc.pojo.SeckillGoods;
import cn.hc.pojo.SeckillOrder;
import cn.hc.pojo.User;
import cn.hc.service.IGoodsService;
import cn.hc.service.IOrderService;
import cn.hc.service.ISeckillGoodsService;
import cn.hc.service.ISeckillOrderService;
import cn.hc.util.JsonUtil;
import cn.hc.util.MD5Util;
import cn.hc.util.UUIDUtil;
import cn.hc.vo.GoodsVo;
import cn.hc.vo.OrderDetailVo;
import cn.hc.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     *
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new
                QueryWrapper<SeckillGoods>().eq("goods_id",
                goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

        // 减库存时判断库存是否足够
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1").eq(
                "goods_id", goods.getId()).gt("stock_count", 0));
        // 判断库存是否大于0
        if (seckillGoods.getStockCount() < 1) {
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }

        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        // 将秒杀订单信息存入Redis，方便判断是否重复抢购时进行查询
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" +
                goods.getId(), JsonUtil.object2JsonStr(seckillOrder));

        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (null == orderId) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setGoodsVo(goodsVo);
        detail.setOrder(order);
        return detail;
    }

    /**
     * 生成秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 验证秒杀地址
     *
     * @param path
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(String path, User user, Long goodsId) {
        if (user == null || StringUtils.isEmpty(path)) {
            return false;
        }
        String str = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(str);
    }

    /**
     * 校验验证码
     *
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (null == user || goodsId < 0 || null == captcha) {
            return false;
        }

        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return redisCaptcha.equals(captcha);
    }
}
