package com.nosuchfield.geisha;

import com.nosuchfield.geisha.ioc.Context;
import com.nosuchfield.geisha.mvc.Http;
import com.nosuchfield.geisha.mvc.Server;

import java.lang.reflect.InvocationTargetException;

/**
 * @author hourui 2017/10/31 14:16
 */
public class Geisha {

    /**
     * 启动服务
     *
     * @param port 监听端口号
     */
    public static void run(int port) {
        try {
            new Context();
            Http.initMapping();
            Server.start(port);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动服务（默认端口5200）
     */
    public static void run() {
        run(5200);
    }

}
