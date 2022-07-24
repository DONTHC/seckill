package cn.hc.config;

import cn.hc.pojo.User;

/**
 * 高并发下多线程的用户信息存储
 *
 * @author HCong
 * @create 2022/7/24
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static User getUser() {
        return userHolder.get();
    }

    public static void setUser(User user) {
        userHolder.set(user);
    }
}
