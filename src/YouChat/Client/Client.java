package YouChat.Client;

import YouChat.JFrame.ChatJFrame;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {

    private final Socket socket;
    private final ChatJFrame chatJFrame;
    private final String userName;
    private DataOutputStream dos;

    public Client(String ip, int port, String userName) throws IOException {
        this.socket = new Socket(ip, port);
        this.chatJFrame = new ChatJFrame(this, userName);
        this.userName = userName;
    }

    //创建线程池
    public static ThreadPoolExecutor getPool() {
        // 1. corePoolSize: 核心线程数
        // 2. maximumPoolSize: 最大线程数
        // 3. keepAliveTime: 空闲线程存活时间
        // 4. 3的单位
        // 5. workQueue: 任务队列
        // 6. threadFactory: 线程工厂，表示使用默认的线程工厂来创建新线程。
        // 7. handler: 拒绝策略，使用了 ThreadPoolExecutor.AbortPolicy()，表示当线程池无法接受新的任务时，将抛出一个 RejectedExecutionException 异常。
        return new ThreadPoolExecutor(
                3,
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public static void ClientLogin(String userName) throws IOException {
        Client client = new Client("127.0.0.1", 9999, userName);
        client.execute();
    }

    public void sendMessage2Server(String message) {
        try {
            //发送聊天消息到服务端
            dos = new DataOutputStream(this.socket.getOutputStream());
            dos.writeUTF(message);
            dos.flush();
        } catch (Exception e) {
            System.out.println("发送消息报错：" + e.getMessage());
        }
    }

    public void execute() {
        sendMessage2Server("USER_NAME" + userName + "USER_NAME");
        chatJFrame.buildChatWindow();
        //线程池优化
        getPool().submit(new MyClientThread(socket, chatJFrame));
    }

    public DataOutputStream getDataOutputStream() {
        return this.dos;
    }
}
