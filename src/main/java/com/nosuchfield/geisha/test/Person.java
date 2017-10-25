package com.nosuchfield.geisha.test;

import com.nosuchfield.geisha.annotations.Component;

/**
 * @author hourui 2017/10/18 20:59
 */
@Component
public class Person {

    private String name = "zhangsan";

    @Override
    public String toString() {
        return name;
    }

}
