package cn.hc.mapper;

import cn.hc.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HCong
 * @since 2022-07-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
