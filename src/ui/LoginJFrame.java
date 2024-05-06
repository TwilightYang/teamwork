package ui;

import cn.hutool.core.io.FileUtil;
import domain.User;
import utils.CodeUtil;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录界面
 */
public class LoginJFrame extends JFrame implements MouseListener {

    ArrayList<User> allUsers = new ArrayList<>();

    JTextField username = new JTextField(); // 用户名输入框
    JPasswordField password = new JPasswordField(); // 密码输入框
    JTextField code = new JTextField(); // 验证码输入框
    JLabel rightCode = new JLabel(); // 正确的验证码

    JButton login = new JButton(); // 登录按钮
    JButton register = new JButton(); // 注册按钮

    public LoginJFrame() {
        // 打开界面是读取文件 在构造方法中写
        // 读取本地文件的用户信息
        readUserInfo();
        // 初始化界面
        initJFrame();
        // 初始化界面内容
        initView();
        // 显示窗口
        this.setVisible(true);
    }

    /**
     * 读取本地文件的用户信息
     */
    private void readUserInfo() {
        String relativelyPath = System.getProperty("user.dir");
        //读取数据
        List<String> userInfoStrList = FileUtil.readUtf8Lines(relativelyPath+"\\userinfo.txt");
        // 遍历集合获取用户信息  转换成User对象
        for (String str : userInfoStrList) {
            // 格式：username=zhangsan&password=123
            String[] userInfoArr = str.split("&");  // 按 & 分割
            // 0 --> username=zhangsan   1 --> password=123
            String[] arr1 = userInfoArr[0].split("=");
            String[] arr2 = userInfoArr[1].split("=");
            System.out.println(arr1[1]);
            System.out.println(arr2[1]);
            // 创建User对象
            User u = new User(arr1[1],arr2[1]);
            // 添加到集合中
            allUsers.add(u);
        }
        // 读取本地文件的用户信息
        System.out.println(allUsers);
    }

    /**
     * 初始化界面
     */
    public void initJFrame() {
        // 设置窗口大小 宽高
        this.setSize(488,435);
        // 设置窗口标题
        this.setTitle("拼图游戏 V1.0登录");
        // 设置窗口关闭时的默认操作
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        this.setLocationRelativeTo(null);
        // 设置窗口置顶
        this.setAlwaysOnTop(true);
        // 设置窗口布局为null，取消内部默认布局，以便自定义组件位置
        this.setLayout(null);
    }

    /**
     * 初始化界面内容
     * 包括：用户名、密码、验证码输入框、登录按钮、注册按钮
     */
    public void initView() {
        // 添加用户名文字
        JLabel usernameText = new JLabel(new ImageIcon("images\\login\\username.png"));
        usernameText.setBounds(116,135,47,17); // 设置文字位置和大小
        this.getContentPane().add(usernameText); // 添加到窗口

        // 创建用户名输入框
        username.setBounds(195, 134, 200, 30);
        this.getContentPane().add(username); // 添加到窗口

        // 添加密码文字
        JLabel passwordText = new JLabel(new ImageIcon("images\\login\\password.png"));
        passwordText.setBounds(130,195,32,16);
        this.getContentPane().add(passwordText); // 添加到窗口

        // 创建密码输入框
        password.setBounds(195, 195, 200, 30);
        this.getContentPane().add(password); // 添加到窗口

        // 验证码提示
        JLabel codeText = new JLabel(new ImageIcon("images\\login\\code.png"));
        codeText.setBounds(133,256,50,30);
        this.getContentPane().add(codeText);

        // 创建验证码输入框
        code.setBounds(195, 256, 100, 30);
        this.getContentPane().add(code);

        String codeStr = CodeUtil.getCode();
        rightCode.setText(codeStr); // 设置正确的验证码
        rightCode.addMouseListener(this); // 添加鼠标点击事件
        rightCode.setBounds(300, 256, 50, 30); // 设置验证码位置和宽高
        this.getContentPane().add(rightCode); // 添加到窗口

        // 添加登录按钮
        login.setBounds(123, 310, 128, 47);
        login.setIcon(new ImageIcon("images\\login\\login.png"));
        login.setBorderPainted(false); // 去除按钮边框
        login.setContentAreaFilled(false); // 设置按钮透明 去除背景
        login.addMouseListener(this);// 绑定鼠标事件
        this.getContentPane().add(login); // 添加到窗口

        // 添加注册按钮
        register.setBounds(260, 310, 128, 47);
        register.setIcon(new ImageIcon("images\\login\\register.png"));
        register.setBorderPainted(false); // 去除按钮边框
        register.setContentAreaFilled(false); // 设置按钮透明 去除背景
        register.addMouseListener(this);// 绑定鼠标事件
        this.getContentPane().add(register); // 添加到窗口

        // 添加背景图片
        JLabel background = new JLabel(new ImageIcon("images\\login\\background.png"));
        background.setBounds(0, 0, 470, 390);
        this.getContentPane().add(background);
    }

