package com.tools.aop.log;
import cn.hutool.json.JSONUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Tang XiaoBai
 * @Date: 2018/11/13 19:57
 * @Description:
 */
@Aspect
@Component
public class MyLogAop {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around(value = "@annotation(MyLog)")
    @Order(10)
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getDeclaringTypeName() +
                "." + joinPoint.getSignature().getName();
        List<Object> param = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof ServletRequest || o instanceof ServletResponse) {
                param.add(o);
            } else {
                String s = JSONUtil.toJsonStr(o);
                param.add(s);
            }
        }
        long start = System.currentTimeMillis();
        Object ret = joinPoint.proceed();
        long end = System.currentTimeMillis();
        logger.info("调用方法：{}，消耗时间：{}毫秒, 参数为：{}， 返回值为：{}", methodName, end - start,param, JSONUtil.toJsonStr(ret));

        return ret;
    }
}
