package com.ui;
import cn.hutool.core.io.FileUtil;
import com.domain.User;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
/**
 * 注册界面
 */
public class RegisterJFrame extends JFrame implements MouseListener {
    ArrayList<User> allUsers;
    //提升变量的作用范围，可以在本类中所有方法里面使用
    JTextField username = new JTextField(); // 用户名输入框
    JTextField password = new JTextField(); // 密码输入框
    JTextField rePassword = new JTextField(); // 确认密码输入框
    JButton submit = new JButton(); // 提交按钮
    JButton reset = new JButton(); // 重置按钮
    /**
     * 构造方法  初始化界面
     */
    public RegisterJFrame(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
        initFrame(); // 初始化界面
        initView(); // 初始化界面内容
        setVisible(true); // 显示界面
    }
    /**
     * 初始化界面内容
     */
    private void initView() {
        // 添加注册用户名的文本
        JLabel usernameText = new JLabel(new ImageIcon("image\\register\\username.png"));
        usernameText.setBounds(85, 135, 80, 20);
        // 添加注册用户名输入框
        username.setBounds(195, 134, 200, 30);
        // 添加注册密码的文本
        JLabel passwordText = new JLabel(new ImageIcon("image\\register\\password.png"));
        passwordText.setBounds(97, 193, 70, 20);
        // 添加注册密码输入框
        password.setBounds(195, 195, 200, 30);
        // 添加确认密码的文本
        JLabel rePasswordText = new JLabel(new ImageIcon("image\\register\\rePassword.png"));
        rePasswordText.setBounds(64, 255, 95, 20);
        // 添加确认密码输入框
        rePassword.setBounds(195, 255, 200, 30);
        // 添加注册按钮
        submit.setIcon(new ImageIcon("image\\register\\submit.png"));
        submit.setBounds(123, 310, 128, 47);
        submit.setBorderPainted(false); // 去掉按钮边框
        submit.setContentAreaFilled(false); // 设置按钮透明
        submit.addMouseListener(this); // 给按钮添加事件
        // 添加重置按钮
        reset.setIcon(new ImageIcon("image\\register\\reset.png"));
        reset.setBounds(256, 310, 128, 47);
        reset.setBorderPainted(false); // 去掉按钮边框
        reset.setContentAreaFilled(false); // 设置按钮透明
        reset.addMouseListener(this); // 给按钮添加事件
        // 添加注册界面背景图片
        JLabel background = new JLabel(new ImageIcon("image\\register\\background.png"));
        background.setBounds(0, 0, 470, 390);
        // 将组件添加到注册界面
        this.getContentPane().add(usernameText);
        this.getContentPane().add(passwordText);
        this.getContentPane().add(rePasswordText);
        this.getContentPane().add(username);
        this.getContentPane().add(password);
        this.getContentPane().add(rePassword);
        this.getContentPane().add(submit);
        this.getContentPane().add(reset);
        this.getContentPane().add(background);
    }
    /**
     * 初始化界面
     */
    private void initFrame() {
        setTitle("拼图游戏 V1.0注册"); // 设置界面标题
        setSize(489, 435); // 设置界面宽高
        setLocationRelativeTo(null); // 设置界面居中显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭窗口程序也退出
        setLayout(null); // 取消默认布局
        setAlwaysOnTop(true); // 设置窗口置顶
    }
    /**
     * 鼠标点击事件
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == submit) {
            // 点击了注册按钮
            // 1.用户名、密码不能为空
            if (username.getText().length() == 0 || password.getText().length() == 0 || rePassword.getText().length() == 0) {
                showDialog("用户名或密码不能为空！");
                return;
            }
            // 2.判断两次密码输入是否一致
            if (!password.getText().equals(rePassword.getText())) {
                showDialog("两次密码输入不一致！");
                return;
            }
            // 3.判断用户名和密码格式是否正确  正则表达式
            if (!username.getText().matches("[a-zA-Z0-9]{4,16}")) {
                showDialog("用户名格式不正确！(4-16位大小写字母或数字)");
                return;
            }
            if (!password.getText().matches("\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[a-z])\\S*")) {
                showDialog("密码至少包含1个字母和1个数字，长度至少6位");
                return;
            }
            // 4.判断用户名是否已经重复
            if (containsUsername(username.getText())) {
                showDialog("用户名已存在！请重新输入");
                return;
            }
            // 5.添加用户
            allUsers.add(new User(username.getText(), password.getText()));
            // 6.写入文件
            String relativelyPath = System.getProperty("user.dir");
            FileUtil.writeLines(allUsers, relativelyPath+"\\userinfo.txt", "UTF-8");
            // 7.提示注册成功
            showDialog("注册成功！");
            // 关闭注册界面 打开登录界面
            this.setVisible(false);
            new LoginJFrame();
        } else if (e.getSource() == reset) {
            // 点击了重置按钮
            // 重置输入框内容
            username.setText("");
            password.setText("");
            rePassword.setText("");
        }
    }
    /**
     * 鼠标按下事件
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == submit) {
            submit.setIcon(new ImageIcon("image\\register\\submit_press.png"));
        }
        else if (e.getSource() == reset) {
            reset.setIcon(new ImageIcon("image\\register\\reset_press.png"));
        }
    }

    /**
     * 鼠标松开事件
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == submit) {
            submit.setIcon(new ImageIcon("image\\register\\submit.png"));
        }
        else if (e.getSource() == reset) {
            reset.setIcon(new ImageIcon("image\\register\\reset.png"));
        }
    }

    /**
     * 鼠标进入事件
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    /**
     * 鼠标离开事件
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }
    // 只创建一个弹框对象
    JDialog jDialog = new JDialog();
    public void showDialog(String content) {
        if (!jDialog.isShowing()) {
            jDialog.getContentPane().removeAll(); // 清空弹框内容

            JLabel jLabel = new JLabel(content);
            jLabel.setBounds(0, 0, 300, 100); // 设置标签大小和位置
            jDialog.add(jLabel); // 添加弹框内容
            jDialog.setSize(300, 100); // 设置弹框大小
            jDialog.setAlwaysOnTop(true); // 设置弹框置顶
            jDialog.setLocationRelativeTo(null);// 设置弹框居中显示
            jDialog.setModal(true); // 设置弹框为模式对话框
            jDialog.setVisible(true); // 显示弹框
        }
    }
    /**
     * 判断用户名在集合中是否已存在
     * @param username 用户名
     * @return ture: 存在   false: 不存在
     */
    public boolean containsUsername(String username) {
        // 遍历集合
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}