    /**
     * 点击鼠标事件
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == login) {
            System.out.println("登录按钮被点击");
            // 获取用户输入的用户名和密码
            String usernameInput = username.getText();
            String passwordInput = password.getText();
            // 获取用户输入的验证码
            String codeInput = code.getText();

            // 创建一个User对象
            User userInfo = new User(usernameInput,passwordInput);
            System.out.println("用户输入的用户名：" + usernameInput);
            System.out.println("用户输入的密码：" + passwordInput);

            if (codeInput.length() == 0) {
                showJDialog("验证码不能为空！");
            } else if (usernameInput.length() == 0 || passwordInput.length() == 0) {
                //校验用户名和密码是否为空
                System.out.println("用户名或者密码为空");
                //调用showJDialog方法并展示弹框
                showJDialog("用户名或者密码为空");
            } else if ( !codeInput.equalsIgnoreCase(rightCode.getText())) {
                showJDialog("验证码错误！");
            } else if ( isContains( userInfo )) {
                System.out.println("用户名和密码正确可以开始玩游戏了");
                // 关闭当前登录界面
                this.setVisible(false);
                // 创建一个游戏窗口
                new GameJFrame();
            } else {
                System.out.println("用户名或密码错误");
                showJDialog("用户名或者密码错误！");
            }
        } else if (e.getSource() == register) {
            System.out.println("点击了注册按钮");
            this.setVisible(false); // 关闭当前登录界面
            new RegisterJFrame(allUsers); // 创建一个注册窗口 传入用户集合
        } else if (e.getSource() == rightCode) {
            System.out.println("更换验证码");
            // 生成验证码
            String code = CodeUtil.getCode();
            rightCode.setText(code);
        }

    }

    /**
     * 鼠标按下不松
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == login) {
            login.setIcon(new ImageIcon("images\\login\\login_press.png"));
        } else if (e.getSource() == register) {
            register.setIcon(new ImageIcon("images\\login\\register_press.png"));
        }
    }

    /**
     * 鼠标松开
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == login) {
            login.setIcon(new ImageIcon("images\\login\\login.png"));
        } else if (e.getSource() == register) {
            register.setIcon(new ImageIcon("images\\login\\register.png"));
        }
    }

    /**
     * 鼠标划入
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * 鼠标划出
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 显示弹框
     */
    public void showJDialog(String content) {
        JDialog jDialog = new JDialog(); // 创建一个弹框对象
        jDialog.setSize(200, 150); // 设置弹框大小
        jDialog.setAlwaysOnTop(true);  // 设置弹框置顶显示
        jDialog.setLocationRelativeTo(null); // 设置弹框居中显示
        jDialog.setModal(true); // 设置弹框为模式对话框 弹框不关闭永远无法操作下面的界面

        JLabel warning = new JLabel(content); // 创建一个标签用于显示提示信息
        warning.setBounds(0,0, 200, 150);
        jDialog.getContentPane().add(warning); // 将标签添加到弹框中
        jDialog.setVisible(true); // 设置弹框可见
    }

    /**
     * 判断用户在集合中是否存在
     */
    public boolean isContains(User userInfo) {
        for (User rightUser : allUsers) {
            if (userInfo.getUsername().equals(rightUser.getUsername()) && userInfo.getPassword().equals(rightUser.getPassword())) {
                return true; // 用户存在
            }
        }
        return false;
    }
}
