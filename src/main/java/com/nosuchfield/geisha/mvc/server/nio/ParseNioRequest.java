package com.nosuchfield.geisha.mvc.server.nio;

import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import com.nosuchfield.geisha.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析NIO服务器的HTTP请求
 *
 * @author hourui 2017/10/27 21:23
 */
@Slf4j
public class ParseNioRequest {

    public static HttpRequest getRequest(String request) {
        HttpParser parser = new HttpParser(request);
        try {
            int status = parser.parseRequest();
            if (status != 200) {
                log.error("error in parsing [{}]", request);
                throw new RuntimeException("Error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parser.getHttpRequest();
    }

    static class HttpParser {
        private String[] request;
        private HttpRequest httpRequest;

        public HttpParser(String request) {
            this.request = request.split(System.getProperty("line.separator"));
            httpRequest = new HttpRequest();
        }

        public int parseRequest() throws UnsupportedEncodingException {
            String initial = request[0]; // 请求的第一行
            if (initial == null || initial.length() == 0 || Character.isWhitespace(initial.charAt(0))) return 400;

            String[] commands = initial.split("\\s");
            if (commands.length != 3) return 400;

            RequestMethod method = RequestMethod.getEnum(commands[0]);
            if (method == null) return 400;

            String url = commands[1];
            String version = commands[2];
            Map<String, String> params = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            Map<String, String> data = new HashMap<>();

            // 获取url上面的参数
            int idx = url.indexOf('?');
            if (idx > 0) {
                String[] paramsArray = url.substring(idx + 1).split("&");
                url = URLDecoder.decode(url.substring(0, idx), Constants.DEFAULT_ENCODING);

                for (String param : paramsArray) {
                    String[] tmp = param.split("=");
                    if (tmp.length == 2) {
                        params.put(URLDecoder.decode(tmp[0], Constants.DEFAULT_ENCODING),
                                URLDecoder.decode(tmp[1], Constants.DEFAULT_ENCODING));
                    }
                }
            }

            // 获取header
            for (int i = 1; i < request.length; i++) {
                if (request[i].equals(Constants.CRLF))
                    break;
                String line = request[i];
                String[] map = line.split(":");
                if (map.length == 2)
                    headers.put(map[0].trim().toLowerCase(), map[1].trim());
            }

            // 获取HTTP body中的参数，GET和HEAD方法没有body
            if (method == RequestMethod.POST || method == RequestMethod.PUT || method == RequestMethod.DELETE) {

            }

            httpRequest.setRequestMethod(method);
            httpRequest.setUrl(url);
            httpRequest.setVersion(version);
            params.putAll(data); // 把data添加到params中
            httpRequest.setParams(params);
            httpRequest.setHeaders(headers);

            return 200;
        }

        public HttpRequest getHttpRequest() {
            return httpRequest;
        }

    }

}
