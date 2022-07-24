package cn.hc.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通用接口限流注解
 *
 * @author HCong
 * @create 2022/7/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int second();

    int maxCount();

    boolean needLogin() default true;  // 判断是否需要登陆
}
