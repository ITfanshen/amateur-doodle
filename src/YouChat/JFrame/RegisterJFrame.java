package YouChat.JFrame;

import YouChat.Message.User;
import YouChat.Util.GetCode;
import YouChat.Util.GetJDialog;
import cn.hutool.core.io.FileUtil;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import static YouChat.Util.AESUtil.encrypt;

public class RegisterJFrame extends JFrame implements MouseListener {

    ArrayList<User> Users;
    JTextField username = new JTextField();
    JPasswordField password = new JPasswordField();
    JPasswordField confirmPassword = new JPasswordField();
    JTextField verification = new JTextField();
    JTextField IDNumber = new JTextField();
    JLabel rightVerification = new JLabel();
    JButton submit = new JButton("注册");
    JButton reset = new JButton("重置");
    JButton cd = new JButton("返回");

    public RegisterJFrame(ArrayList<User> allUsers) {
        this.Users = allUsers;
        //初始化注册界面
        initFrame();
        initView();
        //使界面可视化
        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == submit) {
            //点击了注册按钮
            //1.用户名，密码不能为空
            //String.valueOf()将password.getPassword()转成String类型
            if (username.getText().isEmpty() || String.valueOf(password.getPassword()).isEmpty() || String.valueOf(confirmPassword.getPassword()).isEmpty()) {
                GetJDialog.showJDialog("用户名和密码不能为空");
                return;
            }
            //2.判断两次密码输入是否一致
            if (!String.valueOf(password.getPassword()).equals(String.valueOf(confirmPassword.getPassword()))) {
                GetJDialog.showJDialog("两次密码输入不一致");
                return;
            }
            //3.判断用户名和密码的格式是否正确
            if (!username.getText().matches("[a-zA-Z0-9]{3,18}")) {
                GetJDialog.showJDialog("用户名不符合规则，长度为3到18");
                return;
            }
            if (!String.valueOf(confirmPassword.getPassword()).matches("\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[a-z])\\S*")) {
                GetJDialog.showJDialog("密码不符合规则，至少包含1个小\n写字母，1个数字，长度至少6位");
                return;
            }
            //4.判断用户名是否已经重复
            if (containsUsername(username.getText())) {
                GetJDialog.showJDialog("用户名已经存在");
                return;
            }
            //5.判断验证码是否正确
            if (!verification.getText().equalsIgnoreCase(rightVerification.getText())) {
                GetJDialog.showJDialog("验证码错误");
                return;
            }
            //6.判断身份证是否存在
            if (containsIdNumber(IDNumber.getText())) {
                GetJDialog.showJDialog("此身份证已注册过账号");
                return;
            }
            //7.判断身份证号格式是否符合
            if (!IDNumber.getText().matches("(\\d{8}(0\\d|10|11|12)([0-2]\\d|30|31)\\d{3})|(\\d{6}(18|19|20)\\d{2}(0[1-9]|10|11|12)([0-2]\\d|30|31)\\d{3}(\\d|X|x))")) {
                GetJDialog.showJDialog("请输入正确的身份证号");
                return;
            }
            //8.添加用户
            try {
                Users.add(new User(username.getText(), String.valueOf(password.getPassword()), IDNumber.getText()));
                System.out.println(Users);
                for (User user : Users) {
                    user.setUsername(encrypt(user.getUsername()));
                    user.setPassword(encrypt(user.getPassword()));
                    user.setIdNumber(encrypt(user.getIdNumber()));
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            //9.写入文件
            //HuTool包FileUtil
            FileUtil.writeLines(Users, "E:\\Java\\YouChat\\src\\YouChat\\UserInfo\\userInformation", "UTF-8");
            //10.提示注册成功
            GetJDialog.showJDialog("注册成功");
            //关闭注册界面，打开登录界面
            this.setVisible(false);
            //转到登录界面
            try {
                new LoginJFrame();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource() == reset) {
            //点击了重置按钮
            //清空5个输入框
            username.setText("");
            password.setText("");
            confirmPassword.setText("");
            IDNumber.setText("");
            verification.setText("");
        } else if (e.getSource() == rightVerification) {
            System.out.println("更换验证码");
            //获取一个新的验证码
            String code = GetCode.getCode();
            rightVerification.setText(code);
        } else if (e.getSource() == cd) {
            //关闭注册界面，打开登录界面
            this.setVisible(false);
            //转到登录界面
            try {
                new LoginJFrame();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    //判断用户名是否存在
    public boolean containsUsername(String username) {
        for (User user : Users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    //判断身份证是否存在
    public boolean containsIdNumber(String idNumber) {
        for (User user : Users) {
            if (user.getIdNumber().replace("}", "").equals(idNumber)) {
                return true;
            }
        }
        return false;
    }

    //初始化界面
    private void initView() {
        //添加注册用户名的文本
        JLabel usernameText = new JLabel("用户名:");
        usernameText.setBounds(100, 20, 80, 20);
        //添加注册用户名的输入框
        username.setBounds(175, 15, 200, 30);

        //添加注册密码的文本
        JLabel passwordText = new JLabel("密码:");
        passwordText.setBounds(100, 80, 70, 20);
        //添加密码输入框
        password.setBounds(175, 75, 200, 30);

        //添加再次输入密码的文本
        JLabel confirmPasswordText = new JLabel("确认密码:");
        confirmPasswordText.setBounds(100, 140, 95, 20);
        //添加再次输入密码的输入框
        confirmPassword.setBounds(175, 135, 200, 30);

        //身份证号文本
        JLabel IdNumber = new JLabel("身份证号：");
        IdNumber.setBounds(100, 200, 95, 20);
        //身份证号输入框
        IDNumber.setBounds(175, 195, 200, 30);

        //验证码文本
        JLabel codeText = new JLabel("验证码:");
        codeText.setBounds(100, 260, 80, 20);
        //验证码输入框
        verification.setBounds(175, 255, 100, 30);
        verification.addMouseListener(this);
        //正确的验证码
        String codeStr = GetCode.getCode();
        //设置内容
        rightVerification.setText(codeStr);
        //绑定鼠标事件
        rightVerification.addMouseListener(this);
        //位置和宽高
        rightVerification.setBounds(280, 255, 50, 30);
        //添加到界面
        this.getContentPane().add(rightVerification);

        //注册的按钮
        submit.setBounds(103, 310, 128, 47);
        submit.addMouseListener(this);

        //重置的按钮
        reset.setBounds(256, 310, 128, 47);
        reset.addMouseListener(this);

        //返回上一级的按钮
        cd.setBounds(10, 10, 65, 22);
        cd.addMouseListener(this);

        this.getContentPane().add(username);
        this.getContentPane().add(reset);
        this.getContentPane().add(password);
        this.getContentPane().add(submit);
        this.getContentPane().add(confirmPassword);
        this.getContentPane().add(passwordText);
        this.getContentPane().add(confirmPasswordText);
        this.getContentPane().add(usernameText);
        this.getContentPane().add(verification);
        this.getContentPane().add(rightVerification);
        this.getContentPane().add(codeText);
        this.getContentPane().add(IdNumber);
        this.getContentPane().add(IDNumber);
        this.getContentPane().add(cd);

    }

    private void initFrame() {
        //界面设置
        //设置宽高
        setSize(488, 430);
        //设置标题
        setTitle("YouChat注册");
        //取消内部默认布局
        setLayout(null);
        //设置关闭模式
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //设置居中
        setLocationRelativeTo(null);
        //设置置顶
        setAlwaysOnTop(true);
    }
}
