package com.nosuchfield.geisha.mvc;

import com.nosuchfield.geisha.mvc.annotations.RequestMapping;
import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import com.nosuchfield.geisha.utils.PackageListUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class RequestScanner {

    /**
     * 初始化HTTP请求的映射
     */
    public static void initMapping() {
        List<Class> classes = PackageListUtils.getAllClass();

        for (Class clazz : classes) {
            String classUrl = null;
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                classUrl = requestMapping.value();
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String methodUrl = requestMapping.value();
                    RequestMethod requestMethod = requestMapping.method(); // 只看方法上的HTTP METHOD

                    methodUrl = classUrl == null ? methodUrl : classUrl + methodUrl;
                    log.info("Mapped URL [{} {}] onto handler of type [{}]", requestMethod.getValue(), methodUrl, method);
                    UrlMappingPool.getInstance().setMap(methodUrl, clazz, method, requestMethod);
                }
            }
        }

    }

}
