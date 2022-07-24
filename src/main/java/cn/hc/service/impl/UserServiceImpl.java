package cn.hc.service.impl;

import cn.hc.exception.GlobalException;
import cn.hc.mapper.UserMapper;
import cn.hc.pojo.User;
import cn.hc.service.IUserService;
import cn.hc.util.*;
import cn.hc.vo.LoginVo;
import cn.hc.vo.RespBean;
import cn.hc.vo.RespBeanEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HCong
 * @since 2022-07-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登陆
     *
     * @param loginVo
     * @return
     */
    @Override
    public RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        // 根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        // 校验密码
        if (!MD5Util.formPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }

        // 生成cookie，并通过cookie将用户信息存入session
        String ticket = UUIDUtil.uuid();
        // 存入Redis
        redisTemplate.opsForValue().set("user:" + ticket, JsonUtil.object2JsonStr(user));
        // 最终后台创建Cookie，并通过response.addCookie(cookie)存入response
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     *
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    @Override
    public User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        // Spring Session会自动从Redis取出Session，注意需要保证前端传过来的SESSION值的一致性，只有SESSION保持一致才能是同一个回话
        // 如果使用JMeter压测，一定不会是同一个回话
        // HttpSession session = request.getSession();
        // 根据cookie的name取出user对象，然后再次存入request中
        // User user = (User) session.getAttribute(userTicket);

        // 从Redis中取出User
        String userJson = (String) redisTemplate.opsForValue().get("user:" + userTicket);
        User user = JsonUtil.jsonStr2Object(userJson, User.class);
        if (null != user) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }

        System.out.println(user);
        return user;
    }
}
