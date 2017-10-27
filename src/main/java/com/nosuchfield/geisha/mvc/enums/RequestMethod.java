package com.nosuchfield.geisha.mvc.enums;

public enum RequestMethod {

    GET("get"),

    HEAD("head"),

    POST("post"),

    PUT("put"),

    DELETE("delete");

    private String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RequestMethod getEnum(String value) {
        for (RequestMethod requestMethod : RequestMethod.values()) {
            if (requestMethod.getValue().equals(value)) {
                return requestMethod;
            }
        }
        return null;
    }

}
