package YouChat.JFrame;

import YouChat.Client.Client;
import YouChat.Util.GetJDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ChatJFrame {
    private final Client client;
    private final String userName;
    private final JFrame mainWin = new JFrame("聊天窗口");
    // 消息展示框
    private final JTextArea displayTa = new JTextArea(28, 80);
    // 在线用户名称展示框
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    // 消息发送框
    private final JTextArea inputTF = new JTextArea(8, 80);

    // 消息按钮
    private final JButton sendBn = new JButton("发送");
    private final JButton quitPrivateChatBn = new JButton("退出私聊");
    private final JButton privateChatBn = new JButton("发送");
    private final JButton chooseFileButton = new JButton("上传文件");

    public ChatJFrame(Client client, String userName) {
        this.client = client;
        this.userName = userName;
    }

    private static void sendFile(File file, Client client) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            client.getDataOutputStream().writeUTF("FILE" + file.getName());
            client.getDataOutputStream().writeLong(file.length());
            byte[] buffer = new byte[1024 * 1024 * 5];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                client.getDataOutputStream().write(buffer, 0, bytesRead);
            }
            client.getDataOutputStream().flush();
            GetJDialog.showJDialog("文件上传成功!");
        } catch (IOException ex) {
            ex.printStackTrace();
            GetJDialog.showJDialog("文件上传失败.");
            System.out.println(ex.getMessage());
        }
    }

    public void buildChatWindow() {
        mainWin.setTitle("欢迎 " + userName + "使用YouChat");

        // 将消息框和按钮添加到窗口的底端
        JPanel bottomPanel = new JPanel();
        mainWin.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(inputTF);
        bottomPanel.add(sendBn);

        //将文件上传按钮添加到窗口的底端
        JPanel middlePanel = new JPanel();
        mainWin.add(middlePanel, BorderLayout.CENTER);
        middlePanel.add(chooseFileButton);
        middlePanel.add(quitPrivateChatBn);
        quitPrivateChatBn.setVisible(false);

        //上传文件按钮添加事件
        chooseFileButton.addActionListener(e -> {
            //选择文件
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("上传文件");
            int userSelection = fileChooser.showOpenDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    sendFile(selectedFile, client);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        //chooseFileButton.addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        //选择文件
        //        JFileChooser fileChooser = new JFileChooser();
        //        fileChooser.setDialogTitle("上传文件");
        //        int userSelection = fileChooser.showOpenDialog(null);
        //        if (userSelection == JFileChooser.APPROVE_OPTION) {
        //            File selectedFile = fileChooser.getSelectedFile();
        //            try (FileInputStream fis = new FileInputStream(selectedFile);
        //                 DataOutputStream dos = new DataOutputStream(client.getSocket().getOutputStream())) {
        //                byte[] buffer = new byte[1024];
        //                int bytesRead;
        //                while ((bytesRead = fis.read(buffer)) != -1) {
        //                    dos.write(buffer, 0, bytesRead);
        //                }
        //                dos.flush();
        //                GetJDialog.showJDialog("文件上传成功!");
        //            } catch (IOException ex) {
        //                ex.printStackTrace();
        //                GetJDialog.showJDialog("文件上传失败.");
        //                System.out.println(ex.getMessage());
        //            }
        //        }
        //    }
        //});

        // 给发送公共消息按钮绑定点击事件监听器
        //Lambda表达式优化
        sendBn.addActionListener(actionEvent -> {
            if (inputTF.getText().isEmpty()) GetJDialog.showJDialog("输入不能为空");
            else client.sendMessage2Server("PUBLIC_CHAT" + inputTF.getText() + "PUBLIC_CHAT");
        });
        //sendBn.addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent actionEvent) {
        //        if (inputTF.getText().isEmpty()) GetJDialog.showJDialog("输入不能为空");
        //        else {
        //            client.sendMessage2Server("PUBLIC_CHAT" + inputTF.getText() + "PUBLIC_CHAT");
        //        }
        //    }
        //});

        // 将展示消息区centerPanel添加到窗口的中间
        JPanel centerPanel = new JPanel();
        mainWin.add(centerPanel, BorderLayout.NORTH);

        // 让展示消息区可以滚动
        centerPanel.add(new JScrollPane(displayTa));
        displayTa.setEditable(false);

        // 用户列表和是否私聊放到窗口的最右边
        Box rightBox = new Box(BoxLayout.Y_AXIS);
        userList.setFixedCellWidth(120);
        userList.setVisibleRowCount(24);
        rightBox.add(new JLabel("用户列表："));
        rightBox.add(new JScrollPane(userList));
        centerPanel.add(rightBox);

        //双击用户列表中的用户进行私聊
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 判断是否双击事件
                if (e.getClickCount() == 2) {
                    // 获取双击的元素索引
                    int index = userList.locationToIndex(e.getPoint());
                    // 获取双击的元素
                    String talker = userList.getModel().getElementAt(index);
                    // 执行双击事件的操作
                    quitPrivateChatBn.setVisible(true);
                    bottomPanel.remove(sendBn);
                    bottomPanel.add(privateChatBn);
                    GetJDialog.showJDialog("正在和" + talker + "聊天");
                    //私聊键发送私聊消息
                    //Lambda表达式优化
                    privateChatBn.addActionListener(actionEvent -> {
                        if (inputTF.getText().isEmpty()) GetJDialog.showJDialog("输入不能为空");
                        else
                            client.sendMessage2Server("PRIVATE_CHAT" + inputTF.getText() + "%%" + talker + "PRIVATE_CHAT");
                    });
                    //privateChatBn.addActionListener(new ActionListener() {
                    //    @Override
                    //    public void actionPerformed(ActionEvent actionEvent) {
                    //        if (inputTF.getText().isEmpty()) GetJDialog.showJDialog("输入不能为空");
                    //        else {
                    //            client.sendMessage2Server("PRIVATE_CHAT" + inputTF.getText() + "%%" + talker + "PRIVATE_CHAT");
                    //        }
                    //    }
                    //});
                }
            }
        });

        //按退出键退出私聊
        //Lambda表达式优化
        quitPrivateChatBn.addActionListener(actionEvent -> {
            quitPrivateChatBn.setVisible(false);
            bottomPanel.remove(privateChatBn);
            bottomPanel.add(sendBn);
            GetJDialog.showJDialog("已退出私聊");
        });
        //quitPrivateChatBn.addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent actionEvent) {
        //        quitPrivateChatBn.setVisible(false);
        //        bottomPanel.remove(privateChatBn);
        //        bottomPanel.add(sendBn);
        //        GetJDialog.showJDialog("已退出私聊");
        //    }
        //});

        // 关闭窗口退出当前程序
        mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // swing加上这句就可以拥有关闭窗口的功能
        mainWin.pack();
        mainWin.setVisible(true);
    }

    public DefaultListModel<String> getUserListModel() {
        return userListModel;
    }

    public JTextArea getDisplayTa() {
        return displayTa;
    }

    public JTextArea getInputTF() {
        return inputTF;
    }

}
