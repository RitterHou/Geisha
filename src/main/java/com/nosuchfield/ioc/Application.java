package com.nosuchfield.ioc;

import com.nosuchfield.ioc.test.Company;

/**
 * @author hourui 2017/10/18 19:26
 */
public class Application {

    public static void main(String[] args) {
        Context context = new Context();
        Company company = (Company) context.getBean(Company.class);
        company.test();
    }

}
