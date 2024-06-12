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
