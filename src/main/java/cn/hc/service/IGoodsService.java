package cn.hc.service;

import cn.hc.pojo.Goods;
import cn.hc.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 获取商品列表
     *
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 根据商品id获取商品详情
     *
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
