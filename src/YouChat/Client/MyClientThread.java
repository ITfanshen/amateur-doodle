package YouChat.Client;

import YouChat.JFrame.ChatJFrame;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import static YouChat.JFrame.FileAcceptJFrame.createWindow;

public class MyClientThread extends Thread {

    private final Socket socket;
    private final ChatJFrame chatJFrame;

    public MyClientThread(Socket socket, ChatJFrame chatJFrame) {
        this.socket = socket;
        this.chatJFrame = chatJFrame;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String message = dataInputStream.readUTF();

                if (message.startsWith("USER_NAME") && message.endsWith("USER_NAME")) {
                    //说明信息是用户名
                    String[] names = message.replace("USER_NAME", "").split(",");
                    // 将用户列表先清空
                    chatJFrame.getUserListModel().clear();
                    for (String name : names) {
                        chatJFrame.getUserListModel().addElement(name);
                        System.out.println(name + "加入聊天");
                    }
                } else if (message.startsWith("PRIVATE_CHAT") && message.endsWith("PRIVATE_CHAT")) {
                    //说明是私密聊天信息，将聊天信息放在对应displayTa中
                    chatJFrame.getInputTF().setText("");
                    chatJFrame.getDisplayTa().append(message.replace("PRIVATE_CHAT", "") + "\t\n");
                } else if (message.startsWith("PUBLIC_CHAT") && message.endsWith("PUBLIC_CHAT")) {
                    //说明是聊天信息，将聊天信息放在对应displayTa中
                    chatJFrame.getInputTF().setText("");
                    chatJFrame.getDisplayTa().append(message.replace("PUBLIC_CHAT", "") + "\t\n");
                } else if (message.startsWith("FILE")) {
                    // 接收服务器发送的文件名
                    String fileName = message.substring(4);
                    createWindow(fileName, socket);
                }
            }
        } catch (IOException e) {
            System.out.println("客户端接收线程报错：" + e.getMessage());
        }
    }
}