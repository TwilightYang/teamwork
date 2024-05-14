package com.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameJFrame extends JFrame implements KeyListener, ActionListener {
    //JFrame 界面，窗体
    //子类呢？也表示界面，窗体
    //规定：GameJFrame这个界面表示的就是游戏的主界面
    //以后跟游戏相关的所有逻辑都写在这个类中

    //创建一个二维数组
    //目的：用来管理数据
    //加载图片的时候，会根据二维数组中的数据进行加载
    private int[][] data = new int[4][4];

    //记录空白方块在二维数组中的位置
    private int x = 0;
    private int y = 0;

    //定义一个变量，记录当前展示图片的路径
    private String pathMain = "image";
    private String style = "animal";
    private int numberNO = 1;

    //定义变量，记录当前音乐的路径
    private volatile String pathMusic = "music";
    private volatile String musicStyle = "animal";
    private volatile int musicNO = 1;
    private volatile boolean musicIsPlaying = false;
    private static volatile Clip music;
    private long microsecondLength = 0;
    private long microsecondPosition = 0;
    private volatile boolean musicCircle = true;
    private volatile boolean musicIsNext = true;
    private volatile boolean musicContinus = false;


    //定义一个二维数组，存储正确的数据
    private int[][] win = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    private Thread thread;
    //定义变量用来统计步数
    private int step = 0;


    //创建选项下面的条目对象
    private final JMenuItem replayItem = new JMenuItem("重新游戏");
    private final JMenuItem reLoginItem = new JMenuItem("重新登录");
    private final JMenuItem closeItem = new JMenuItem("关闭游戏");

    private final JMenuItem accountItem = new JMenuItem("公众号");

    private final JMenuItem girls = new JMenuItem("美女");
    private final JMenuItem animals = new JMenuItem("动物");
    private final JMenuItem sports = new JMenuItem("运动");

    private final JMenuItem change = new JMenuItem("模式转换（3*3）");


    public GameJFrame() {
        //初始化界面
        initJFrame();

        //初始化菜单
        initJMenuBar();

        //初始化数据（打乱）
        initData();

        //初始化图片（根据打乱之后的结果去加载图片）
        initImage();

        //初始化音乐（默认为关闭状态）
        initMusic();

        //让界面显示出来，建议写在最后
        this.setVisible(true);

    }


    //初始化数据（打乱）
    public void initData() {
        //1.定义一个一维数组
        int[] tempArr = {1, 2, 3, 4, 8, 7, 6, 10, 11, 12, 15, 14, 13, 9, 5, 0};
        //2.打乱数组中的数据的顺序
        //遍历数组，得到每一个元素，拿着每一个元素跟随机索引上的数据进行交换
        Random r = new Random();
        for (int i = 0, len = tempArr.length - 1; i < len; ) {
            //获取到随机索引
            int index = r.nextInt(len);
            if ((Math.abs(index - i)) % 2 == 0) {
                //拿着遍历到的每一个数据，跟随机索引上的数据进行交换
                int left = Math.min(i, index);
                int right = Math.max(i, index);
                int temp = tempArr[left];
                while (left < right) {
                    tempArr[left] = tempArr[left + 1];
                    left++;
                }
                tempArr[right] = temp;
                i++;
            }
        }
        System.out.println(Arrays.toString(tempArr));
        /*
         *
         *           5   6   8   9
         *           10  11  15  1
         *           4   7   12  13
         *           2   3   0  14
         *
         *           5   6   8   9   10  11  15  1   4   7   12  13  2   3   0   14
         * */

        //4.给二维数组添加数据
        //遍历一维数组tempArr得到每一个元素，把每一个元素依次添加到二维数组当中
        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                x = i / 4;
                y = i % 4;
            }
            data[i / 4][i % 4] = tempArr[i];
        }
    }


    /**
     * 初始化图片
     * 添加图片的时候，就需要按照二维数组中管理的数据添加图片
     */
    public void initImage() {

        //清空原本已经出现的所有图片
        this.getContentPane().removeAll();

        if (victory()) {
            //显示胜利的图标
            JLabel winJLabel = new JLabel(new ImageIcon("image\\win.png"));
            winJLabel.setBounds(203, 283, 197, 73);
            this.getContentPane().add(winJLabel);
        }

        //步数
        JLabel stepCount = new JLabel("步数：" + step);
        stepCount.setBounds(50, 30, 100, 20);
        this.getContentPane().add(stepCount);

        //重新初始化音乐（防止音乐被删）
        initMusic();

        //路径分为两种：
        //绝对路径：一定是从盘符开始的。C:\  D：\
        //相对路径：不是从盘符开始的
        //相对路径相对当前项目而言的。 aaa\\bbb
        //在当前项目下，去找aaa文件夹，里面再找bbb文件夹。

        //细节：
        //先加载的图片在上方，后加载的图片塞在下面。
        //外循环 --- 把内循环重复执行了4次。
        for (int i = 0; i < 4; i++) {
            //内循环 --- 表示在一行添加4张图片
            for (int j = 0; j < 4; j++) {
                //获取当前要加载图片的序号
                int num = data[i][j];
                //创建一个JLabel的对象（管理容器）
                JLabel jLabel = new JLabel(new ImageIcon(pathMain + "/" + style + "/" + style + numberNO + "/" + num + ".jpg"));
                //指定图片位置
                jLabel.setBounds(105 * j + 83, 105 * i + 134, 105, 105);
                //给图片添加边框
                //0:表示让图片凸起来
                //1：表示让图片凹下去
                jLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                //把管理容器添加到界面中
                this.getContentPane().add(jLabel);
            }
        }


        //添加背景图片
        JLabel background = new JLabel(new ImageIcon("image\\background.png"));
        background.setBounds(40, 40, 508, 560);

        //把背景图片添加到界面当中
        this.getContentPane().add(background);

        //刷新一下界面
        this.getContentPane().repaint();


    }

    public void initJMenuBar() {
        //创建整个的菜单对象
        JMenuBar jMenuBar = new JMenuBar();
        //创建菜单上面的两个选项的对象 （功能  关于我们）
        JMenu functionJMenu = new JMenu("功能");
        JMenu aboutJMenu = new JMenu("关于我们");
        JMenu styleJMenu = new JMenu("风格");


        //将每一个选项下面的条目天极爱到选项当中
        functionJMenu.add(replayItem);
        functionJMenu.add(reLoginItem);
        functionJMenu.add(closeItem);
        styleJMenu.add(animals);
        styleJMenu.add(sports);
        styleJMenu.add(girls);

        aboutJMenu.add(accountItem);

        //给条目绑定事件
        replayItem.addActionListener(this);
        reLoginItem.addActionListener(this);
        closeItem.addActionListener(this);
        animals.addActionListener(this);
        sports.addActionListener(this);
        girls.addActionListener(this);
        accountItem.addActionListener(this);
        change.addActionListener(this);

        //将菜单里面的两个选项添加到菜单当中
        jMenuBar.add(functionJMenu);
        jMenuBar.add(aboutJMenu);
        jMenuBar.add(styleJMenu);
        jMenuBar.add(change);


        //给整个界面设置菜单
        this.setJMenuBar(jMenuBar);
    }

    private volatile File file = null;
    private volatile AudioInputStream audioInputStream = null;
    public void initMusic() {
        //显示音乐相关图标
        JLabel musicStart = new JLabel(new ImageIcon("image\\musicImage\\pause.jpg"));
        musicStart.setBounds(400,30,30,30);
        this.getContentPane().add(musicStart);

        JLabel musicNext = new JLabel(new ImageIcon("image\\musicImage\\next.jpg"));
        musicNext.setBounds(440,30,30,30);
        this.getContentPane().add(musicNext);

        JLabel musicSingleCircle = new JLabel(new ImageIcon("image\\musicImage\\circle.jpg"));
        musicSingleCircle.setBounds(480,30,30,30);
        this.getContentPane().add(musicSingleCircle);

        //给三个类按钮添加监听事件
        try {
            //加载音乐
            file = new File(pathMusic + "\\" + musicNO + musicStyle + ".wav");
            audioInputStream = AudioSystem.getAudioInputStream(file);
//            AudioFormat audioFormat = audioInputStream.getFormat();   不知道有啥用
            if (music != null) { // 释放旧的音乐
                music.stop();
                music.flush();
                music.close();
            }
            music = AudioSystem.getClip();
            music.open(audioInputStream);
            music.addLineListener(
                    new LineListener() {
                        @Override
                        public void update(LineEvent event) {
                            try {
                                System.out.println(event.getType());
                                System.out.println(event.getFramePosition());

                                if (event.getType().equals(LineEvent.Type.CLOSE) && musicContinus) {
                                    if (musicCircle) {
                                        if (musicNO >= 3) musicNO = 1;
                                        else musicNO++;
                                    }
                                    file = new File(pathMusic + "\\" + musicNO + musicStyle + ".wav");
                                    audioInputStream = AudioSystem.getAudioInputStream(file);
                                    music.open(audioInputStream);
                                    music.start();
                                } else if (event.getType().equals(LineEvent.Type.STOP)) {
                                    if (music.getMicrosecondLength() != music.getMicrosecondPosition())
                                        return ;
                                    music.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

            //播放与暂停按钮
            musicStart.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("pause / start");
                    if (!musicIsPlaying) {
                        //播放音乐
                        music.setMicrosecondPosition(microsecondPosition);
                        music.start();
                        musicStart.setIcon(new ImageIcon("image\\musicImage\\start.jpg"));
                        musicIsPlaying = true;
                    } else {
                        //暂停音乐
                        music.stop();
                        microsecondPosition = music.getMicrosecondPosition();
                        musicStart.setIcon(new ImageIcon("image\\musicImage\\pause.jpg"));
                        musicIsPlaying = false;
                        musicContinus = true;
                    }
                }
            });

            //播放下一首按钮
            musicNext.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("next");
                    musicContinus = true;
                    music.close();
                    //重新加载音乐
                    microsecondPosition = 0;
                    musicStart.setIcon(new ImageIcon("image\\musicImage\\pause.jpg"));
                    sleepThread(1000);
                    musicStart.setIcon(new ImageIcon("image\\musicImage\\start.jpg"));
                    music.start();
                }
            });

            //单曲循环按钮
            musicSingleCircle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("circle / single");
                    if (musicCircle) {
                        musicSingleCircle.setIcon(new ImageIcon("image\\musicImage\\singleCircle.jpg"));
                        musicCircle = false;
                    } else {
                        musicSingleCircle.setIcon(new ImageIcon("image\\musicImage\\circle.jpg"));
                        musicCircle = true;
                    }
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "无法加载音乐文件: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initJFrame() {
        //设置界面的宽高
        this.setSize(603, 680);
        //设置界面的标题
        this.setTitle("拼图单机版 v1.0");
        //设置界面置顶
        this.setAlwaysOnTop(true);
        //设置界面居中
        this.setLocationRelativeTo(null);
        //设置关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //取消默认的居中放置，只有取消了才会按照XY轴的形式添加组件
        this.setLayout(null);
        //给整个界面添加键盘监听事件
        this.addKeyListener(this);

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    //按下不松时会调用这个方法
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == 65) {//按键a
            //把界面中所有的图片全部删除
            this.getContentPane().removeAll();
            //加载第一张完整的图片image/animal/animal1
            JLabel all = new JLabel(new ImageIcon(pathMain + "/" + style + "/" + style + numberNO + "/" + "all.jpg"));
            all.setBounds(83, 134, 420, 420);
            this.getContentPane().add(all);
            //加载背景图片
            //添加背景图片
            JLabel background = new JLabel(new ImageIcon("image/background.png"));
            background.setBounds(40, 40, 508, 560);
            //把背景图片添加到界面当中
            this.getContentPane().add(background);
            //刷新界面
            this.getContentPane().repaint();
        } else if (code == 87) {
            if (thread==null) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        restoration();
                    }
                });
                thread.start();
            }
        }
    }

    //松开按键的时候会调用这个方法
    @Override
    public void keyReleased(KeyEvent e) {
        //判断游戏是否胜利，如果胜利，此方法需要直接结束，不能再执行下面的移动代码了
        if (victory()) {
            //结束方法
            return;
        }
        //对上，下，左，右进行判断
        //左：37 上：38 右：39 下：40
        int code = e.getKeyCode();
        System.out.println(code);
        if (code == 37) {
            System.out.println("向左移动");
            if (y == 3) {
                return;
            }
            //逻辑：
            //把空白方块右方的数字往左移动
            data[x][y] = data[x][y + 1];
            data[x][y + 1] = 0;
            y++;
            //每移动一次，计数器就自增一次。
            step++;
            //调用方法按照最新的数字加载图片
            initImage();

        } else if (code == 38) {
            System.out.println("向上移动");
            if (x == 3) {
                //表示空白方块已经在最下方了，他的下面没有图片再能移动了
                return;
            }
            //逻辑：
            //把空白方块下方的数字往上移动
            //x，y  表示空白方块
            //x + 1， y 表示空白方块下方的数字
            //把空白方块下方的数字赋值给空白方块
            data[x][y] = data[x + 1][y];
            data[x + 1][y] = 0;
            x++;
            //每移动一次，计数器就自增一次。
            step++;
            //调用方法按照最新的数字加载图片
            initImage();
        } else if (code == 39) {
            System.out.println("向右移动");
            if (y == 0) {
                return;
            }
            //逻辑：
            //把空白方块左方的数字往右移动
            data[x][y] = data[x][y - 1];
            data[x][y - 1] = 0;
            y--;
            //每移动一次，计数器就自增一次。
            step++;
            //调用方法按照最新的数字加载图片
            initImage();
        } else if (code == 40) {
            System.out.println("向下移动");
            if (x == 0) {
                return;
            }
            //逻辑：
            //把空白方块上方的数字往下移动
            data[x][y] = data[x - 1][y];
            data[x - 1][y] = 0;
            x--;
            //每移动一次，计数器就自增一次。
            step++;
            //调用方法按照最新的数字加载图片
            initImage();
        } else if (code == 65) {
            initImage();
        } else /*data = new int[][]{
                    {1, 2, 3, 4},
                    {5, 6, 7, 8},
                    {9, 10, 11, 12},
                    {13, 14, 15, 0}
            };
            initImage();*/ //initImage();
            if (code == 87) {
                thread.stop();
                thread = null;
                //线程中断，要进行更新图片，避免0方块的位置错乱
                initImage();
                int[] zero = findZero();
                x = zero[0];
                y = zero[1];
                System.out.println(Arrays.toString(zero));
            }
    }


    //判断data数组中的数据是否跟win数组中相同
    //如果全部相同，返回true。否则返回false
    public boolean victory() {
        for (int i = 0; i < data.length; i++) {
            //i : 依次表示二维数组 data里面的索引
            //data[i]：依次表示每一个一维数组
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] != win[i][j]) {
                    //只要有一个数据不一样，则返回false
                    return false;
                }
            }
        }
        thread = null;
        //循环结束表示数组遍历比较完毕，全都一样返回true
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //获取当前被点击的条目对象
        Object obj = e.getSource();
        //判断
        if (obj == replayItem) {
            System.out.println("重新游戏");
            //计步器清零
            step = 0;
            //再次打乱二维数组中的数据
            initData();
            //重新加载图片
            initImage();
            //重新加载音乐
            initMusic();
        } else if (obj == reLoginItem) {
            System.out.println("重新登录");
            //关闭当前的游戏界面
            this.setVisible(false);
            //打开登录界面
            new LoginJFrame();
        } else if (obj == closeItem) {
            System.out.println("关闭游戏");
            //直接关闭虚拟机即可
            System.exit(0);
        } else if (obj == accountItem) {
            System.out.println("公众号");

            //创建一个弹框对象
            JDialog jDialog = new JDialog();
            //创建一个管理图片的容器对象JLabel
            JLabel jLabel = new JLabel(new ImageIcon("image\\about.png"));
            //设置位置和宽高
            jLabel.setBounds(0, 0, 258, 258);
            //把图片添加到弹框当中
            jDialog.getContentPane().add(jLabel);
            //给弹框设置大小
            jDialog.setSize(344, 344);
            //让弹框置顶
            jDialog.setAlwaysOnTop(true);
            //让弹框居中
            jDialog.setLocationRelativeTo(null);
            //弹框不关闭则无法操作下面的界面
            jDialog.setModal(true);
            //让弹框显示出来
            jDialog.setVisible(true);
        } else if (obj == girls) {
            if (style.equals("girl")) {
                numberNO = ++numberNO % 14;
                if (numberNO == 0) {
                    numberNO = 1;
                }
            } else numberNO = 1;
            style = "girl";
            step = 0;
            musicStyle = "girl";
            //初始化数据（打乱）
            initData();
            initImage();
            initMusic();
        } else if (obj == animals) {
            if (style.equals("animal")) {
                numberNO = ++numberNO % 9;
                if (numberNO == 0) {
                    numberNO = 1;
                }
            } else numberNO = 1;
            style = "animal";
            step = 0;
            musicStyle = "animal";
            //初始化数据（打乱）
            initData();
            initImage();
            initMusic();
        } else if (obj == sports) {
            if (style.equals("sport")) {
                numberNO = ++numberNO % 11;
                if (numberNO == 0) {
                    numberNO = 1;
                }
            } else numberNO = 1;
            style = "sport";
            step = 0;
            musicStyle = "sport";
            //初始化数据（打乱）
            initData();
            initImage();
            initMusic();
        } else if (obj == change) {
            this.setVisible(false);
            if (music != null) { // 释放旧的音乐
                music.stop();
                music.flush();
                music.close();
            }
            new GameJFrame2();
        }
    }


    public int[] findZero() {
        int hang = data.length - 1, lie = data[0].length - 1;
        for (int j = 0, dataLength = data.length; j < dataLength; j++) {
            int[] ints = data[j];
            for (int k = 0, intsLength = ints.length; k < intsLength; k++) {
                int i = ints[k];
                if (i == 0) {
                    hang = j;
                    lie = k;
                }
            }
        }
        return new int[]{hang, lie};
    }
    public int[] findIndex(int a){
        int hang = data.length - 1, lie = data[0].length - 1;
        for (int j = 0, dataLength = data.length; j < dataLength; j++) {
            int[] ints = data[j];
            for (int k = 0, intsLength = ints.length; k < intsLength; k++) {
                int i = ints[k];
                if (i == a) {
                    hang = j;
                    lie = k;
                }
            }
        }
        return new int[]{hang, lie};
    }
    public void restoration(){
        boolean[][] flag = new boolean[data.length][data[0].length];
        for (boolean[] booleans : flag) {
            Arrays.fill(booleans,true);
        }
        int k = 0;
        for (; k < data.length-2; k++) {
            for (int i = k, j = k; j < data.length; j++) {
                int[] temp = findIndex(win[i][j]);
                if (temp[0]==i&&temp[1]==j)flag[i][j] = false;
                else if (j==data[0].length-1&& !flag[i][j-1])specialMove(temp, new int[]{i, j}, findZero(), flag);
                else move(temp, new int[]{i,j}, findZero(), flag);
            }
            for (int i = k, j = k; i < data.length; i++) {
                int[] temp = findIndex(win[i][j]);
                if (temp[0]==i&&temp[1]==j)flag[i][j] = false;
                else if (i==data.length-1&& !flag[i-1][j])specialMove(temp, new int[]{i, j}, findZero(), flag);
                else move(temp, new int[]{i,j}, findZero(), flag);
            }
        }
        move(findIndex(win[k][k]), new int[]{k,k}, findZero(), flag);
    }

    public void specialMove(int[] begin, int[] end, int[] zeroIndex, boolean[][] flag) {
        int i = 0, j = 0;
        if (end[0] == data.length - 1) {
            move(begin, new int[]{end[0], end[1] + 2}, zeroIndex, flag);
            begin = new int[]{end[0], end[1] + 2};
            i = begin[0];
            j = begin[1];
            flag[begin[0]][begin[1]] = false;
            List<int[]> zeroPath = zeroPath(zeroIndex, new int[]{end[0], end[1]}, flag);
            for (int[] ints : zeroPath) {
                swap(ints, zeroIndex);
            }
            swap(zeroIndex, new int[]{end[0] - 1, end[1]});
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] + 1});
            swap(zeroIndex, new int[]{zeroIndex[0] + 1, zeroIndex[1]});
            swap(zeroIndex, begin);
            swap(zeroIndex, new int[]{zeroIndex[0] - 1, zeroIndex[1]});
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] - 1});
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] - 1});
            swap(zeroIndex, new int[]{zeroIndex[0] + 1, zeroIndex[1]});
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] + 1});
        } else {
            move(begin, new int[]{end[0] + 2, end[1]}, zeroIndex, flag);
            begin = new int[]{end[0] + 2, end[1]};
            i = begin[0];
            j = begin[1];
            flag[begin[0]][begin[1]] = false;
            List<int[]> zeroPath = zeroPath(zeroIndex, new int[]{end[0], end[1]}, flag);
            for (int[] ints : zeroPath) {
                swap(ints, zeroIndex);
            }
            swap(zeroIndex, new int[]{end[0], end[1] - 1});
            swap(zeroIndex, new int[]{zeroIndex[0] + 1, zeroIndex[1]});
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] + 1});
            swap(zeroIndex, begin);
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] - 1});
            swap(zeroIndex, new int[]{zeroIndex[0] - 1, zeroIndex[1]});
            swap(zeroIndex, new int[]{zeroIndex[0] - 1, zeroIndex[1]});
            swap(zeroIndex, new int[]{zeroIndex[0], zeroIndex[1] + 1});
            swap(zeroIndex, new int[]{zeroIndex[0] + 1, zeroIndex[1]});
        }
        flag[end[0]][end[1]] = false;
        flag[i][j] = true;
    }

    public void move(int[] begin, int[] end, int[] zeroIndex, boolean[][] flag) {
        flag[begin[0]][begin[1]] = false;
        List<int[]> path = zeroPath(begin, end, flag);
        for (int[] ints : path) {
            List<int[]> zeroPath = zeroPath(zeroIndex, ints, flag);
            for (int[] zero : zeroPath) {
                swap(zero, zeroIndex);
            }
            flag[begin[0]][begin[1]] = true;
            swap(begin, zeroIndex);
            begin = ints;
            flag[begin[0]][begin[1]] = false;
        }
        if (zeroIndex[0]==data.length-1&&end[1]==zeroIndex[1])swap(zeroIndex, new int[]{data.length-1, end[1]+1});
        else if (zeroIndex[0]==end[0]&&zeroIndex[1]==data.length-1)swap(zeroIndex, new int[]{end[0]+1, data.length-1});
    }

    public void swap(int[] a, int[] b) {
        if (a[0] < 0 || a[0] >= data.length || b[0] < 0 || b[0] >= data.length || a[1] < 0 || a[1] >= data[0].length || b[1] < 0 || b[1] >= data[0].length)
            return;
        int temp = data[a[0]][a[1]];
        data[a[0]][a[1]] = data[b[0]][b[1]];
        data[b[0]][b[1]] = temp;
        int temp1 = a[0], temp2 = a[1];
        a[0] = b[0];
        a[1] = b[1];
        b[0] = temp1;
        b[1] = temp2;
        step++;
        initImage();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出空白块从起点到终点的路径，考虑障碍，不包起点
     *
     * @param begin         起点
     * @param end           终点
     * @param Reachable 障碍表
     * @return 路径集合
     */
    public List<int[]> zeroPath(int[] begin, int[] end, boolean[][] Reachable) {
        int[][] place = new int[Reachable.length][Reachable[0].length];
        for (int i = 0; i < Reachable.length; i++) {
            for (int j = 0; j < Reachable[i].length; j++) {
                if (Reachable[i][j])place[i][j] = 1;
                else place[i][j] = Integer.MAX_VALUE;
            }
        }
        List<int[]> list = findLeastCostPath(place, begin[0], begin[1], end[0], end[1]);
        if (!list.isEmpty())list.remove(0);
        return list;
    }
    public static class Node {
        int x, y;
        int fatherX = -1, fatherY = -1;

        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }
        public Node(int x, int y, int fatherX, int fatherY){
            this.fatherX = fatherX;
            this.fatherY = fatherY;
            this.x = x;
            this.y = y;
        }
        public void addFather(int fatherX, int fatherY){
            this.fatherX = fatherX;
            this.fatherY = fatherY;
        }
        public void setFather(int fatherX, int fatherY){
            addFather(fatherX,fatherY);
        }
        public int[] getFather(int x, int y){
            if (x==this.x&&y==this.y)return new int[]{fatherX,fatherY};
            return null;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    public List<int[]> findLeastCostPath(int[][] place, int beginX, int beginY, int endX, int endY){
        if (place[endX][endY]==Integer.MAX_VALUE)return new ArrayList<>();
        Set<Node> closeSet = new HashSet<>();//记录走过的路径
        Set<Node> openSet = new HashSet<>();//记录可以走的路
        int[][] costList = new int[place.length][place[0].length];
        for (int[] ints : costList) {
            Arrays.fill(ints, Integer.MAX_VALUE);
        }

        costList[beginX][beginY] = 0;
        openSet.add(new Node(beginX,beginY));

        while(openSet.size()!=0){
            Node node = new Node(-1,-1);
            int min = Integer.MAX_VALUE;
            for (Node n : openSet) {
                if(costList[n.x][n.y]<min){
                    beginX = n.x;
                    beginY = n.y;
                    node = n;
                    min = costList[beginX][beginY];
                }
            }
            if (beginX+1<place.length&&place[beginX+1][beginY]!=Integer.MAX_VALUE&&costList[beginX+1][beginY]>costList[beginX][beginY]+place[beginX+1][beginY]){
                openSet.add(new Node(beginX+1,beginY, beginX, beginY));
                costList[beginX+1][beginY]=costList[beginX][beginY]+place[beginX+1][beginY];
            }
            if (beginX>0&&place[beginX-1][beginY]!=Integer.MAX_VALUE&&costList[beginX-1][beginY]>costList[beginX][beginY]+place[beginX-1][beginY]){
                openSet.add(new Node(beginX-1,beginY,beginX,beginY));
                costList[beginX-1][beginY]=costList[beginX][beginY]+place[beginX-1][beginY];
            }
            if (beginY+1<place[0].length&&place[beginX][beginY+1]!=Integer.MAX_VALUE&&costList[beginX][beginY+1]>costList[beginX][beginY]+place[beginX][beginY+1]){
                openSet.add(new Node(beginX,beginY+1,beginX,beginY));
                costList[beginX][beginY+1]=costList[beginX][beginY]+place[beginX][beginY+1];
            }
            if (beginY>0&&place[beginX][beginY-1]!=Integer.MAX_VALUE&&costList[beginX][beginY-1]>costList[beginX][beginY]+place[beginX][beginY-1]){
                openSet.add(new Node(beginX,beginY-1,beginX,beginY));
                costList[beginX][beginY-1]=costList[beginX][beginY]+place[beginX][beginY-1];
            }
            openSet.remove(node);
            closeSet.add(node);
        }
        if (!closeSet.contains(new Node(endX, endY)))return new ArrayList<>();
        List<int[]> temp = new ArrayList<>();
        temp.add(new int[]{endX,endY});
        while (true) {
            for (Node node : closeSet) {
                int[] father = node.getFather(endX, endY);
                if (father != null) {
                    endX = father[0];
                    endY = father[1];
                    temp.add(new int[]{endX,endY});
                    closeSet.remove(node);
                    break;
                }
            }
            if (endX==-1||endY==-1){
                temp.remove(temp.size()-1);
                break;
            }
        }
        List<int[]> path = new ArrayList<>();
        for (int i = temp.size()-1; i > -1; i--) {
            path.add(temp.get(i));
        }
        return path;
    }
    /**
     * 输出起始点到终点的路径，不考虑障碍，不包起点
     *
     * @param begin 起点
     * @param end   终点
     * @return 路径集合
     */
    public List<int[]> findPath(int[] begin, int[] end) {
        int hangOfBegin = begin[0];
        int lieOfBegin = begin[1];
        int hangOfEnd = end[0];
        int lieOfEnd = end[1];
        List<int[]> path = new ArrayList<>();
        int hang = hangOfEnd - hangOfBegin;
        int lie = lieOfEnd - lieOfBegin;
        if (hang < 0) {
            while (hang != 0) {
                path.add(new int[]{--hangOfBegin, lieOfBegin});
                hang++;
            }
        } else if (hang > 0) {
            while (hang != 0) {
                path.add(new int[]{++hangOfBegin, lieOfBegin});
                hang--;
            }
        }
        if (lie < 0) {
            while (lie != 0) {
                path.add(new int[]{hangOfBegin, --lieOfBegin});
                lie++;
            }
        } else if (lie > 0) {
            while (lie != 0) {
                path.add(new int[]{hangOfBegin, ++lieOfBegin});
                lie--;
            }
        }
        return path;
    }

    void sleepThread(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {

        }
    }
}
