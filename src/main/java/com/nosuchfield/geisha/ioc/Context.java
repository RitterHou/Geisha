package com.nosuchfield.geisha.ioc;

import com.nosuchfield.geisha.ioc.annotations.Bean;
import com.nosuchfield.geisha.ioc.annotations.Component;
import com.nosuchfield.geisha.ioc.annotations.Configuration;
import com.nosuchfield.geisha.ioc.annotations.Resource;
import com.nosuchfield.geisha.utils.PackageListUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author hourui 2017/10/18 19:37
 */
public class Context {

    // 创建 -> 保存 -> 获取 -> 使用

    public Context() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<Class> classes = PackageListUtils.getAllClass();
        // 扫描类并且创建bean，把bean保存到内存中
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Configuration.class)) {
                Beans.getInstance().setObject(clazz, clazz.newInstance());
            }
        }
        // 把用户自定义的Bean保存到内存中去
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Configuration.class)) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        Object classObject = Beans.getInstance().getObject(clazz);
                        Object o = method.invoke(classObject); // 获取方法的返回值对象
                        Beans.getInstance().setObject(o.getClass(), o);
                    }
                }
            }
        }
        // 把内存中的bean注入到对象中去
        for (Class clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Resource.class)) {
                    Object classObject = Beans.getInstance().getObject(clazz);
                    Object fieldObject = Beans.getInstance().getObject(field.getType());

                    field.setAccessible(true);
                    field.set(classObject, fieldObject);
                }
            }
        }
    }

    public Object getBean(Class clazz) {
        return Beans.getInstance().getObject(clazz);
    }

}