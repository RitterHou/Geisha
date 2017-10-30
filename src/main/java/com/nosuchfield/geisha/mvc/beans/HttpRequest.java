package com.nosuchfield.geisha.mvc.beans;

import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hourui 2017/10/27 21:25
 */
@Setter
@Getter
@Builder
public class HttpRequest {

    private RequestMethod requestMethod;
    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;

    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public String getCookie(String name) {
        String rawCookie = this.getHeader("Cookie");
        if (rawCookie == null)
            return null;
        String[] cookies = rawCookie.split("; ?");
        Map<String, String> map = new HashMap<>();
        for (String cookie : cookies) {
            String[] c = cookie.split("=");
            map.put(c[0], c[1]);
        }
        return map.get(name);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestMethod=" + requestMethod +
                ", url='" + url + '\'' +
                ", params=" + params +
                ", headers=" + headers +
                '}';
    }

}
