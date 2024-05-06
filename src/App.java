import com.ui.GameJFrame;
import com.ui.LoginJFrame;


import ui.LoginJFrame;

/*
运行类
 */
public class App {
    public static void main(String[] args) {

        //表示程序的启动入口
        //如果我们想要开启一个界面，就创建谁的对象就可以了
        //new LoginJFrame();
        new GameJFrame();

        // 创建对象  并显示登录窗口
        new LoginJFrame();

    }
}