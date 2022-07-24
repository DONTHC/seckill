package cn.hc.controller;

import cn.hc.pojo.User;
import cn.hc.service.IGoodsService;
import cn.hc.vo.DetailVo;
import cn.hc.vo.GoodsVo;
import cn.hc.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author HCong
 * @create 2022/7/15
 */
@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转商品列表页
     * 页面资源的静态化处理同样存在重复代码，能否有统一的接口实现部分代码
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, User user) {
        System.out.println(user);
        // Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String goodsList = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(goodsList)) {
            return goodsList;
        }

        // 如果为空，手动渲染并存入Redis
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        goodsList = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(goodsList)) {
            // 设置过期时间，便于刷新页面
            valueOperations.set("goodsList", goodsList, 60, TimeUnit.SECONDS);
        }

        return goodsList;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId) {
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        // 剩余开始时间
        int remainSeconds = 0;
        // 秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }

        DetailVo detailVo = new DetailVo(user, goods, secKillStatus, remainSeconds);
        return RespBean.success(detailVo);
    }
}
