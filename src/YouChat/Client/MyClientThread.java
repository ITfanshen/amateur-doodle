package YouChat.Client;

import YouChat.JFrame.ChatJFrame;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import static YouChat.JFrame.FileAcceptJFrame.createWindow;

public class MyClientThread extends Thread {

    private final Socket socket;
    private final ChatJFrame chatJFrame;
    DataInputStream dis;

    public MyClientThread(Socket socket, ChatJFrame chatJFrame) {
        this.socket = socket;
        this.chatJFrame = chatJFrame;
    }

    @Override
    public void run() {
        try {
            while (true) {
                dis = new DataInputStream(socket.getInputStream());
                String message = dis.readUTF();

                if (message.startsWith("USER_NAME") && message.endsWith("USER_NAME")) {
                    //说明信息是用户名
                    String[] names = message.replace("USER_NAME", "").split(",");
                    // 将用户列表先清空
                    chatJFrame.getUserListModel().clear();
                    for (String name : names) {
                        chatJFrame.getUserListModel().addElement(name);
                    }
                } else if (message.startsWith("PRIVATE_CHAT") && message.endsWith("PRIVATE_CHAT")) {
                    //说明是私密聊天信息，将聊天信息放在对应displayTa中
                    chatJFrame.getInputTF().setText("");
                    chatJFrame.getDisplayTa().append(message.replace("PRIVATE_CHAT", "") + "\t\n");
                } else if (message.startsWith("PUBLIC_CHAT") && message.endsWith("PUBLIC_CHAT")) {
                    //说明是公共聊天信息，将聊天信息放在对应displayTa中
                    chatJFrame.getInputTF().setText("");
                    chatJFrame.getDisplayTa().append(message.replace("PUBLIC_CHAT", "") + "\t\n");
                } else if (message.startsWith("FILE")) {
                    // 接收服务器发送的文件名
                    String fileName = message.substring(4);
                    createWindow(fileName, socket);
                } else if (message.startsWith("CONTENT")) {
                    String fileName = message.replace("CONTENT", "");
                    receiveFile(fileName);
                }
            }
        } catch (IOException e) {
            System.out.println("客户端接收线程报错：" + e.getMessage());
        }
    }

    private void receiveFile(String fileName) {
        try {

            long fileSize = dis.readLong();
            byte[] buffer = new byte[1024 * 1024 * 5];
            int b;
            long totalRead = 0;

            // 定义保存文件的目录
            File dir = new File("E:\\Java\\YouChat\\src\\YouChat\\File");
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    System.out.println("创建文件成功");
                }
            }

            File outputFile = new File(dir, fileName);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                // 循环条件和写入文件的逻辑
                while (totalRead < fileSize && (b = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
                    fos.write(buffer, 0, b);
                    totalRead += b;
                }
                fos.flush();
            }

            chatJFrame.getDisplayTa().append("文件接收完成: " + outputFile.getAbsolutePath() + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

