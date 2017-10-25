package com.nosuchfield.geisha.test;

import com.nosuchfield.geisha.annotations.Component;
import com.nosuchfield.geisha.annotations.Resource;

/**
 * @author hourui 2017/10/18 20:59
 */
@Component
public class Company {

    @Resource
    private Person person;

    @Resource
    private String name;

    public void test() {
        System.out.println(person + " hate " + name);
    }

}
