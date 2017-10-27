package com.nosuchfield.geisha.mvc.beans;

import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
