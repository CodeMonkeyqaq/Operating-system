# File Management

本文件是操作系统第三次项目——文件管理系统的使用说明。

作者：1652977——王星洲



## 使用方式

* 请确保计算机内安装了java环境
* 运行 `FileManagement.jar` 。
* 您也可选择自行编译



## 开发环境

语言：`Java`

IDE：`Intellij IDEA`



## 项目结构

```
├── File                        
│   ├── Block.java         // 物理块
│   ├── ContentPanel.java  // 文件夹打开后的面板
│   ├── Disk.java          // 磁盘管理
│   ├── FCB.java           // 文件控制块
│   ├── Folder.java        // 文件夹图标
│   ├── FolderToolBar.java // 工具栏
├── Main.java
```



### FCB类

定义了文件控制块，提供一些函数的接口以及文件属性。



#### 成员变量

| 名称                            | 说明                                   |
| ------------------------------- | -------------------------------------- |
| ContentPanel contentPanel       | 文件夹所拥有的面板（文件没有这个属性） |
| ContentPanel fatherContentPanel | 父文件夹                               |
| String fileType                 | 文件 / 文件夹                          |
| String fatherAddress            | 父文件夹地址                           |
| String absAddress               | 绝对地址                               |



#### 函数

| 名称                | 说明             |
| ------------------- | ---------------- |
| setFatherAddress( ) | 设置父文件夹地址 |
| showProperty( )     | 属性显示框       |
| getProperty( )      | 获取属性         |



### Folder类

继承自FCB类，在fatherContentPanel中显示文件夹图标，并提供双击打开和通过右键进行的一些操作。



#### 函数

| 名称          | 说明         |
| ------------- | ------------ |
| open( )       | 打开文件夹   |
| delete( )     | 删除文件夹   |
| checkName( )  | 判断是否重名 |
| resetName( )  | 重命名       |
| getAddress( ) | 获取绝对地址 |



### FolderToolBar类

工具栏，显示工具栏界面，并提供搜索、后退、输入绝对地址打开文件或文件夹的功能。



### MyFile类

继承自FCB类，在fatherContentPanel中显示记事本图标，提供提供双击打开和通过右键进行的一些操作，提供打开记事本后进行的修改、保存等操作。



### Block类

定义了物理块，可以从真实的电脑上存取数据。块中有一些信息，包括文件或文件夹的一些属性、文件或文件夹的数据、该block在Disk中的序号。



### ContentPanel类

文件夹打开后的面板。



#### 成员变量

| 名称                            | 说明               |
| ------------------------------- | ------------------ |
| Vector folderList               | 该文件夹下的文件夹 |
| Vector fileList                 | 该文件夹下的文件   |
| ContentPanel fatherContentPanel | 父文件夹面板       |



#### 函数

| 名称            | 说明                 |
| --------------- | -------------------- |
| refresh( )      | 刷新                 |
| createFolder( ) | 创建文件夹           |
| createFile( )   | 创建文件             |
| delete( )       | 是否删除             |
| getFolder( )    | 获取选中文件夹的信息 |



### Disk类

对磁盘的位图进行管理，对Block进行申请、释放，对磁盘进行读写，提供双击进入磁盘面板和右键进行格式化等操作。

