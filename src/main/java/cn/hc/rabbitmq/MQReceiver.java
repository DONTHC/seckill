package cn.hc.rabbitmq;

import cn.hc.pojo.SeckillMessage;
import cn.hc.pojo.User;
import cn.hc.service.IGoodsService;
import cn.hc.service.IOrderService;
import cn.hc.util.JsonUtil;
import cn.hc.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author HCong
 * @create 2022/7/22
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接受消息：" + message);

        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        // 判断数据库库存
        if (goodsVo.getStockCount() < 1) {
            return;
        }

        // 判断是否重复抢购
        String seckillOrderJson = (String)
                redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return;
        }

        orderService.seckill(user, goodsVo);
    }
}