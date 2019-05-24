# 动态分区分配方式的模拟

本项目是操作系统第二次大作业第一小问问题的解决方案

本报告为该项目的总结，包含思考过程，算法描述等，如对使用有问题或希望查询函数或属性的说明请移步readme文件

作者：1652977——王星洲



## 问题分析

本题要求采用首次适应算法以及最佳适应算法两种方法来进行内存块的分配和回收。那么我们只需要按部就班的使用方法就可以很好的解决问题。而为了便利的查看两种方法的结果，我使用指令菜单来相应不同的指令，在每一次分配或释放空间后展示当前分区内空间分配的情况。选取基本的命令行应用，采用C++实现。



## 问题解决

### 首次适应算法

该算法从空闲分区链首开始查找，直至找到一个能满足其大小要求的空闲分区为止。然后再按照作业的大小，从该分区中划出一块内存分配给请求者，余下的空闲分区仍留在空闲分区链中。实现思路如下:

#### 流程

```flow
st=>start: 开始
ed=>end: 结束
op1=>operation: 找到第一个分区
op2=>operation: 查看分区
op3=>operation: 将作业分配在该分区
op4=>operation: 寻找下一个分区
op5=>operation: 将作业分配在该分区
op6=>operation: 将剩余空闲空间设置为新分区
op7=>operation: 分配成功
op8=>operation: 分配失败
c1=>condition: 该分区是否空闲
c2=>condition: 该分区大小是否等于作业大小
c3=>condition: 该分区大小是否大于作业大小
c4=>condition: 存在分区
st->op1->op2->c1(no, bottom)->op4->c4
c4(no,down)->op8->ed
c1(yes, right)->c2(no, right)->c3(no, left)->op4
c2(yes)->op3->op7->ed
c3(yes, bottom)->op5->op6->op7(left)
c4(yes)->op2
```

#### 具体代码：

````c++
bool first_fit(int ID, int size)
{
	partition* newPartition = first->next;
	while(newPartition)
	{
		if (newPartition->size == size && newPartition->flag == FREE)
		{
			newPartition->flag = BUSY;
			newPartition->ID = ID;
			return true;
			break;
		}
		else if (newPartition->flag == FREE && newPartition->size > size)
		{
			newPartition->flag = BUSY;
			newPartition->ID = ID;
			partition *newNewPartition = new partition(newPartition->size - size, FREE, newPartition->address + size, FREE);
			newPartition->size = size;
			newNewPartition->next = newPartition->next;
			newPartition->next = newNewPartition;
			newNewPartition->front = newPartition;
			return true;
			break;
		}
		newPartition = newPartition->next;
	}
	return false;
}
````



### 最佳适应算法
该算法总是把既能满足要求，又是最小的空闲分区分配给作业。我采用了一个记录分区与作业大小差距的变量 `difference` 来记录大小，每次找到符合要求的分区时，比较其差值与difference的大小，最后选出最佳的分区，并进行分配。实现思路如下：

#### 流程

```flow
st=>start: 开始
ed=>end: 结束
op0=>operation: 初始化difference
op1=>operation: 找到第一个分区
op2=>operation: 查看分区
op3=>operation: 将作业分配在该分区
op4=>operation: 寻找下一个分区
op5=>operation: 将作业分配在该分区
op6=>operation: 将剩余空闲空间设置为新分区
op7=>operation: 分配成功
op8=>operation: 分配失败
op9=>operation: 计算作业与分区大小差距temp
op10=>operation: 更新difference为temp的值
op11=>operation: 记录分区位置
op12=>operation: 查看记录的分区位置
c1=>condition: 该分区是否空闲
c2=>condition: 该分区大小是否等于作业大小
c3=>condition: 该分区大小是否大于作业大小
c4=>condition: 存在分区
c5=>condition: temp<difference
c6=>condition: 记录的分区位置为空
st->op0->op1->op2
op2(right)->c1(no)->op4->c4
c4(no,down)->op12->c6(yes)->op8->ed
c6(no)->op5->op6->op7->ed
c1(yes)->c2(no,down)->c3(no)->op4
c2(yes,right)->op3->op7(left)->ed
c3(yes,down)->op9->c5(no,down)->op4
c5(yes,down)->op10->op11(right)->op4
c4(yes)->op2
```

