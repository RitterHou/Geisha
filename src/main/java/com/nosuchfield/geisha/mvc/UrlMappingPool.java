package com.nosuchfield.geisha.mvc;

import com.nosuchfield.geisha.mvc.enums.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存HTTP请求与Java方法的映射关系
 */
public class UrlMappingPool {

    private static UrlMappingPool urlMappingPool;

    public static UrlMappingPool getInstance() {
        if (urlMappingPool == null)
            urlMappingPool = new UrlMappingPool();
        return urlMappingPool;
    }

    private List<MethodDetail> methodDetails = new ArrayList<>();

    public MethodDetail getMap(String url, RequestMethod requestMethod) {
        for (MethodDetail methodDetail : methodDetails) {
            if (methodDetail.getUrl().equals(url) && methodDetail.getRequestMethod() == requestMethod)
                return methodDetail;
        }
        return null;
    }

    public void setMap(String url, Class clazz, Method method, RequestMethod requestMethod) {
        MethodDetail methodDetail = MethodDetail.builder()
                .url(url)
                .clazz(clazz)
                .method(method)
                .requestMethod(requestMethod)
                .build();

        methodDetails.add(methodDetail);
    }

    public void setMap(String url, MethodDetail methodDetail) {
        methodDetails.add(methodDetail);
    }

}
