package com.nosuchfield.geisha;

import com.nosuchfield.geisha.ioc.Context;
import com.nosuchfield.geisha.mvc.Http;
import com.nosuchfield.geisha.mvc.Server;
import com.nosuchfield.geisha.test.Company;

import java.lang.reflect.InvocationTargetException;

/**
 * @author hourui 2017/10/18 19:26
 */
public class Application {

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Context context = new Context();
        Http.initMapping();
        Server.start(8080);

        Company company = (Company) context.getBean(Company.class);
        company.test();
    }

}
