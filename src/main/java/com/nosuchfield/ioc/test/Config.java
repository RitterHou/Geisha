package com.nosuchfield.ioc.test;

import com.nosuchfield.ioc.annotations.Bean;
import com.nosuchfield.ioc.annotations.Configuration;

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
