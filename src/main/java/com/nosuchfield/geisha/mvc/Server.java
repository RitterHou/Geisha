package com.nosuchfield.geisha.mvc;

/**
 * @author hourui 2017/10/27 21:04
 */

import com.google.common.primitives.Bytes;
import com.nosuchfield.geisha.ioc.Beans;
import com.nosuchfield.geisha.mvc.annotations.Param;
import com.nosuchfield.geisha.mvc.beans.HttpRequest;
import com.nosuchfield.geisha.mvc.beans.MethodDetail;
import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import com.nosuchfield.geisha.utils.Constants;
import com.nosuchfield.geisha.utils.RequestUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author hourui 2017/9/7 10:48
 */
public class Server {
    private int port = 5200;

    public static void start(int port) {
        new Thread(() -> {
            try {
                Server server = new Server();
                server.port = port;
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void start() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select(); // 此处的select方法是阻塞的
            selector.selectedKeys().forEach((selectionKey -> handleKey(selectionKey, selector)));
        }
    }

    private void handleKey(SelectionKey key, Selector selector) {
        try {
            ServerSocketChannel server = null;
            SocketChannel client = null;
            if (key.isAcceptable()) {
                server = (ServerSocketChannel) key.channel();
                client = server.accept();
                if (client != null) {
                    client.configureBlocking(false);
                    // 给新的链接注册读取事件
                    client.register(selector, SelectionKey.OP_READ);
                }
            } else if (key.isReadable()) {
                client = (SocketChannel) key.channel();
                read(client);
//                 key.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读取channel数据并且写回响应内容
    private void read(SocketChannel channel) throws Exception {
        LinkedList<Byte> list = new LinkedList<>();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buf);
        // 如果读取到-1，则说明客户端关闭了该链接
        if (bytesRead == -1) {
            System.out.println(channel + " closed");
            channel.close();
            return;
        }
        while (bytesRead > 0) {
            buf.flip();
            while (buf.hasRemaining()) {
                list.add(buf.get());
            }
            buf.clear();
            bytesRead = channel.read(buf);
        }
        String request = new String(Bytes.toArray(list), Constants.DEFAULT_ENCODING);
        try {
            // 写回响应
            response(request, channel);
        } catch (Exception e) {
            e.printStackTrace();
            serverError(e.toString(), channel);
        }
    }

    /**
     * 解析请求并返回响应
     */
    private void response(String request, SocketChannel channel) throws Exception {
        HttpRequest httpRequest = RequestUtils.getRequest(request);

        String url = httpRequest.getUrl();
        RequestMethod requestMethod = httpRequest.getRequestMethod();
        MethodDetail methodDetail = UrlMappings.getInstance().getMap(url, requestMethod);

        // 如果找不到对应的匹配规则
        if (methodDetail == null) {
            notFound(channel);
            return;
        }

        Class clazz = methodDetail.getClazz();
        Object object = Beans.getInstance().getObject(clazz);
        if (object == null)
            throw new RuntimeException("can't find bean for " + clazz);

        Map<String, String> requestParam = httpRequest.getParams(); // 请求参数
        List<String> params = new ArrayList<>(); // 最终的方法参数
        Method method = methodDetail.getMethod();

        // 获取方法上所有参数的所有注解
        Annotation[][] annotations = method.getParameterAnnotations();
        for (Annotation[] annotation : annotations) {
            String name = null;
            for (Annotation ann : annotation) {
                if (ann.annotationType() == Param.class) {
                    Param param = (Param) ann;
                    name = param.value();
                    break;
                }
            }
            // 如果请求参数中存在这个参数就把该值赋给方法参数，否则赋值null
            params.add(requestParam.getOrDefault(name, null));
        }

        Object result = method.invoke(object, params.toArray());

        // 写回响应
        String str = (String) result;
        String response = "HTTP/1.1 200 OK\r\n\r\nContent-Length: " + str.getBytes().length + "\r\n\r\n" + str;
        writeData(response, channel);
    }

    /**
     * 500 Internal Server Error
     */
    private void serverError(String error, SocketChannel channel) {
        String response = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: " + error.getBytes().length + "\r\n\r\n" + error;
        writeData(response, channel);
    }

    /**
     * 404 Not Found
     */
    private void notFound(SocketChannel channel) throws IOException {
        String str = Constants.NOT_FOUND;
        String response = "HTTP/1.1 404 Not Found\r\nContent-Length: " + str.getBytes().length + "\r\n\r\n" + str;
        writeData(response, channel);
    }

    /**
     * 向连接中写数据
     *
     * @param data    数据
     * @param channel 连接
     */
    private void writeData(String data, SocketChannel channel) {
        ByteBuffer res = ByteBuffer.allocate(data.getBytes().length);
        res.clear();
        res.put(data.getBytes());
        res.flip();
        while (res.hasRemaining()) {
            try {
                channel.write(res);
            } catch (IOException e) {
                System.out.println("error when writing data");
                e.printStackTrace();
            }
        }
    }
}

