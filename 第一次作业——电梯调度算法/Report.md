# Elevator Management

本报告是针对OS第一次作业——电梯调度算法的报告，在阅读本报告前，请先阅读`ReadMe.md`，并确保成功执行了`ElevatorManagement.jar`。如有任何问题，欢迎与我取得联系。

如需要查询属性及功能说明，请移步`ReadMe.md`。

作者：1652977——王星洲



## 问题分析

本题目需要实现一栋20层大楼中的5部电梯调度问题，主要难点有以下几方面：

* 界面设计制作
* 电梯功能实现
* 电梯调度算法

接下来我将分别从这几个方面一一说明我的实现。



## 具体实现

### 界面设计

实现一个GUI界面有多种方式，比如：

* C# winform
* C++ + Qt
* Java.swing
* python + pyQt
* ...

本题目中，为了使用便利，采用Java.swing来设计图形界面。设计思路如下：

1. 首先`frame.setLayout(new GridLayout(1, 2));`将界面分为两块grid和logs

![](img\screenShot1.png)

2. 再将grid界面分为21行8列`GridLayout grid = new GridLayout(21, 8);`
3. 楼层列为JLabel及电梯列为JLabel，上升下降选项为JComboBox
4. 在电梯列的第0和第1层分别写入开关门和当前状态信息
5. frame设计了Icon
6. 进入界面时有运行说明提示框弹出



### 电梯功能实现

电梯具体函数接口已在ReadMe中说明，这里不再赘述，为了实现本题要求，我主要给电梯设置了开关门状态，上升/下降/停止状态以及向上向下两个请求队列。

在Elevator.run()函数中，我们对电梯的运行进行了实现，主要包括：

* 上升(下降)状态时楼层的判定
* 上下客人，开关电梯门的操作
* 电梯走空的判定
* 电梯停止运行的判定及操作
* Thread.sleep()为电梯移动及开关门设置等待时间
* 访问楼层时要检查及设置上升/下降队列锁，防止冲突

具体代码如下

```java
public void run()
    {
        //noinspection InfiniteLoopStatement
        while(true)
        {
            // 下降状态
            while(state == -1)
            {
                boolean OpenDoors = false;
                labelList[1].setText("下降中");

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
                    labelList[currentFloor].setBackground(Color.BLUE);
                    labelList[0].setBackground(Color.YELLOW);
                    labelList[0].setText("开");
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
                    labelList[currentFloor].setBackground(Color.BLUE);
                    labelList[0].setBackground(Color.YELLOW);
                    labelList[0].setText("开");
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

                        labelList[0].setText("关");
                        RunProject.logs.append("电梯" + name + "关\n");
                        labelList[0].setBackground(Color.WHITE);
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
                    labelList[currentFloor].setBackground(Color.RED);
                    labelList[0].setText("关");
                    RunProject.logs.append("电梯" + name + "关\n");
                    labelList[0].setBackground(Color.WHITE);
                }

                // 电梯走空
                if (downStopList.isEmpty() || currentFloor == 0)
                {
                    labelList[currentFloor].setBackground(Color.RED);
                    setState(0);
                    maxUp = 0;
                    minDown = 19;
                    RunProject.logs.append("电梯" + name + "停止\n");
                    break;
                }

                while(labelList[0].getText().equals("开"))
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

                labelList[currentFloor].setBackground(Color.WHITE);
                currentFloor--;
                labelList[currentFloor].setBackground(Color.RED);
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
                labelList[1].setText("上升中");

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
                    labelList[currentFloor].setBackground(Color.BLUE);
                    labelList[0].setBackground(Color.YELLOW);
                    labelList[0].setText("开");
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
                    labelList[currentFloor].setBackground(Color.BLUE);
                    labelList[0].setBackground(Color.YELLOW);
                    labelList[0].setText("开");
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

                        labelList[0].setText("关");
                        RunProject.logs.append("电梯" + name + "关\n");
                        labelList[0].setBackground(Color.WHITE);
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
                    labelList[currentFloor].setBackground(Color.RED);
                    labelList[0].setText("关");
                    RunProject.logs.append("电梯" + name + "关了\n");
                    labelList[0].setBackground(Color.WHITE);
                }

                // 电梯空了
                if (upStopList.isEmpty() || currentFloor == 19)
                {
                    setState(0); //修改该电梯状态为停止
                    maxUp = 0;
                    minDown = 19;
                    labelList[currentFloor].setBackground(Color.RED);
                    RunProject.logs.append("电梯" + name + "停止\n");
                    break;
                }

                while(labelList[0].getText().equals("开"))
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

                labelList[currentFloor].setBackground(Color.WHITE);
                currentFloor++; //上一层红灯亮
                labelList[currentFloor].setBackground(Color.RED);

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
                labelList[1].setText("停止");
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
```



### 电梯调度算法

这里是题目的核心算法，针对本题，可以考虑采用：

* **先来先服务算法（FCFS）**
* **最短寻找楼层时间优先算法（SSTF）**
* **扫描算法（SCAN）**
* **LOOK 算法**
* **SATF 算法**
* ...

这里我选择采用SSTF算法，根据电梯所在楼层`currentFloor`与当前队列请求楼层`i`之间进行差的绝对值运算，从而决定哪一个电梯响应请求，并相应的调整电梯的运行状态。

具体流程如下：

```flow
st=>start: 开始
ed=>end: 结束
op1=>operation: i楼按键
op2=>operation: 寻找距离i楼最近的电梯
op3=>operation: 寻找距离i楼最近的电梯
op4=>operation: 把i楼加入该电梯的上升/下降停止序列
op5=>operation: 启动该电梯，更改其状态，
并将i加入该电梯上升/下降队列
c1=>condition: 有电梯正向i楼方向移动且越过i楼
c2=>condition: 有静止电梯
st->op1->c1
c1(yes)->op2
c1(no)->op3
op2->op4->ed
op3->op5->ed
```

该算法的实现在ElevatorManager类中，具体代码如下：

```java
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
```

