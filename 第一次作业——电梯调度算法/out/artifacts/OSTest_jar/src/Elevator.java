/*
Author:王星洲
StudentID:1652977
FileName:Elevator.java
Introduction:这个文件是单独的电梯类的实现，包括电梯的属性及开关门，移动电梯等方法。
 */

//为操控JButton提供支持
import javax.swing.*;
import java.awt.*;
import java.util.*;

//电梯类的实现
public class Elevator extends Thread
{
    private int name;
    private int state; //该电梯当前运动状态 停止：0 上升：1 下降：-1
    private int currentFloor; //该电梯当前所在层数
    private int maxUp; //该电梯要去的最高楼层
    private int minDown; //该电梯要去的最低楼层

    //将int类型数据按照从大到小排列
    private Comparator<Integer> Up = Comparator.comparingInt(o -> o);
    //将int类型数据按照从小到大排列
    private Comparator<Integer> Down = (o1, o2) -> o2 - o1;
    private Queue<Integer> upStopList = new PriorityQueue<>(15, Up); //下降停止队列
    private Queue<Integer> downStopList = new PriorityQueue<>(15, Down); //上升停止队列
    private JButton[] buttonList; //按钮队列（RunProject）

    /*
    构造函数
    arguments:name buttonList
    */
    Elevator(int name, JButton[] buttonList)
    {
        this.name = name;
        maxUp = 0;
        minDown = 19;
        state = 0;
        currentFloor = 0;
        this.buttonList = buttonList;
    }

    /*
     返回当前状态
     return int
     */
    int GetState()
    {
        return state;
    }

    /*
    修改当前状态
    argument: state_int
    */
    void setState(int state)
    {
        this.state = state;
    }

    /*
      获取当前楼层
      return currentFloor
      */
    int getCurrentFloor()
    {
        return currentFloor;
    }

    /*
      添加向上目的地
     argument: aim_int
      */
    void addUp(Integer aim)
    {
        upStopList.add(aim);
    }

    /*
      添加向下目的地
     argument: aim_int
      */
    void addDown(Integer aim)
    {
        downStopList.add(aim);
    }

    /*
     获取要去的最高楼层
     return maxUp_int
     */
    int getMaxUp()
    {
        return maxUp;
    }

    /*
     设置要去的最高楼层
     argument maxUp_int
     */
    void setMaxUp(int maxUp)
    {
        this.maxUp = maxUp;
    }

    /*
     获取要去的最低楼层
     argument minDown_int
     */
    int getMinDown()
    {
        return minDown;
    }

    /*
     设置要去的最低楼层
     argument minDown_int
     */
    void setMinDown(int minDown)
    {
        this.minDown = minDown;
    }

