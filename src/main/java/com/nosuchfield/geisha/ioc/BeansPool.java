package com.nosuchfield.geisha.ioc;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存所有的bean
 *
 * @author hourui 2017/10/18 21:01
 */
public class BeansPool {

    private static BeansPool beansPool;

    public static BeansPool getInstance() {
        if (beansPool == null)
            beansPool = new BeansPool();
        return beansPool;
    }

    // 根据类型保存对象
    private Map<Class, Object> map = new HashMap<>();

    public Object getObject(Class clazz) {
        return map.get(clazz);
    }

    public void setObject(Class clazz, Object object) {
        map.put(clazz, object);
    }
}
