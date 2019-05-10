/*
Author:王星洲
StudentID:1652977
FileName:RunProject.java
Introduction:这个文件是具体的实现，调用了Elevator的接口，包含main函数，实现了程序的功能
主要包括界面搭建，电梯查找，电梯管理等。
 */


//为界面搭建提供支持
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RunProject
{
    private static JLabel[] labels = new JLabel[20]; //楼层
    //为电梯组件添加按钮
    private static JLabel[] ELE1 = new JLabel[20],
            ELE2 = new JLabel[20],
            ELE3 = new JLabel[20],
            ELE4 = new JLabel[20],
            ELE5 = new JLabel[20];

    //上升下降队列锁
    static boolean[] upQueLock = new boolean[20], downQueLock = new boolean[20];
    private static JComboBox[] upSelect = new JComboBox[20]; //上升选择
    private static JComboBox[] downSelect = new JComboBox[20]; //下降选择

    private static Elevator one, two, three, four, five; //电梯
    static TextArea logs = new TextArea(); //输出日志
    private static ArrayList<Elevator> ELES = new ArrayList<>(); //电梯数组
    static ArrayList[] upQueue = new ArrayList[20], downQueue = new ArrayList[20]; //上升和下降请求队列


    @SuppressWarnings("unchecked")
    private static void init()
    {

        for (int i = 0; i < 20; i++)
        {
            // 队列锁初始化为可访问
            upQueLock[i] = true;
            downQueLock[i] = true;

            // 楼号初始化
            labels[i] = new JLabel(String.valueOf(i + 1));
//            labels[i].setBackground(Color.WHITE);
            labels[i].setOpaque(true);

            // 请求队列初始化
            // 上升等待队列
            upQueue[i] = new ArrayList<Integer>();
            // 下降等待队列
            downQueue[i] = new ArrayList<Integer>();

          // 上升选择键初始化
            upSelect[i] = new JComboBox();
            upSelect[i].addItem("-");
            //对于一个特定楼层，它的可选择楼层是不同的
            for(int m = i + 2; m <= 20; m++) upSelect[i].addItem(String.valueOf(m));
            final int finalI = i;
            upSelect[i].addItemListener(e -> {
                if (ItemEvent.SELECTED == e.getStateChange() && !Objects.requireNonNull(upSelect[finalI].getSelectedItem()).toString().equals("-"))
                {
                    upQueue[finalI].add(Integer.parseInt(Objects.requireNonNull(upSelect[finalI].getSelectedItem()).toString()));
                    if(labels[finalI].getBackground()== Color.PINK)
                    {
                        labels[finalI].setBackground(Color.CYAN);
                    }
                    else if(labels[finalI].getBackground()== Color.CYAN){
                        labels[finalI].setBackground(Color.CYAN);
                    }
                    else {
                        labels[finalI].setBackground(Color.GREEN);
                    }
                    upSelect[finalI].setSelectedIndex(0);
                    logs.append("第" + (finalI + 1) + "楼前往" + upQueue[finalI] + "楼\n");
                }
            });

            // 下降选择键初始化
            downSelect[i] = new JComboBox();
            downSelect[i].addItem("-");
            for(int m = i; m > 0; m--) downSelect[i].addItem(String.valueOf(m));
            downSelect[i].addItemListener(e -> {
                if (ItemEvent.SELECTED == e.getStateChange() && !Objects.requireNonNull(downSelect[finalI].getSelectedItem()).toString().equals("-"))
                {
                    downQueue[finalI].add(Integer.parseInt(Objects.requireNonNull(downSelect[finalI].getSelectedItem()).toString()));
                    if(labels[finalI].getBackground()== Color.GREEN)
                    {
                        labels[finalI].setBackground(Color.CYAN);
                    }
                    else if(labels[finalI].getBackground()== Color.CYAN){
                        labels[finalI].setBackground(Color.CYAN);
                    }
                    else {
                        labels[finalI].setBackground(Color.PINK);
                    }
                    downSelect[finalI].setSelectedIndex(0);
                    logs.append("第" + (finalI + 1) + "楼前往" + downQueue[finalI] + "楼\n");
                }
            });
        }

        // 电梯1初始化
        for (int i = 0; i < 20; i++)
        {
            ELE1[i] = new JLabel(" ", JLabel.CENTER);
            ELE1[i].setBorder(BorderFactory.createLineBorder(Color.black));
            ELE1[i].setOpaque(true);
            ELE1[i].setBackground(Color.WHITE);
            ELE1[i].setHorizontalTextPosition(SwingConstants.CENTER);
        }
        ELE1[0].setBackground(Color.RED);
        // 开/关
        ELE1[0].setText("关");



        // 电梯2初始化
        for (int i = 0; i < 20; i++)
        {
            ELE2[i] = new JLabel(" ", JLabel.CENTER);
            ELE2[i].setBorder(BorderFactory.createLineBorder(Color.black));
            ELE2[i].setOpaque(true);
            ELE2[i].setBackground(Color.WHITE);
        }
        ELE2[0].setBackground(Color.RED);
        // 开/关
        ELE2[0].setText("关");


        // 电梯3初始化
        for (int i = 0; i < 20; i++)
        {
            ELE3[i] = new JLabel(" ", JLabel.CENTER);
            ELE3[i].setBorder(BorderFactory.createLineBorder(Color.black));
            ELE3[i].setOpaque(true);
            ELE3[i].setBackground(Color.WHITE);
        }
        ELE3[0].setBackground(Color.RED);
        // 开/关
        ELE3[0].setText("关");


        // 电梯4初始化
        for (int i = 0; i < 20; i++)
        {
            ELE4[i] = new JLabel(" ", JLabel.CENTER);
            ELE4[i].setBorder(BorderFactory.createLineBorder(Color.black));
            ELE4[i].setOpaque(true);
            ELE4[i].setBackground(Color.WHITE);
        }
        ELE4[0].setBackground(Color.RED);
        // 开/关
        ELE4[0].setText("关");


        // 电梯5初始化
        for (int i = 0; i < 20; i++)
        {
            ELE5[i] = new JLabel(" ", JLabel.CENTER);
            ELE5[i].setBorder(BorderFactory.createLineBorder(Color.black));
            ELE5[i].setOpaque(true);
            ELE5[i].setBackground(Color.WHITE);
        }
        ELE5[0].setBackground(Color.RED);
        // 开/关
        ELE5[0].setText("关");


        //界面建立及初始化
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("电梯调度问题——1652977 王星洲");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //添加icon
        ImageIcon img = new ImageIcon("src/superman.jpg");
        frame.setIconImage(img.getImage());
        //设置布局
        frame.setLayout(new GridLayout(1, 2));
        GridLayout grid = new GridLayout(21, 8);
        Container c = new Container();
        c.setLayout(grid);
        JLabel.setDefaultLocale(Locale.CHINA);
        // 标签设置，并居中显示
        c.add(new JLabel("楼层", JLabel.CENTER));
        c.add(new JLabel("上升选项", JLabel.CENTER));
        c.add(new JLabel("下降选项", JLabel.CENTER));
        c.add(new JLabel("电梯一", JLabel.CENTER));
        c.add(new JLabel("电梯二", JLabel.CENTER));
        c.add(new JLabel("电梯三", JLabel.CENTER));
        c.add(new JLabel("电梯四", JLabel.CENTER));
        c.add(new JLabel("电梯五", JLabel.CENTER));
        for (int i = 20; i > 0; i--){
            labels[i - 1].setHorizontalAlignment(SwingConstants.CENTER);
        }
        // 按钮
        for (int i = 20; i > 0; i--) {
            c.add(labels[i - 1]);
            c.add(upSelect[i - 1]);
            c.add(downSelect[i - 1]);
            c.add(ELE1[i - 1]);
            c.add(ELE2[i - 1]);
            c.add(ELE3[i - 1]);
            c.add(ELE4[i - 1]);
            c.add(ELE5[i - 1]);
        }

        //输出日志设置及初始化
        logs.setEditable(false);
        logs.setFont(new Font("宋体",Font.PLAIN,25));
        logs.setBackground(Color.ORANGE);
        frame.add(c);
        JScrollPane pane = new JScrollPane(logs);
        frame.add(pane);

        //设置窗口大小
        frame.setSize(new Dimension(1500, 800));
        frame.setVisible(true);

        // 初始化电梯
        one = new Elevator(1, ELE1);
        ELES.add(one);
        two = new Elevator(2, ELE2);
        ELES.add(two);
        three = new Elevator(3, ELE3);
        ELES.add(three);
        four = new Elevator(4, ELE4);
        ELES.add(four);
        five = new Elevator(5,  ELE5);
        ELES.add(five);

        // 运行提示初始化
        //帮助弹窗
        String help = "<html><h2>运行提示:</h2>" +
                "<li>乘客在哪一个楼层，就选择哪个楼层对应的下拉框，设置乘客的目标</li>" +
                "<li>向上用绿色标记，向下用粉色标记，上下都有用青色标记</li>" +
                "<li>电梯所在楼层将显示红色，上下乘客时将显示蓝色并停顿</li>" +
                "<li>电梯上行时将显示“上升中”，电梯下行时将显示“下降中”</li>" +
                "<li>本题开门时间模拟为2秒，速度模拟为2层/秒</li>" +
                "<li>制作人：1652977 王星洲</li>" +
                "</html>";
        JOptionPane.showMessageDialog(null, help, "Help", JOptionPane.PLAIN_MESSAGE);
    }

    //当电梯任务结束时，将电梯对应楼层灯灭掉
    static class LightManager extends Thread
    {
        LightManager()
        {
            start();
        }
        public void run()
        {
            //noinspection InfiniteLoopStatement
            while (true)
            {
                for (int i = 0; i < 20; i++){

                    if (upQueue[i].isEmpty() && downQueue[i].isEmpty())
                        labels[i].setBackground(Color.WHITE);
                    else if (upQueue[i].isEmpty() && !downQueue[i].isEmpty())
                        labels[i].setBackground(Color.PINK);
                    else if (!upQueue[i].isEmpty() && downQueue[i].isEmpty())
                        labels[i].setBackground(Color.GREEN);
                    else
                        labels[i].setBackground(Color.CYAN);
                }
                try
                {
                    Thread.sleep(15);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //电梯管理方法
    static class ElevatorManager extends Thread
    {
        ElevatorManager()
        {
            start();
        }

        void adjust(int index, int i) throws InterruptedException
        {
            // 最优电梯位于当前楼层下方
            if (ELES.get(index).getCurrentFloor()< i)
            {
                //状态：上升，添加到上升队列，设置最高楼层
                ELES.get(index).setState(1);
                ELES.get(index).addUp(i);
                ELES.get(index).setMaxUp(i);
                logs.append("电梯" + (index + 1) + "上升\n");
                //停留1s
                Thread.sleep(200);
                return;
            }

            // 最优电梯位于当前楼层上方
            if (ELES.get(index).getCurrentFloor()> i)
            {
                //状态：下降，添加到下降队列，设置最低楼层
                ELES.get(index).setState(-1);
                ELES.get(index).addDown(i);
                ELES.get(index).setMinDown(i);
                logs.append("电梯" + (index + 1) + "下降\n");
                Thread.sleep(200);
                return;
            }

            // 最优电梯位于当前楼层
            if (ELES.get(index).getCurrentFloor() == i)
            {
                if(!upQueue[i].isEmpty()) {
                    ELES.get(index).setState(1);
                }
                else if(!downQueue[i].isEmpty()) {
                    ELES.get(index).setState(-1);
                }
                logs.append("电梯" + (index + 1) + "启动\n");
                Thread.sleep(200);
            }
        }

        //电梯调度方法
        public void run()
        {
            //noinspection InfiniteLoopStatement
            while (true)
            {
                for (int i = 0; i < 20; i++)
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    //等待权限
                    while (!upQueLock[i]);
                    //i楼上升请求队列不为空
                    if (!upQueue[i].isEmpty())
                    {
                        //初始化选择电梯及与目标间距离
                        int  selectID= -1,  distance = 999999;

                        //离i楼最近的可用电梯
                        for (int m = 0; m < 5; m++)
                        {
                            //如果电梯没有在运行
                            if (ELES.get(m).GetState() == 0 && !upQueue[i].isEmpty())
                            {
                                //比较两者距离绝对值与distance
                                if (Math.abs(ELES.get(m).getCurrentFloor() - i) < distance)
                                {
                                    //选择该电梯
                                    selectID = m;
                                    distance = Math.abs(ELES.get(m).getCurrentFloor() - i);
                                }
                            }

                            //电梯m所在楼层高于i楼&&电梯m为下降状态&&电梯m要去的最低楼层高于i
                            if (ELES.get(m).getCurrentFloor() >= i && ELES.get(m).GetState() == -1 && ELES.get(m).getMinDown() >= i)
                            {
                                if (Math.abs(ELES.get(m).getCurrentFloor() - i) < distance)
                                {
                                    selectID = -1;
                                    distance = Math.abs(ELES.get(m).getCurrentFloor() - i);
                                }
                            }

                            //电梯m所在楼层低于i楼+电梯m为上升状态+电梯m要去的最高楼层高于i
                            if (ELES.get(m).getCurrentFloor() <= i && ELES.get(m).GetState() == 1 && ELES.get(m).getMaxUp() >= i)
                            {
                                if (Math.abs(ELES.get(m).getCurrentFloor() - i) < distance)
                                {
                                    selectID = -1;
                                    distance = Math.abs(ELES.get(m).getCurrentFloor() - i);
                                }
                            }
                        }

                        //如果需要寻找电梯，调用adjust方法
                        if (selectID != -1 && !upQueue[i].isEmpty())
                        {
                            try
                            {
                                adjust(selectID, i);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }


                for (int i = 0; i < 20; i++)
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }


                    //等待权限
                    while (!downQueLock[i]);
                    //基本同上
                    if (!downQueue[i].isEmpty()) //i楼下降请求队列不为空
                    {
                        int index = -1,  distance = 9999999;
                        for (int m = 0; m < 5; m++)
                        {
                            //电梯m停止
                            if (ELES.get(m).GetState() == 0 && !downQueue[i].isEmpty())
                            {
                                if (Math.abs(ELES.get(m).getCurrentFloor() - i) < distance)
                                {
                                    index = m;
                                    distance = Math.abs(ELES.get(m).getCurrentFloor() - i);
                                }
                            }

                            //电梯m所在楼层高于i楼&&电梯m为下降状态+电梯m要去的最低楼层低于i
                            if (ELES.get(m).getCurrentFloor() >= i && ELES.get(m).GetState() == -1 && ELES.get(m).getMinDown() <= i){
                                if (Math.abs(ELES.get(m).getCurrentFloor() - i) < distance)
                                {
                                    index = -1;
                                    distance = Math.abs(ELES.get(m).getCurrentFloor() - i);
                                }
                            }

                            //电梯m所在楼层低于i楼&&电梯k为上升状态&&电梯k要去的最高楼层低于i
                            if (ELES.get(m).getCurrentFloor() <= i && ELES.get(m).GetState() == 1 && ELES.get(m).getMaxUp() <= i){
                                if (Math.abs(ELES.get(m).getCurrentFloor() - i) < distance)
                                {
                                    index = -1;
                                    distance = Math.abs(ELES.get(m).getCurrentFloor() - i);
                                }
                            }
                        }

                        if (index != -1 && !downQueue[i].isEmpty())
                        {
                            try
                            {
                                adjust(index, i);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args)
    {
        init();
        new LightManager();
        new ElevatorManager();
        one.start();
        two.start();
        three.start();
        four.start();
        five.start();
    }
}
