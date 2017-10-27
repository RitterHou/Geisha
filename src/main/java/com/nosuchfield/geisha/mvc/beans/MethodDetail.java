package com.nosuchfield.geisha.mvc.beans;


import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author hourui 2017/10/27 20:25
 */
@Getter
@Setter
@Builder
public class MethodDetail {

    private String url;
    private RequestMethod requestMethod;
    private Class clazz;
    private Method method;

}
