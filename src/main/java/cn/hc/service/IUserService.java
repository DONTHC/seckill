package cn.hc.service;

import cn.hc.pojo.User;
import cn.hc.vo.LoginVo;
import cn.hc.vo.RespBean;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HCong
 * @since 2022-07-14
 */
public interface IUserService extends IService<User> {

    RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
