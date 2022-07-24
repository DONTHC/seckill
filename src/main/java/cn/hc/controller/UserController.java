package cn.hc.controller;


import cn.hc.pojo.User;
import cn.hc.rabbitmq.MQSender;
import cn.hc.vo.RespBean;
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
 * @since 2022-07-14
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;

    /**
     * 用户信息(测试)
     *
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        System.out.println(1);
        return RespBean.success(user);
    }
}
