package com.jjs.ddl.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: ddl 事务管理
 * @Version: 1.0
 * @Author: zhangrenhua
 * @Date: Created in 10:03 2022/1/13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DdlTransactional {

}
