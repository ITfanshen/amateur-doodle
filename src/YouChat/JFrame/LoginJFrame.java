package YouChat.JFrame;

import YouChat.Message.User;
import YouChat.Util.GetCode;
import cn.hutool.core.io.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static YouChat.Client.Client.ClientLogin;
import static YouChat.Util.AESUtil.decrypt;
import static YouChat.Util.GetJDialog.showJDialog;

public class LoginJFrame extends JFrame implements MouseListener {
    //储存用户信息
    ArrayList<User> Users = new ArrayList<>();
    JButton login = new JButton("登录");
    JButton register = new JButton("没有账号？立即注册");
    JButton title = new JButton("欢迎使用YouChat");
    JButton changePassword = new JButton("忘记密码");

    JTextField username = new JTextField();
    JPasswordField password = new JPasswordField();
    //    PasswordFieldWithEye password=new PasswordFieldWithEye(this.getContentPane());
    JTextField verification = new JTextField();

    //提供的验证码
    JLabel rightVerification = new JLabel();

    public LoginJFrame() throws Exception {
        //读取本地文件中的用户信息
        readUserInfo();
        //初始化界面
        initJFrame();
        //在界面中添加内容
        initView();
        //让界面显示出来
        this.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == login) {
            System.out.println("点击了登录按钮");
            //获取两个文本输入框中的内容
            String usernameInput = username.getText();
            String passwordInput = String.valueOf(password.getPassword());
            System.out.println(passwordInput);
            //获取用户输入的验证码
            String codeInput = verification.getText();
            //创建一个User对象
            User user = new User(usernameInput, passwordInput);
            System.out.println("用户输入的用户名为" + usernameInput);
            System.out.println("用户输入的密码为" + passwordInput);

            if (codeInput.isEmpty()) {
                showJDialog("验证码不能为空");
                //校验用户名和密码是否为空
            } else if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                System.out.println("用户名或者密码为空");
                //调用showJDialog方法并展示弹框
                showJDialog("用户名或者密码为空");
            } else if (!codeInput.equalsIgnoreCase(rightVerification.getText())) {
                showJDialog("验证码输入错误");
            } else {
                try {
                    if (contains(user)) {
                        showJDialog("登陆成功");
                        //关闭当前登录界面
                        this.setVisible(false);
                        try {
                            //打开聊天的主界面
                            //需要把当前登录的用户名传递给聊天界面
                            ClientLogin(usernameInput);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        System.out.println("用户名或密码错误");
                        showJDialog("用户名或密码错误");
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSource() == register) {
            System.out.println("点击了注册按钮");
            //关闭当前的登录界面
            this.setVisible(false);
            //打开注册界面
            new RegisterJFrame(Users);
        } else if (e.getSource() == rightVerification) {
            System.out.println("更换验证码");
            //获取一个新的验证码
            String code = GetCode.getCode();
            rightVerification.setText(code);
        } else if (e.getSource() == changePassword) {
            //关闭当前的登录界面
            this.setVisible(false);
            //打开修改密码界面
            new ChangePasswordJFrame(Users);
        }
    }

    //鼠标按下
    @Override
    public void mousePressed(MouseEvent e) {
    }

    //鼠标松开
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    //鼠标划入
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    //鼠标划出
    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void initView() {
        //1.添加用户名文字
        JLabel usernameText = new JLabel("用户名:");
        usernameText.setBounds(130, 140, 47, 17);
        this.getContentPane().add(usernameText);

        //2.添加用户名输入框
        username.setBounds(205, 134, 200, 30);
        this.getContentPane().add(username);

        //3.添加密码文字
        JLabel passwordText = new JLabel("密码:");
        passwordText.setBounds(130, 200, 32, 16);
        this.getContentPane().add(passwordText);

        //4.密码输入框
        password.setBounds(205, 195, 200, 30);
        this.getContentPane().add(password);

        //5.添加标题
        //设置标题字体大小
        Font font = new Font("Serif", Font.BOLD, 25);
        title.setFont(font);
        title.setBounds(160, 60, 250, 30);
        this.getContentPane().add(title);
        //去除按钮的边框
        title.setBorderPainted(false);
        //去除按钮的背景
        title.setContentAreaFilled(false);

        //6.验证码提示
        JLabel codeText = new JLabel("验证码:");
        codeText.setBounds(133, 261, 50, 30);
        this.getContentPane().add(codeText);

        //7.验证码的输入框
        verification.setName("验证码");
        verification.setBounds(205, 256, 100, 30);
        verification.addMouseListener(this);
        this.getContentPane().add(verification);

        //正确的验证码
        String codeStr = GetCode.getCode();
        //设置内容
        rightVerification.setText(codeStr);
        //绑定鼠标事件
        rightVerification.addMouseListener(this);
        //位置和宽高
        rightVerification.setBounds(310, 256, 50, 30);
        //添加到界面
        this.getContentPane().add(rightVerification);

        //8.添加登录按钮
        login.setBounds(132, 310, 273, 47);
        //给登录按钮绑定鼠标事件
        login.addMouseListener(this);
        this.getContentPane().add(login);

        //9.添加注册按钮
        register.setBounds(250, 360, 200, 22);
        //去除按钮的边框
        register.setBorderPainted(false);
        //去除按钮的背景
        register.setContentAreaFilled(false);
        //给注册按钮绑定鼠标事件
        register.addMouseListener(this);
        this.getContentPane().add(register);

        //10.添加忘记密码按钮
        changePassword.setBounds(56, 360, 200, 22);
        //去除按钮的边框
        changePassword.setBorderPainted(false);
        //去除按钮的背景
        changePassword.setContentAreaFilled(false);
        //给注册按钮绑定鼠标事件
        changePassword.addMouseListener(this);
        this.getContentPane().add(changePassword);
    }

    public void initJFrame() {
        //设置宽高
        this.setSize(590, 490);
        //设置标题
        this.setTitle("YouChat");
        //设置关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //居中
        this.setLocationRelativeTo(null);
        //置顶
        this.setAlwaysOnTop(true);
        //取消内部默认布局
        this.setLayout(null);
    }

    //读取本地文件中的用户信息
    private void readUserInfo() throws Exception {
        //1.读取数据
        List<String> userInfoStrList = FileUtil.readUtf8Lines("E:\\Java\\YouChat\\src\\YouChat\\UserInfo\\userInformation");
        //2.遍历集合获取用户信息并创建User对象
        for (String str : userInfoStrList) {
            //处理用户信息
            String[] userInfoArr = str.split(", ");
            String[] arr1 = userInfoArr[0].split(" = ");
            String[] arr2 = userInfoArr[1].split(" = ");
            String[] arr3 = userInfoArr[2].split(" = ");
            String[] tempUserName = arr1[1].split("①②③④⑤");
            String[] tempPassword = arr2[1].split("①②③④⑤");
            String[] tempIdNumber = arr3[1].split("①②③④⑤");
            Users.add(new User(decrypt(tempUserName[0], tempUserName[1]),
                    decrypt(tempPassword[0], tempPassword[1]),
                    decrypt(tempIdNumber[0], tempIdNumber[1].replace("}", ""))));
        }
        for (User user : Users) {
            System.out.println(user);
        }
    }

    //判断用户在集合中是否存在
    public boolean contains(User userInformation) {
        for (User rightUser : Users) {
            if (userInformation.getUsername().equals(rightUser.getUsername())
                    && userInformation.getPassword().equals(rightUser.getPassword())) {
                //有相同的代表存在，返回true，后面的不需要再比了
                return true;
            }
        }
        //循环结束之后还没有找到就表示不存在
        return false;
    }
}
