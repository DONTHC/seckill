# 秒杀项目

# 1 项目框架搭建

- SpringBoot环境搭建
- 集成Thymeleaf、MySQL和MyBatisPlus
- 创建公共返回对象RespBean（前后端分离）

需要进一步学习：MyBatisPlus的使用

# 2 分布式回话

## 2.1 用户登陆

- 数据库用户表的设计
- 明文密码二次MD5加密
- 前后端手机号（主键）的参数校验
  - 前端是正则表达式验证
  - 后端一部分采取的是SpringBoot validation组件中自带的注解，另一部分采取的是自定义的注解
- 全局异常处理，SpringBoot全局异常处理方式主要两种：
  - 使用@ControllerAdvice 和@ExceptionHandler 注解
  - 使用ErrorController类来实现
  - 二者的区别在于：
    - @ControllerAdvice 方式**只能处理控制器抛出的异常**，此时请求已经进入控制器中
    2. ErrorController类方式可以**处理所有的异常**，包括未进入控制器的错误，比如404,401等错误
    - 如果应用中两者共同存在，则@ControllerAdvice 方式处理控制器抛出的异常，ErrorController类方式处理未进入控制器的异常
    - @ControllerAdvice 方式可以定义多个拦截方法，拦截不同的异常类，并且可以获取抛出的异常信息，**自由度更大**

需要进一步学习：总结SpringBoot的全局异常处理

## 2.2 共享Session

- SpringSession：SpringBoot会自动将Session存入Redis中，如果该会话一直保持连接，那么从Session中根据Cookie值取出的用户信息是正确的。反之，如果该会话断开后再连接，因为SESSIONID的不同（不是同一个会话），根据Cookie值取出的用户信息为null，这是发生在JMeter压测过程中实例
- Redis：以Cookie为健，用户信息为值存入Redis。判断用户的登陆情况，需要根据Cookie值到Redis数据库中进行查询，这样可以避免SpringBootSession在JMeter中发生的问题

# 3 功能开发

- 商品列表
- 商品详情
- 秒杀
- 订单详情

这部分包括商品表、订单表、秒杀商品表、秒杀订单表的设计，以及基于MyBatisPlus的逆向工程的实现

# 4 系统压测

- JMeter的使用（Windows+Linus）
- JMeter自定义变量模拟多用户
- 商品列表和秒杀的压测

压测中发现如下可以改进的问题：

- 页面资源每次都要通过链接访问，这样大大增加了访问时间
- 秒杀出现超卖现象

# 5 页面优化

- 页面缓存+URL缓存+对象缓存
  - 使用模板引擎渲染页面存入Redis中
  - 对象缓存的例子之一就是之前共享Session中的第二种方法
- 页面静态化，前后端分离
  - 通过公共的返回对象+AJAX实现
- 静态资源优化

# 6 接口优化

- Redis预减库存减少数据库的访问
  - 通过项目初始化的时候加载秒杀商品的库存件数
- 内存标记减少Redis的访问
- RabbitMQ的异步下单
  - SpringBoot整合RabbitMQ
  - 交换机

# 7 安全优化

- 秒杀接口地址隐藏
- 算术验证码
- 接口防刷