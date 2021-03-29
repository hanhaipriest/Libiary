package com.ischoolbar.programmer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库列注解类
 * @author llq
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	String name();
	boolean autoIncrease() default false;
	boolean primaryKey() default false;
	boolean updateEnable() default true;//可更新，默认是true
	boolean isForeignEntity() default false;//是否是自定义对象
	boolean autoCreateTime() default false;//自动创建时间
}
