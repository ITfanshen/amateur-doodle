package YouChat.JFrame;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileAcceptJFrame {
    public static void createWindow(String fileName, Socket socket) {
        // 创建一个新的JFrame窗口
        JFrame frame = new JFrame("文件接收");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 窗口居中

        // 创建一个面板
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(frame, panel, fileName, socket);

        // 设置窗口可见
        frame.setVisible(true);
    }

    private static void placeComponents(JFrame frame, JPanel panel, String fileName, Socket socket) {
        // 设置布局为null
        panel.setLayout(null);

        // 创建一个标签
        JLabel label = new JLabel("是否接收文件：" + fileName);
        label.setBounds(20, 20, 260, 25);
        panel.add(label);

        // 创建“是”按钮
        JButton yesButton = new JButton("是");
        yesButton.setBounds(50, 60, 80, 25);
        panel.add(yesButton);

        // 创建“否”按钮
        JButton noButton = new JButton("否");
        noButton.setBounds(150, 60, 80, 25);
        panel.add(noButton);

        // 添加“是”按钮的事件监听器
        yesButton.addActionListener(e -> {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("REQUEST_FILE" + fileName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            frame.dispose();
        });
        // 添加“否”按钮的事件监听器
        noButton.addActionListener(e -> frame.dispose());
    }
}


