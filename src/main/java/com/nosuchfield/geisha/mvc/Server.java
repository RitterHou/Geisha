package com.nosuchfield.geisha.mvc;

/**
 * @author hourui 2017/10/27 21:04
 */

import com.google.common.primitives.Bytes;
import com.nosuchfield.geisha.mvc.beans.HttpRequest;
import com.nosuchfield.geisha.mvc.beans.MethodDetail;
import com.nosuchfield.geisha.mvc.enums.RequestMethod;
import com.nosuchfield.geisha.utils.RequestUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

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
                // key.cancel();
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
        while (bytesRead > 0) {
            buf.flip();
            while (buf.hasRemaining()) {
                list.add(buf.get());
            }
            buf.clear();
            bytesRead = channel.read(buf);
        }
        String request = new String(Bytes.toArray(list), "utf-8");
        HttpRequest httpRequest = RequestUtils.getRequest(request);

        String url = httpRequest.getUrl();
        RequestMethod requestMethod = httpRequest.getRequestMethod();
        MethodDetail methodDetail = UrlMappings.getInstance().getMap(url, requestMethod);
        System.out.println(methodDetail);

        // 写回响应
        String str = "<h1>" + System.currentTimeMillis() + "</h1>";
        String response = "HTTP/1.1 200 OK\r\nContent-Length: " + str.length() + "\r\n\r\n" + str;
        System.out.println(response);
        ByteBuffer res = ByteBuffer.allocate(response.length());
        res.clear();
        res.put(response.getBytes());
        res.flip();
        while (res.hasRemaining()) {
            channel.write(res);
        }
    }
}

