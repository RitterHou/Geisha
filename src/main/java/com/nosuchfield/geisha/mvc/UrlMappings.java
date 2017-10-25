package com.nosuchfield.geisha.mvc;

import java.util.HashMap;
import java.util.Map;

public class UrlMappings {

    private static UrlMappings urlMappings;

    public static UrlMappings getInstance() {
        if (urlMappings == null)
            urlMappings = new UrlMappings();
        return urlMappings;
    }

    private Map<String, Object> map = new HashMap<>();

    public Object getObject(String url) {
        return map.get(url);
    }

    public void setObject(String url, Object object) {
        map.put(url, object);
    }

}
