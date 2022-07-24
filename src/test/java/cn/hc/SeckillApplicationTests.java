package cn.hc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
//    @Autowired
//    private RedisScript<Boolean> script;

    /**
     * 利用setIfAbsent实现分布式锁
     * 缺点：程序执行过程中抛异常或者挂机导致del指定没法调用形成死锁
     */
    @Test
    public void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 利用setIfAbsent设置锁
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        // 如果加锁成功则进行执行
        if (isLock) {
            valueOperations.set("name", "HCong");
            String name = (String) valueOperations.get("name");
            System.out.println(name);
            // int num = 1 / 0;  // 模拟异常
            redisTemplate.delete("k1");
        } else {
            // 否则失败
            System.out.println("资源已被占用，请稍后尝试！");
        }
    }

    /**
     * 利用setIfAbsent实现分布式锁，并且添加超时时间防止死锁
     * 缺点：一旦程序运行时间过长（网络波动），会造成锁被提前删除。（造成的结果可能就是线程删除了其他线程的锁）
     */
    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 利用setIfAbsent设置锁
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        // 如果加锁成功则进行执行
        if (isLock) {
            valueOperations.set("name", "HCong");
            String name = (String) valueOperations.get("name");
            System.out.println(name);
            // int num = 1 / 0;  // 模拟异常
            redisTemplate.delete("k1");
        } else {
            // 否则失败
            System.out.println("资源已被占用，请稍后尝试！");
        }
    }

    /**
     * 方法二的解决方案就是：规定了谁上的锁，谁才能删除，可以使用Lua脚本保持多个Redis命令的原子性
     */
//    @Test
//    public void testLock03() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//
//        // 利用setIfAbsent设置锁
//        String value = UUID.randomUUID().toString();
//        Boolean isLock = valueOperations.setIfAbsent("k1", value, 5, TimeUnit.SECONDS);
//        // 如果加锁成功则进行执行
//        if (isLock) {
//            valueOperations.set("name", "HCong");
//            String name = (String) valueOperations.get("name");
//            System.out.println(name);
//            System.out.println(valueOperations.get("k1"));
//            // int num = 1 / 0;  // 模拟异常
//            Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), value);
//            System.out.println(result);
//        } else {
//            // 否则失败
//            System.out.println("资源已被占用，请稍后尝试！");
//        }
//    }


}
