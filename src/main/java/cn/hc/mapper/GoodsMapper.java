package cn.hc.mapper;

import cn.hc.pojo.Goods;
import cn.hc.vo.GoodsVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
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
