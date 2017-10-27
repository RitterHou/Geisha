package com.nosuchfield.geisha.utils;

import com.nosuchfield.geisha.mvc.beans.HttpRequest;
import com.nosuchfield.geisha.mvc.enums.RequestMethod;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hourui 2017/10/27 21:23
 */
public class RequestUtils {

    public static HttpRequest getRequest(String request) {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpParser parser = new HttpParser(inputStream);
        try {
            parser.parseRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dateHeader = parser.getDateHeader();
        Map<String, String> headers = parser.getHeaders();
        String method = parser.getMethod();
        Map<String, String> params = parser.getParams();
        String url = parser.getRequestURL();

        return HttpRequest.builder()
                .url(url)
                .requestMethod(RequestMethod.getEnum(method.toLowerCase()))
                .headers(headers)
                .params(params)
                .build();
    }

    static class HttpParser {
        private final String[][] HttpReplies = {{"100", "Continue"},
                {"101", "Switching Protocols"},
                {"200", "OK"},
                {"201", "Created"},
                {"202", "Accepted"},
                {"203", "Non-Authoritative Information"},
                {"204", "No Content"},
                {"205", "Reset Content"},
                {"206", "Partial Content"},
                {"300", "Multiple Choices"},
                {"301", "Moved Permanently"},
                {"302", "Found"},
                {"303", "See Other"},
                {"304", "Not Modified"},
                {"305", "Use Proxy"},
                {"306", "(Unused)"},
                {"307", "Temporary Redirect"},
                {"400", "Bad Request"},
                {"401", "Unauthorized"},
                {"402", "Payment Required"},
                {"403", "Forbidden"},
                {"404", "Not Found"},
                {"405", "Method Not Allowed"},
                {"406", "Not Acceptable"},
                {"407", "Proxy Authentication Required"},
                {"408", "Request Timeout"},
                {"409", "Conflict"},
                {"410", "Gone"},
                {"411", "Length Required"},
                {"412", "Precondition Failed"},
                {"413", "Request Entity Too Large"},
                {"414", "Request-URI Too Long"},
                {"415", "Unsupported Media Type"},
                {"416", "Requested Range Not Satisfiable"},
                {"417", "Expectation Failed"},
                {"500", "Internal Server Error"},
                {"501", "Not Implemented"},
                {"502", "Bad Gateway"},
                {"503", "Service Unavailable"},
                {"504", "Gateway Timeout"},
                {"505", "HTTP Version Not Supported"}};

        private BufferedReader reader;
        private String method, url;
        private Map<String, String> headers, params;
        private int[] ver;

        public HttpParser(InputStream is) {
            reader = new BufferedReader(new InputStreamReader(is));
            method = "";
            url = "";
            headers = new ConcurrentHashMap<>();
            params = new ConcurrentHashMap<>();
            ver = new int[2];
        }

        public int parseRequest() throws IOException {
            String initial, prms[], cmd[], temp[];
            int ret, idx, i;

            ret = 200; // default is OK now
            initial = reader.readLine();
            if (initial == null || initial.length() == 0) return 0;
            if (Character.isWhitespace(initial.charAt(0))) {
                // starting whitespace, return bad request
                return 400;
            }

            cmd = initial.split("\\s");
            if (cmd.length != 3) {
                return 400;
            }

            if (cmd[2].indexOf("HTTP/") == 0 && cmd[2].indexOf('.') > 5) {
                temp = cmd[2].substring(5).split("\\.");
                try {
                    ver[0] = Integer.parseInt(temp[0]);
                    ver[1] = Integer.parseInt(temp[1]);
                } catch (NumberFormatException nfe) {
                    ret = 400;
                }
            } else ret = 400;

            if (cmd[0].equals("GET") || cmd[0].equals("HEAD")) {
                method = cmd[0];

                idx = cmd[1].indexOf('?');
                if (idx < 0) url = cmd[1];
                else {
                    url = URLDecoder.decode(cmd[1].substring(0, idx), "ISO-8859-1");
                    prms = cmd[1].substring(idx + 1).split("&");

                    params = new Hashtable();
                    for (i = 0; i < prms.length; i++) {
                        temp = prms[i].split("=");
                        if (temp.length == 2) {
                            // we use ISO-8859-1 as temporary charset and then
                            // String.getBytes("ISO-8859-1") to get the data
                            params.put(URLDecoder.decode(temp[0], "ISO-8859-1"),
                                    URLDecoder.decode(temp[1], "ISO-8859-1"));
                        } else if (temp.length == 1 && prms[i].indexOf('=') == prms[i].length() - 1) {
                            // handle empty string separatedly
                            params.put(URLDecoder.decode(temp[0], "ISO-8859-1"), "");
                        }
                    }
                }
                parseHeaders();
                if (headers == null) ret = 400;
            } else if (cmd[0].equals("POST")) {
                ret = 501; // not implemented
            } else if (ver[0] == 1 && ver[1] >= 1) {
                if (cmd[0].equals("OPTIONS") ||
                        cmd[0].equals("PUT") ||
                        cmd[0].equals("DELETE") ||
                        cmd[0].equals("TRACE") ||
                        cmd[0].equals("CONNECT")) {
                    ret = 501; // not implemented
                }
            } else {
                // meh not understand, bad request
                ret = 400;
            }

            if (ver[0] == 1 && ver[1] >= 1 && getHeader("Host") == null) {
                ret = 400;
            }

            return ret;
        }

        private void parseHeaders() throws IOException {
            String line;
            int idx;

            // that fscking rfc822 allows multiple lines, we don't care now
            line = reader.readLine();
            while (!line.equals("")) {
                idx = line.indexOf(':');
                if (idx < 0) {
                    headers = null;
                    break;
                } else {
                    headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx + 1).trim());
                }
                line = reader.readLine();
            }
        }

        public String getMethod() {
            return method;
        }

        public String getHeader(String key) {
            if (headers != null)
                return (String) headers.get(key.toLowerCase());
            else return null;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getRequestURL() {
            return url;
        }

        public String getParam(String key) {
            return (String) params.get(key);
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String getVersion() {
            return ver[0] + "." + ver[1];
        }

        public int compareVersion(int major, int minor) {
            if (major < ver[0]) return -1;
            else if (major > ver[0]) return 1;
            else if (minor < ver[1]) return -1;
            else if (minor > ver[1]) return 1;
            else return 0;
        }

        public String getHttpReply(int codevalue) {
            String key, ret;
            int i;

            ret = null;
            key = "" + codevalue;
            for (i = 0; i < HttpReplies.length; i++) {
                if (HttpReplies[i][0].equals(key)) {
                    ret = codevalue + " " + HttpReplies[i][1];
                    break;
                }
            }

            return ret;
        }

        public String getDateHeader() {
            SimpleDateFormat format;
            String ret;

            format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            ret = "Date: " + format.format(new Date()) + " GMT";

            return ret;
        }
    }

}
