package com.ischoolbar.programmer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表注解类
 * @author llq
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String prefix() default "";//表名前缀
	String tableName();//表名
	String sufix() default "";//表名后缀
}
