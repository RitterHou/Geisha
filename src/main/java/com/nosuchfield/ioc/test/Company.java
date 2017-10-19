package com.nosuchfield.ioc.test;

import com.nosuchfield.ioc.annotations.Component;
import com.nosuchfield.ioc.annotations.Resource;

/**
 * @author hourui 2017/10/18 20:59
 */
@Component
public class Company {

    @Resource
    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public void test() {
        System.out.println(person);
    }

}
