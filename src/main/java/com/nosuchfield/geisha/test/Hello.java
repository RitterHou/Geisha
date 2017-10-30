package com.nosuchfield.geisha.test;

import com.nosuchfield.geisha.ioc.annotations.Component;
import com.nosuchfield.geisha.mvc.annotations.Param;
import com.nosuchfield.geisha.mvc.annotations.RequestMapping;

/**
 * @author hourui 2017/10/27 22:51
 */
@Component
@RequestMapping("/name")
public class Hello {

    @RequestMapping("/person")
    public String hello(@Param("name") String name, @Param("age") String age) {
        return "hello " + name + ", your age is " + Integer.valueOf(age);
    }

    @RequestMapping("/stu")
    public String test() {
        return "test";
    }

}
