package com.tools.aop.validate;

import com.tools.response.Response;
import com.tools.validate.ValidateException;
import com.tools.validate.ValidateHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Auther: Tang XiaoBai
 * @Date: 2018/11/13 17:09
 * @Description:
 */
@Aspect
@Component
public class MyValidAop {
    @Around("@annotation(MyValid)")
    @Order(5)
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        for (Object o : objects) {
            try {
                ValidateHelper.validate(o);
            } catch (ValidateException e) {
                String errCode = getErrCode(joinPoint);
                return Response.failed(errCode, e.getMessage());
            }
        }
        return joinPoint.proceed();
    }

    private static String getErrCode(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] method = targetClass.getMethods();
        String errCode = "1002";
        for (Method m : method) {
            if (m.getName().equals(methodName)) {
                Class[] tmpCs = m.getParameterTypes();
                if (tmpCs.length == arguments.length) {
                    MyValid methodCache = m.getAnnotation(MyValid.class);
                    if (methodCache != null) {
                        errCode = methodCache.errCode();
                    }
                    break;
                }
            }
        }
        return errCode;
    }
}
