package com.nosuchfield.geisha.mvc;


import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * 与某个HTTP的URL映射的Java方法的详细信息
 *
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
