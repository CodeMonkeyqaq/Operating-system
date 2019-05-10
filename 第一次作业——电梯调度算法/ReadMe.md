# Elevator Management

OS第一次作业：电梯调度问题

作者：1652977——王星洲



## 开发环境

语言：`Java 1.8.0_201`

IDE:    `Intellij IDEA`



## 运行方式

* 双击`ElevatorManagement.jar`预览(推荐)
* 或者在电脑保证安装了java及jdk条件下，在目录文件夹下用命令行工具使用`javac Elevator.java` `javac RunProject.java` `java RunProject`来运行程序
* 使用IDE(如Intellj IDEA)来编译并运行`RunProject.java`
* 为保证icon加载成功，请将jar包与src文件夹置于同一目录下



## 问题背景

​	某一层楼20层，有五部互联的电梯。基于线程思想，编写一个电梯调度程序。本程序主要实现了基本的电梯功能，包括关门开门，上行下行，以及控制灯的亮灭；基本的界面实现，采用 **java.swing** 解决；以及电梯调度算法，采用了 **最短寻找楼层时间优先算法(SSTF)**



## 包含文件

* 可执行jar包文件 `ElevatorManagement.jar`
* java文件 `Elevator.java ` `RunProject.java` 其中main函数在`RunProject`之中
* 图片文件 `superman.jpg` 处于`src`文件夹下
* 两个markdown文件 `ReadMe.md` `Report.md`





## 包含的类

* Elevator_public class extend Thread
* RunProject _public class
* LightManager_static class extend Thread
* ElevatorManager_static class extend Thread





## 主要属性

|               属性名               |        类型         |                         说明                         |
| :--------------------------------: | :-----------------: | :--------------------------------------------------: |
|           Elevator.name            |         int         |                   电梯名(如电梯1)                    |
|           Elevator.state           |         int         |     该电梯当前运动状态 停止：0 上升：1 下降：-1      |
|       Elevator.currentFloor        |         int         |                   电梯当前所在楼层                   |
|           Elevator.maxUp           |         int         |                 电梯要去的最高的楼层                 |
|          Elevator.minDown          |         int         |                 电梯要去的最低的楼层                 |
|        Elevator.upStopList         |   Queue<Integer>    | 电梯上升的停止队列(使用Comparator实现了从低到高排列) |
|       Elevator.downStopList        |   Queue<Integer>    | 电梯下降的停止队列(使用Comparator实现了从高到低排列) |
|         Elevator.labelList         |      JLabel[]       |                    电梯的标签队列                    |
|         RunProject.labels          |      JLabel[]       |                       楼层标签                       |
|      RunProject.ELE1(2/3/4/5)      |      JLabel[]       |                电梯组件对每一层的标签                |
|        RunProject.upQueLock        |      boolean[]      |                      上升序列锁                      |
|       RunProject.downQueLock       |      boolean[]      |                      下降序列锁                      |
|        RunProject.upSelect         |     JComboBox[]     |                      上升选择框                      |
|       RunProject.downSelect        |     JComboBox[]     |                      下降选择框                      |
| RunProject.one/two/three/four/five |      Elevator       |                    电梯1/2/3/4/5                     |
|          RunProject.logs           |      TextArea       |                     右侧输出日志                     |
|          RunProject.ELES           | ArrayList<Elevator> |                       电梯数列                       |
|      RunProject.up(down)Queue      | ArrayList<Integer>  |              对于向上/下的楼层请求数列               |





## 主要功能

| 函数名                     | 参数                         | 返回类型 | 说明                       |
| -------------------------- | ---------------------------- | -------- | -------------------------- |
| Elevator()                 | int name, JLabel[] labelList | /        | Elevator类构造函数         |
| Elevator.GetState()        | /                            | int      | 获取当前电梯state值        |
| Elevator.setState()        | int state                    | /        | 设置当前电梯state值        |
| Elevator.getCurrentFloor() | /                            | int      | 获取当前电梯所在楼层       |
| Elevator.addUp()           | int aim                      | /        | 添加一个向上的目的地       |
| Elevator.addDown()         | int aim                      | /        | 添加一个向下的目的地       |
| Elevator.getMaxUp()        | /                            | int      | 获取电梯要去的最高楼层     |
| Elevator.getMinDown()      | /                            | int      | 获取电梯要去的最低楼层     |
| Elevator.setMaxUp()        | int maxUp                    | /        | 设置电梯要去的最高楼层     |
| Elevator.setMinDown()      | int minDown                  | /        | 设置电梯要去的最低楼层     |
| Elevator.run()             | /                            | /        | 电梯的工作流程函数         |
| RunProject.init()          | /                            | /        | 工程初始化                 |
| LightManager()             | /                            | /        | 灯光控制类的构造函数       |
| LightManager.run()         | /                            | /        | 灯光控制类的实现函数       |
| ElevatorManager()          | /                            | /        | 电梯调度管理类的构造函数   |
| ElevatorManager.adjust()   | int index  int i             | /        | 搜索最佳电梯并使其响应调度 |
| ElevatorManager.run()      | /                            | /        | 电梯调度方法实现           |
| main()                     | String args[]                | /        | 主函数                     |





如有任何使用问题，请联系wxz00328@qq.com 或 tel:18916111729

祝好！