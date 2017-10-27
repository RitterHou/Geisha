package com.nosuchfield.geisha.mvc.annotations;

import com.nosuchfield.geisha.mvc.enums.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String value() default "/"; // 默认访问根目录

    RequestMethod method() default RequestMethod.GET;
}
