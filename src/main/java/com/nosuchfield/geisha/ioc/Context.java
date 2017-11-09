package com.nosuchfield.geisha.ioc;

import com.nosuchfield.geisha.ioc.annotations.Bean;
import com.nosuchfield.geisha.ioc.annotations.Component;
import com.nosuchfield.geisha.ioc.annotations.Configuration;
import com.nosuchfield.geisha.ioc.annotations.Resource;
import com.nosuchfield.geisha.utils.PackageListUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 实现依赖注入
 *
 * @author hourui 2017/10/18 19:37
 */
@Slf4j
public class Context {

    // 创建 -> 保存 -> 获取 -> 使用

    public static void init() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<Class> classes = PackageListUtils.getAllClass();
        // 扫描类并且创建bean，把bean保存到内存中
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Configuration.class)) {
                log.info("Created bean for [{}]", clazz);
                BeansPool.getInstance().setObject(clazz, clazz.newInstance());
            }
        }
        // 把用户自定义的Bean保存到内存中去
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Configuration.class)) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        Object classObject = BeansPool.getInstance().getObject(clazz);
                        Object o = method.invoke(classObject); // 获取方法的返回值对象
                        log.info("Created bean for [{}] by method [{}]", o.getClass(), method);
                        BeansPool.getInstance().setObject(o.getClass(), o);
                    }
                }
            }
        }
        // 把内存中的bean注入到对象中去
        for (Class clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Resource.class)) {
                    Object classObject = BeansPool.getInstance().getObject(clazz);
                    Object fieldObject = BeansPool.getInstance().getObject(field.getType());

                    field.setAccessible(true);
                    field.set(classObject, fieldObject);
                    log.info("Inject bean [{}] type [{}] into [{}]", fieldObject, field.getType(), clazz);
                }
            }
        }
    }

    public static Object getBean(Class clazz) {
        return BeansPool.getInstance().getObject(clazz);
    }

}