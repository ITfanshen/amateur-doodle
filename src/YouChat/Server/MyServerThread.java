package YouChat.Server;

import YouChat.Util.GetJDialog;
import YouChat.Util.TimeUtil;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyServerThread extends Thread {
    private final Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public MyServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //循环接收用户端发来的信息
                dis = new DataInputStream(socket.getInputStream());
                String data = dis.readUTF();
                //发送过来的是用户名
                if (data.startsWith("USER_NAME") && data.endsWith("USER_NAME")) {
                    //将Socket以及用户名字都存放在Map集合中（socket为键，name为值）
                    Server.socketNameMap.put(socket, data.replace("USER_NAME", ""));
                    //将消息传给客户端
                    broadcastMsg("PUBLIC_CHAT" + "                                                                                               "
                            + data.replace("USER_NAME", "") + "加入了聊天_" + TimeUtil.getCurrentTime() + "PUBLIC_CHAT");
                    broadcastMsg("USER_NAME" + getUserNamesStr() + "USER_NAME");
                //发送过来的是私密聊天信息
                } else if (data.startsWith("PRIVATE_CHAT") && data.endsWith("PRIVATE_CHAT")) {
                    //处理信息，分开名字和消息
                    String[] message = data.replace("PRIVATE_CHAT", "").split("%%");
                    //建立Set集合，储存键值（socket）
                    Set<Map.Entry<Socket, String>> entries = Server.socketNameMap.entrySet();
                    //通过值（名字）获取键（私聊对象的socket）
                    Socket talkerSocket = getPrivateSocket(entries, message);
                    //判断是否找到
                    if (!message[0].isEmpty() && talkerSocket != null) {
                        //将处理后的信息发给私聊对象
                        privateCastMsg("PRIVATE_CHAT" + "[ " + Server.socketNameMap.get(socket) + "_" + TimeUtil.getCurrentTime() + " ]对你说：" + message[0] + "PRIVATE_CHAT", talkerSocket);
                        //私聊信息自己可视
                        privateCastMsg("PRIVATE_CHAT" + "你对[ " + message[1] + "_" + TimeUtil.getCurrentTime() + " ]说：" + message[0] + "PRIVATE_CHAT", socket);
                    } else GetJDialog.showJDialog("输入不能为空");
                //发送过来的是公共聊天信息
                } else if (data.startsWith("PUBLIC_CHAT") && data.endsWith("PUBLIC_CHAT")) {
                    //將聊天信息广播出去
                    broadcastMsg("PUBLIC_CHAT" + "[ " + Server.socketNameMap.get(socket) + "_" + TimeUtil.getCurrentTime() + " ]：" + data);
                //发送过来的是文件
                } else if (data.startsWith("FILE")) {
                    String fileName = data.substring(4);
                    receiveFile(fileName);
                    broadcastFile("FILE" + fileName);
                //请求接收
                } else if (data.startsWith("REQUEST_FILE")) {
                    String requestedFileName = data.substring(12);
                    sendFileToClient("received_" + requestedFileName);
                }
            }
        } catch (IOException e) {
            // 客户端退出时清除对应socket，并同步用户列表
            broadcastMsg("PUBLIC_CHAT" + "                                                                                               "
                    + Server.socketNameMap.remove(socket) + "退出聊天_" + TimeUtil.getCurrentTime() + "PUBLIC_CHAT");
            System.out.println(Server.socketNameMap.remove(socket) + "退出聊天");
            broadcastMsg("USER_NAME" + getUserNamesStr() + "USER_NAME");
            System.out.println(this.getName() + "程序退出:" + e.getMessage());
        }
    }

    //收到请求后发送文件给客户端
    private void sendFileToClient(String fileName) throws IOException {
        dos.writeUTF("CONTENT" + fileName);
        File fileToSend = new File(fileName);

        byte[] buffer = new byte[1024];
        int b;
        try (FileInputStream fis = new FileInputStream(fileToSend)) {
            while ((b = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, b);
            }
            dos.flush();
        }

    }

    //接收用户上传的文件
    private void receiveFile(String fileName) throws IOException {
        long fileSize = dis.readLong();
        try (FileOutputStream fos = new FileOutputStream("received_" + fileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;
            while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, Math.min(buffer.length, (int) (fileSize - totalRead)))) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }
        }
        GetJDialog.showJDialog("服务器接收文件：" + fileName);
        broadcastMsg("PUBLIC_CHAT" + "                                                                                               " +
                Server.socketNameMap.get(socket) + "上传了文件：" + fileName + "PUBLIC_CHAT");
    }

    //获得私聊socket对象
    private Socket getPrivateSocket(Set<Map.Entry<Socket, String>> entries, String[] message) {
        for (Map.Entry<Socket, String> entry : entries) {
            if (entry.getValue().equals(message[1])) {
                return entry.getKey();
            }
        }
        return null;
    }

    //获取所有用户列表
    private String getUserNamesStr() {
        Collection<String> names = Server.socketNameMap.values();
        StringBuilder namesStr = new StringBuilder();
        for (String userName : names) {
            namesStr.append(userName).append(",");
        }
        System.out.println("用户列表:" + namesStr);
        return namesStr.toString();
    }

    //私聊信息发送
    private void privateCastMsg(String msg, Socket talkerSocket) {
        try {
            dos = new DataOutputStream(talkerSocket.getOutputStream());
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //公共广播信息
    private void broadcastMsg(String msg) {
        try {
            Set<Socket> sockets = Server.socketNameMap.keySet();
            for (Socket soc : sockets) {
                dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF(msg);
                dos.flush();
            }
        } catch (IOException e) {
            System.out.println("广播消息失败：" + e.getMessage());
        }
    }

    //提出文件接收请求
    private void broadcastFile(String msg) {
        try {
            Set<Socket> sockets = Server.socketNameMap.keySet();
            for (Socket soc : sockets) {
                if (!soc.equals(socket)) {
                    dos = new DataOutputStream(soc.getOutputStream());
                    dos.writeUTF(msg);
                    dos.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("广播消息失败：" + e.getMessage());
        }
    }
}

