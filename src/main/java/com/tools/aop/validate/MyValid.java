package com.tools.aop.validate;
import java.lang.annotation.*;

/**
 * @Auther: Tang XiaoBai
 * @Date: 2018/11/13 17:05
 * @Description: 该注解是对hibernate validate的框架封装，用来校验参数，只要在方法上
 * 添加该注解，注意方法的返回类型是ReturnMessage类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyValid {
    /**
     * 返回的错误码，默认为1002
     *
     * @return
     */
    String errCode() default "1002";
}
