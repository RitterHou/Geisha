package com.nosuchfield.geisha.test;

import com.nosuchfield.geisha.ioc.annotations.Component;
import com.nosuchfield.geisha.mvc.annotations.Param;
import com.nosuchfield.geisha.mvc.annotations.RequestMapping;
import com.nosuchfield.geisha.mvc.beans.Session;

/**
 * @author hourui 2017/10/27 22:51
 */
@Component
@RequestMapping("/name")
public class Hello {

    @RequestMapping("/person")
    public String hello(@Param("name") String name, @Param("age") String age, Session session) {
        session.set("name", name);
        return "hello " + name + ", your age is " + Integer.valueOf(age);
    }

    @RequestMapping
    public String test(Session session) {
        return "你好，" + session.get("name");
    }

}
