package com.tools.aop.log;

import java.lang.annotation.*;

/**
 * @Auther: Tang XiaoBai
 * @Date: 2018/11/13 19:55
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {
}
