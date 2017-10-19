package com.nosuchfield.ioc;

import com.nosuchfield.ioc.annotations.Bean;
import com.nosuchfield.ioc.annotations.Component;
import com.nosuchfield.ioc.annotations.Configuration;
import com.nosuchfield.ioc.annotations.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author hourui 2017/10/18 19:37
 */
public class Context {

    public Context() {
        List<Class> classes = Utils.getAllClass();
        // 扫描并且创建bean，把bean保存到内存中
        classes.forEach(clazz -> {
            try {
                if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Configuration.class)) {
                    Beans.getInstance().setObject(clazz, clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 把用户自定义的Bean保存到内存中去
        classes.forEach(clazz -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 把内存中的bean注入到对象中去
        classes.forEach(clazz -> {
            try {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Resource.class)) {
                        Object classObject = Beans.getInstance().getObject(clazz);
                        Object fieldObject = Beans.getInstance().getObject(field.getType());

                        field.setAccessible(true);
                        field.set(classObject, fieldObject);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Object getBean(Class clazz) {
        return Beans.getInstance().getObject(clazz);
    }

}