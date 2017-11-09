package com.nosuchfield.geisha.mvc.server.nio;

import com.google.common.primitives.Bytes;
import com.nosuchfield.geisha.ioc.BeansPool;
import com.nosuchfield.geisha.mvc.MethodDetail;
import com.nosuchfield.geisha.mvc.UrlMappingPool;
import com.nosuchfield.geisha.mvc.annotations.Param;
import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import com.nosuchfield.geisha.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
 * @author hourui 2017/10/27 21:04
 */
@Slf4j
public class NioServer {
    private int port;
    private Selector selector;

    public static void start(int port) {
        new Thread(() -> {
            try {
                NioServer server = new NioServer();
                server.port = port;
                log.info("NioServer is running on http://127.0.0.1:{}", port);
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

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select(); // 此处的select方法是阻塞的
            // 对所有的key做一次遍历，由key本身判断此事件是否与自己有关
            selector.selectedKeys().forEach((this::handleKey));
        }
    }

    private void handleKey(SelectionKey key) {
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
                    log.info("Open channel {}", client.getRemoteAddress());
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
            log.info("Close channel {}", channel.getRemoteAddress());
            channel.close();
            return;
        }
        // 非阻塞IO可以读取0个字节，这种数据应该手动丢弃
        if (bytesRead == 0) return;

        // 读取所有的数据
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
            // 返回错误信息
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            serverError(stringWriter.toString(), channel);
        }
    }

    /**
     * 解析请求并返回响应
     */
    private void response(String request, SocketChannel channel) throws Exception {
        HttpRequest httpRequest = ParseNioRequest.getRequest(request);

        String url = httpRequest.getUrl();
        RequestMethod requestMethod = httpRequest.getRequestMethod();
        log.info("{} {}", requestMethod, url);
        MethodDetail methodDetail = UrlMappingPool.getInstance().getMap(url, requestMethod);

        // 如果找不到对应的匹配规则
        if (methodDetail == null) {
            notFound(channel);
            return;
        }

        Class clazz = methodDetail.getClazz();
        Object object = BeansPool.getInstance().getObject(clazz);
        if (object == null)
            throw new RuntimeException("can't find bean for " + clazz);

        Map<String, String> requestParam = httpRequest.getParams(); // 请求参数
        List<String> params = new ArrayList<>(); // 最终的方法参数
        Method method = methodDetail.getMethod();

        // 获取方法的所有的参数
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            String name = null;
            // 获取参数上所有的注解
            Annotation[] annotations = parameter.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Param.class) {
                    Param param = (Param) annotation;
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
        String response = "HTTP/1.1 200 OK" + Constants.CRLF + "Content-Length: "
                + str.getBytes(Constants.DEFAULT_ENCODING).length + Constants.CRLF_2 + str;
        writeData(response, channel);
    }

    /**
     * 500 Internal Server Error
     */
    private void serverError(String error, SocketChannel channel) throws UnsupportedEncodingException {
        String response = "HTTP/1.1 500 Internal Server Error" + Constants.CRLF + "Content-Length: "
                + error.getBytes(Constants.DEFAULT_ENCODING).length + Constants.CRLF_2 + error;
        writeData(response, channel);
    }

    /**
     * 404 Not Found
     */
    private void notFound(SocketChannel channel) throws IOException {
        String str = Constants.NOT_FOUND;
        String response = "HTTP/1.1 404 Not Found" + Constants.CRLF + "Content-Length: "
                + str.getBytes(Constants.DEFAULT_ENCODING).length + Constants.CRLF_2 + str;
        writeData(response, channel);
    }

    /**
     * 向连接中写数据
     *
     * @param data    数据
     * @param channel 连接
     */
    private void writeData(String data, SocketChannel channel) throws UnsupportedEncodingException {
        ByteBuffer res = ByteBuffer.allocate(data.getBytes(Constants.DEFAULT_ENCODING).length);
        res.clear();
        res.put(data.getBytes(Constants.DEFAULT_ENCODING));
        res.flip();
        while (res.hasRemaining()) {
            try {
                channel.write(res);
            } catch (IOException e) {
                log.error("error when writing data");
                e.printStackTrace();
            }
        }
    }
}

