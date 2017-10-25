package com.nosuchfield.geisha.test;

import com.nosuchfield.geisha.ioc.annotations.Bean;
import com.nosuchfield.geisha.ioc.annotations.Configuration;

/**
 * @author hourui 2017/10/19 16:27
 */
@Configuration
public class Config {

    @Bean
    public String init() {
        return "lisi";
    }

}
