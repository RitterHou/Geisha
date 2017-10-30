package com.nosuchfield.geisha.mvc.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 保存某一个HTTP Session的信息
 *
 * @author hourui 2017/10/30 17:08
 */
public class Session {

    private Map<String, String> map = new HashMap<>();

    /**
     * 保存属性
     */
    public void set(String key, String value) {
        map.put(key, value);
    }

    /**
     * 获取属性
     */
    public String get(String key) {
        return map.get(key);
    }

    /**
     * 移除属性
     */
    public String remove(String key) {
        return map.remove(key);
    }

    /**
     * 获取所有的key
     */
    public Set<String> keys() {
        return map.keySet();
    }

}
