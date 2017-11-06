package com.nosuchfield.geisha;

import com.nosuchfield.geisha.ioc.Context;
import com.nosuchfield.geisha.mvc.RequestScanner;
import com.nosuchfield.geisha.mvc.server.jetty.JettyServer;
import com.nosuchfield.geisha.test.Company;

import java.lang.reflect.InvocationTargetException;

/**
 * @author hourui 2017/11/6 22:58
 */
public class Test {

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Context.init();
        RequestScanner.initMapping();
        JettyServer.start(8080);

        Company company = (Company) Context.getBean(Company.class);
        company.test();
    }

}
