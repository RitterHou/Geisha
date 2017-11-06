package com.nosuchfield.geisha;

import com.nosuchfield.geisha.ioc.Context;
import com.nosuchfield.geisha.mvc.RequestScanner;
import com.nosuchfield.geisha.mvc.server.jetty.JettyServer;
import com.nosuchfield.geisha.mvc.server.nio.NioServer;
import com.nosuchfield.geisha.utils.ConfigUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author hourui 2017/10/31 14:16
 */
public class Geisha {

    /**
     * 启动服务
     */
    public static void run() {
        try {
            Context.init(); // 依赖注入
            RequestScanner.initMapping(); // 初始化HTTP请求映射
            // 获取服务器配置
            Map<String, Object> config = ConfigUtil.getConfig();
            String server = (String) config.get("server");
            int port = (Integer) config.get("port");
            // 启动HTTP服务器
            if ("jetty".equals(server))
                JettyServer.start(port);
            else if ("nio".equals(server))
                NioServer.start(port);
            else
                throw new RuntimeException("Unknown server type [" + server + "]");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