#### 具体代码：

```C++
bool best_fit(int ID, int size)
{
	//记录可用内存与需求内存的差值
	int difference;
	partition *newPartition = first->next;
	//记录最佳位置
	partition* q = NULL;
	//找到第一个q
	while (newPartition)
	{
		if (newPartition->flag == FREE && newPartition->size >= size)
		{
			//最佳
			if (newPartition->size == size)
			{
				newPartition->flag = BUSY;
				newPartition->ID = ID;
				return true;
				break;
			}
			q = newPartition;
			difference = newPartition->size - size;
			break;
		}
		newPartition = newPartition->next;
	}
	//继续遍历
	while (newPartition)
	{
		//最佳
		if (newPartition->flag == FREE && newPartition->size == size)
		{
			newPartition->flag = BUSY;
			newPartition->ID = ID;
			return true;
			break;
		}
		//另一个可行位置
		if (newPartition->flag == FREE && newPartition->size > size)
		{
			//位置更合适
			if (difference > newPartition->size - size)
			{
				difference = newPartition->size - size;
				q = newPartition;
			}
		}
		newPartition = newPartition->next;
	}
	//如果没有找到位置
	if (q == NULL)
	{
		return false;
	}
	//找到了最佳位置
	else
	{
		q->flag = BUSY;
		q->ID = ID;
		partition* newNewPartition = new partition(q->size - size, FREE, q->address + size, FREE);
		q->size = size;
		newNewPartition->next = q->next;
		q->next = newNewPartition;
		newNewPartition->front = q;
		return true;
	}
}
```



### 内存释放

释放作业时，首先要找到作业对应的分区，并把该分区释放，而且要考虑该分区的前后分区，如果前面存在空闲分区则向前合并，若后面存在空闲分区则向后合并，若都空闲，则一起合并。



#### 流程

```flow
st=>start: 开始
ed=>end: 结束
op1=>operation: 找到作业对应分区
op2=>operation: 将该分区置为空闲
c1=>condition: 前方为空闲分区，后方为已分配分区
c2=>condition: 后方为空闲分区，前方为已分配分区
c3=>condition: 前后均为空闲分区
op3=>operation: 与前方空闲分区合并
op4=>operation: 与后方空闲分区合并
op5=>operation: 与前后方空闲分区合并
op6=>operation: 释放成功
st->op1->op2->c1
c1(yes)->op3->op6(left)->ed
c1(no,down)->c2(no,down)->c3(no,down)->ed
c2(yes)->op4->op6
c3(yes)->op5->op6
```

#### 具体代码：

```C++
bool free(int ID)
{
	partition* newPartition = first->next;
	while (newPartition)
	{
		//找到要回收的ID区域
		if (newPartition->ID == ID)
		{
			newPartition->flag = FREE;
			newPartition->ID = FREE;
			//前为空闲，一起合并
			if (newPartition->front->flag == FREE && newPartition->next->flag != FREE)
			{
				newPartition->front->size += newPartition->size;
				newPartition->front->next = newPartition->next;
				newPartition->next->front = newPartition->front;
			}
			//后为空闲，一起合并
			else if (newPartition->front->flag != FREE && newPartition->next->flag == FREE)
			{
				newPartition->size += newPartition->next->size;
				if (newPartition->next->next)
				{
					newPartition->next->next->front = newPartition;
					newPartition->next = newPartition->next->next;
				}
				else { 
					newPartition->next = newPartition->next->next; 
				}
			}
			//前后都为空闲，一起合并
			else if (newPartition->front->flag == FREE && newPartition->next->flag == FREE)
			{
				newPartition->front->size += newPartition->size + newPartition->next->size;
				if (newPartition->next->next)
				{
					newPartition->next->next->front = newPartition->front;
					newPartition->front->next = newPartition->next->next;
				}
				else {
					newPartition->front->next = newPartition->next->next;
				}
			}
			break;
		}
		newPartition = newPartition->next;
	}
	cout << "指令" << ID << "回收成功！" << endl;
	show();
	return true;
}
```



## 程序运行截屏

### 首次适应算法

![1558518224219](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1558518224219.png)

![1558518231634](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1558518231634.png)

### 最佳适应算法

![1558518238725](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1558518238725.png)

![1558518243947](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1558518243947.png)