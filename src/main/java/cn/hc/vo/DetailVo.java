package cn.hc.vo;

import cn.hc.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HCong
 * @create 2022/7/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int seckillStatus;
    private int remainSeconds;
}