    /*
     run函数：电梯工作流程
     void
     */
    public void run()
    {
        //noinspection InfiniteLoopStatement
        while(true)
        {
            // 下降状态
            while(state == -1)
            {
                boolean OpenDoors = false;
                buttonList[1].setText("下降中");

                // 上下客
                if (!downStopList.isEmpty() && currentFloor  == downStopList.peek())
                {
                    while (currentFloor  == downStopList.peek())
                    {
                        downStopList.poll();
                        RunProject.logs.append("电梯" + name + "停在" + (currentFloor + 1) + "楼\n");
                        RunProject.logs.append("电梯" + name + "开\n");
                        if(downStopList.isEmpty()) break;
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    buttonList[0].setBackground(Color.YELLOW);
                    buttonList[0].setText("开");
                    OpenDoors = true;
                }

                // 载上当前下降的人
                while (!RunProject.downQueLock[currentFloor]);
                RunProject.downQueLock[currentFloor] = false;
                if (!RunProject.downQueue[currentFloor].isEmpty())
                {
                    for (int i = 0; i < RunProject.downQueue[currentFloor].size(); i++)
                    {
                        if ((int) RunProject.downQueue[currentFloor].get(i) - 1 < minDown) minDown = (int) RunProject.downQueue[currentFloor].get(i) - 1;
                        addDown((Integer) RunProject.downQueue[currentFloor].get(i) - 1);
                        RunProject.logs.append("电梯" + name + "在第" + (currentFloor + 1) + "楼载上去" + RunProject.downQueue[currentFloor].get(i) + "楼的乘客\n");
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    buttonList[0].setBackground(Color.YELLOW);
                    buttonList[0].setText("开");
                    OpenDoors = true;
                }
                RunProject.downQueue[currentFloor].clear();
                RunProject.downQueLock[currentFloor] = true;

                // 电梯走空 载上向上的人
                while (!RunProject.upQueLock[currentFloor]);
                RunProject.upQueLock[currentFloor] = false;
                if (downStopList.isEmpty() && !RunProject.upQueue[currentFloor].isEmpty())
                {
                    for (int i = 0; i < RunProject.upQueue[currentFloor].size();i++)
                    {
                        if ((int)RunProject.upQueue[currentFloor].get(i) - 1 > maxUp) maxUp = (int)RunProject.upQueue[currentFloor].get(i) - 1;
                        addUp((Integer) RunProject.upQueue[currentFloor].get(i) - 1);
                        RunProject.logs.append("电梯" + name + "在第" + (currentFloor + 1) + "楼载上去" + RunProject.upQueue[currentFloor].get(i) + "楼的乘客\n");
                    }

                    if (!upStopList.isEmpty())
                    {
                        RunProject.upQueue[currentFloor].clear();
                        setState(1);
                        RunProject.upQueLock[currentFloor] = true;

                        try
                        {
                            Thread.sleep(2500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        buttonList[0].setText("关");
                        RunProject.logs.append("电梯" + name + "关\n");
                        buttonList[0].setBackground(Color.WHITE);
                        RunProject.logs.append("电梯" + name + "上升\n");
                        break;
                    }
                }
                RunProject.upQueLock[currentFloor] = true;

                if (OpenDoors) //停顿
                {
                    try
                    {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    buttonList[currentFloor].setBackground(Color.RED);
                    buttonList[0].setText("关");
                    RunProject.logs.append("电梯" + name + "关\n");
                    buttonList[0].setBackground(Color.WHITE);
                }

                // 电梯走空
                if (downStopList.isEmpty() || currentFloor == 0)
                {
                    buttonList[currentFloor].setBackground(Color.RED);
                    setState(0);
                    maxUp = 0;
                    minDown = 19;
                    RunProject.logs.append("电梯" + name + "停止\n");
                    break;
                }

                while(buttonList[0].getText().equals("开"))
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                buttonList[currentFloor].setBackground(Color.WHITE);
                currentFloor--;
                buttonList[currentFloor].setBackground(Color.RED);
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }

            // 上升状态
            while (state == 1)
            {
                //是否已经开门
                boolean OpenDoors = false;
                //二楼显示上升或下降状态
                buttonList[1].setText("上升中");

                // 上下客
                //如果电梯需要向上并且当前楼层是当前要去的楼层
                if (!upStopList.isEmpty() && currentFloor  == upStopList.peek())
                {
                    while (currentFloor  == upStopList.peek())
                    {
                        //当前楼层出队列
                        upStopList.poll();
                        RunProject.logs.append("电梯" + name + "停在" + (currentFloor + 1) + "楼\n");
                        RunProject.logs.append("电梯" + name + "开了\n");
                        if(upStopList.isEmpty()) break;
                    }
                    //设置蓝色
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    buttonList[0].setBackground(Color.YELLOW);
                    buttonList[0].setText("开");
                    OpenDoors = true;
                }

                // 载上当前上升的人
                //上升锁是否被占用
                while (!RunProject.upQueLock[currentFloor]);
                //请求锁住
                RunProject.upQueLock[currentFloor] = false;
                if (!RunProject.upQueue[currentFloor].isEmpty()) //该楼层上升请求队列不为空
                {

                    for (int i = 0; i < RunProject.upQueue[currentFloor].size(); i++)
                    {
                        //更改该电梯要去的最高楼层
                        if ((int) RunProject.upQueue[currentFloor].get(i) - 1 > maxUp){
                            maxUp = (int) RunProject.upQueue[currentFloor].get(i) - 1;
                        }
                        addUp((Integer) RunProject.upQueue[currentFloor].get(i) - 1); //将请求加入该电梯上升停止队列
                        RunProject.logs.append("电梯" + name + "在第" + (currentFloor + 1) + "楼载上去" + RunProject.upQueue[currentFloor].get(i) + "楼的乘客\n");
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    buttonList[0].setBackground(Color.YELLOW);
                    buttonList[0].setText("开");
                    OpenDoors = true;
                }
                RunProject.upQueue[currentFloor].clear(); //清空该楼层上升请求队列
                RunProject.upQueLock[currentFloor] = true;

                // 电梯走空 载上向下的人
                while (!RunProject.downQueLock[currentFloor]);
                RunProject.downQueLock[currentFloor] = false;
                if (upStopList.isEmpty() && !RunProject.downQueue[currentFloor].isEmpty()) //该楼层上升停止队列为空且下降请求队列不为空
                {
                    for (int i = 0; i < RunProject.downQueue[currentFloor].size();i++)
                    {
                        if ((int)RunProject.downQueue[currentFloor].get(i) - 1 < minDown) minDown = (int)RunProject.downQueue[currentFloor].get(i) - 1; //更改该电梯要去的最低楼层
                        addDown((Integer) RunProject.downQueue[currentFloor].get(i) - 1); //将请求加入该电梯下降停止队列
                        RunProject.logs.append("电梯" + name + "在第" + (currentFloor + 1) + "楼载上去" + RunProject.downQueue[currentFloor].get(i) + "楼的乘客\n");
                    }

                    if (!downStopList.isEmpty()) //该电梯下降停止队列不为空 则输出下降信息
                    {
                        RunProject.downQueue[currentFloor].clear(); //清空该楼层下降请求队列
                        setState(-1); //更改该电梯当前状态为下降
                        RunProject.downQueLock[currentFloor] = true;

                        try
                        {
                            Thread.sleep(2500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        buttonList[0].setText("关");
                        RunProject.logs.append("电梯" + name + "关\n");
                        buttonList[0].setBackground(Color.WHITE);
                        RunProject.logs.append("电梯" + name + "下降\n");
                        break;
                    }
                }
                RunProject.downQueLock[currentFloor] = true;

                if (OpenDoors) //停顿
                {
                    try
                    {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    buttonList[currentFloor].setBackground(Color.RED);
                    buttonList[0].setText("关");
                    RunProject.logs.append("电梯" + name + "关了\n");
                    buttonList[0].setBackground(Color.WHITE);
                }

                // 电梯空了
                if (upStopList.isEmpty() || currentFloor == 19)
                {
                    setState(0); //修改该电梯状态为停止
                    maxUp = 0;
                    minDown = 19;
                    buttonList[currentFloor].setBackground(Color.RED);
                    RunProject.logs.append("电梯" + name + "停止\n");
                    break;
                }

                while(buttonList[0].getText().equals("开"))
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                buttonList[currentFloor].setBackground(Color.WHITE);
                currentFloor++; //上一层红灯亮
                buttonList[currentFloor].setBackground(Color.RED);

                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // 电梯停止
            while(state == 0)
            {
                buttonList[1].setText("停止");
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // 防止线程阻塞
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
