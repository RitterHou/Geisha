package com.nosuchfield.ioc;

import com.nosuchfield.ioc.annotations.Component;
import com.nosuchfield.ioc.annotations.Resource;
import com.nosuchfield.ioc.test.Company;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author hourui 2017/10/18 19:37
 */
public class Core {

    public void run() {
        List<String> classes = Utils.getAllClassName();
        // 扫描并且创建bean，把bean保存到内存中
        classes.forEach(path -> {
            try {
                Class clazz = Class.forName(path);
                if (clazz.isAnnotationPresent(Component.class)) {
                    Component component = (Component) clazz.getAnnotation(Component.class);
                    Beans.getInstance().setObject(clazz, clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 把内存中的bean注入到对象中去
        classes.forEach(path -> {
            try {
                Class clazz = Class.forName(path);
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Resource.class)) {
                        Resource resource = (Resource) field.getAnnotation(Resource.class);
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

        Company company = (Company) Beans.getInstance().getObject(Company.class);
        company.test();
    }
}