package com.nosuchfield.geisha.mvc.server.nio;

import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author hourui 2017/10/27 21:25
 */
@Setter
@Getter
@NoArgsConstructor
public class HttpRequest {

    private RequestMethod requestMethod;
    private String url;
    private Map<String, String> headers;
    private Map<String, String> params;
    private String version;

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
