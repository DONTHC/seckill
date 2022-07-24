package cn.hc.mapper;

import cn.hc.pojo.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HCong
 * @since 2022-07-16
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
