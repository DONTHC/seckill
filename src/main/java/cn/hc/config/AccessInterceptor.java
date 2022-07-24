package cn.hc.config;

import cn.hc.pojo.User;
import cn.hc.service.IOrderService;
import cn.hc.service.IUserService;
import cn.hc.util.CookieUtil;
import cn.hc.vo.RespBean;
import cn.hc.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 通用接口限流拦截器，注意拦截器先于HandlerMethodArgumentResolver执行
 *
 * @author HCong
 * @create 2022/7/24
 */
@Component
public class AccessInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 方法执行之前
     *
     * @param request
     * @param response
     * @param handler：待执行的方法
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断后续执行的是方法
        if (handler instanceof HandlerMethod) {
            // 根据request中的cookie从redis汇总获取用户信息
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;

            // 获取方法上接口限流的用意注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            // 如果没有该注解，直接放行即可
            if (accessLimit == null) {
                return true;
            }
            // 说明second时间内访问的最大次数maxCount
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                // 需要登陆，如果用户为空，则返回错误信息
                if (user == null) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                // 否则，拼接获取redis中的限流次数key
                key += ":" + user.getId();
            }

            // 从redis中获取当前时间段已经访问的次数
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (count == null) {
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                valueOperations.increment(key);
            } else {
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 通过流的方式返回错误信息
     *
     * @param response
     * @param respBeanEnum
     * @throws IOException
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        RespBean bean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(bean));
        out.flush();
        out.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        // 从request中取出cookie
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(ticket)) {
            return null;
        }

        // 根据cookie从redis中取出用户信息
        return userService.getByUserTicket(ticket, request, response);
    }
}
