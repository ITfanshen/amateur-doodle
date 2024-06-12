package YouChat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server {
    //定义Map集合用于存储用户的Socket以及用户的名字
    public final static Map<Socket, String> socketNameMap = Collections.synchronizedMap(new HashMap<>());
    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);
        server.execute();
    }

    public void execute() throws IOException {
        while (true) {
            // 监听客户端套接字，若没有客户端连接，则代码不会往下执行，会堵塞在此处。
            Socket socket = serverSocket.accept();
            System.out.println("客户端连接成功");
            // 开启线程，用于读取客户端发送的信息，并转发给每一个客户端
            new MyServerThread(socket).start();
        }
    }

}