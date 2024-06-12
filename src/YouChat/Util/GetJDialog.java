package YouChat.Util;

import javax.swing.*;

public class GetJDialog {
    //因为展示弹框的代码，会被运行多次
    //所以，我们把展示弹框的代码，抽取到一个类中。以后用到的时候，就不需要写了
    //直接调用就可以了。
    public static void showJDialog(String content) {
        //创建一个弹框对象
        JDialog jDialog = new JDialog();
        //给弹框设置大小
        jDialog.setSize(400, 75);
        //让弹框置顶
        jDialog.setAlwaysOnTop(true);
        //让弹框居中
        jDialog.setLocationRelativeTo(null);

        //创建JLabel对象管理文字并添加到弹框当中
        JLabel warning = new JLabel(content);
        warning.setBounds(0, 0, 200, 150);
        jDialog.getContentPane().add(warning);

        //设置计时器1秒后自动关闭弹窗
        //优化为Lambda表达式
        Timer timer = new Timer(1000, e -> {
            // 关闭窗口
            jDialog.dispose();
        });
        //Timer timer2 = new Timer(500, new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        // 关闭窗口
        //        jDialog.dispose();
        //    }
        //});
        // 设置定时器只执行一次
        timer.setRepeats(false);
        // 启动定时器
        timer.start();
        //弹框展示
        jDialog.setVisible(true);

    }
}
