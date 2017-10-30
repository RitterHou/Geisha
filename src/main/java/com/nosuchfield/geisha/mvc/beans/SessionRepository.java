package com.nosuchfield.geisha.mvc.beans;

import com.nosuchfield.geisha.ioc.annotations.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 存储所有的Session
 *
 * @author hourui 2017/10/30 17:16
 */
@Component
public class SessionRepository {

    private Map<String, Session> map = new HashMap<>();

    /**
     * 创建一个Session
     */
    public String createSession() {
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        map.put(key, new Session());
        return key;
    }

    /**
     * 保存会话
     */
    public void put(String key, Session session) {
        map.put(key, session);
    }

    /**
     * 获取会话
     */
    public Session get(String key) {
        return key != null ? map.get(key) : null;
    }

    /**
     * 移除会话
     */
    public Session remove(String key) {
        return map.remove(key);
    }

    /**
     * 获取所有的key
     */
    public Set<String> keys() {
        return map.keySet();
    }

}
