#include <list>
#include <iostream>
using namespace std;

const bool FREE = false;
const bool BUSY = true;
const int MAX_LENGTH = 640;

//分区结构体
struct partition
{
	//状态
	bool flag;
	//大小
	int size;
	//分区装入的结点号
	int ID;
	//分区起始地址
	int address;
	partition* front;
	partition* next;
	partition() { flag = FREE; size = 0; ID = 0; address = 0; front = NULL; next = NULL; }
	partition(int insize, int inID, int inaddress, int inflag) { flag = inflag; size = insize; ID = inID; address = inaddress; front = NULL; next = NULL; }
};

//分配的结点结构体
struct node
{
	//结点号
	int ID;
	//大小
	int size;
	node(int inID, int insize) {ID = inID; size = insize;}
};

partition *first;
partition *last;
//内存分配
void alloc(int, int, int);
//内存回收
bool free(int);
//首次适应算法
bool first_fit(int, int);
//最佳适应算法
bool best_fit(int, int);
//查看分配
void show();
//总流程
void flow(int);
//初始化
void init();
//菜单
void menu();

//初始化分区链表
void init()
{
	first = new partition();
	last = new partition(MAX_LENGTH, FREE, 0, FREE);
	first->flag = BUSY;
	first->next = last;
	last->front = first;
}

//申请内存
void alloc(int method, int ID, int size)
{
	if (method == 1)//采用首次适应算法
	{
		if (first_fit(ID, size))
		{
			cout << "指令" << ID << "分配成功！" << endl;
			show();
		}
		else
		{
			cout  << "指令" << ID << "分配失败！" << endl;
			show();
		}
		return;
	}
	else
	{
		if (best_fit(ID, size))
		{
			cout << "指令" << ID << "分配成功！" << endl;
			show();
		}
		else
		{
			cout << "指令" << ID << "分配失败！" << endl;
			show();
		}
		return;
	}
}

//首次适应算法
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

//最佳适应算法
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

//主存回收
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

void flow(int method)
{
	cout << "作业1申请130K" << endl;
	alloc(method, 1, 130);
	cout << "作业2申请60K" << endl;
	alloc(method, 2, 60);
	cout << "作业3申请100k" << endl;
	alloc(method, 3, 100);
	cout << "作业2释放60K" << endl;
	free(2);
	cout << "作业4申请200K" << endl;
	alloc(method, 4, 200);
	cout << "作业3释放100K" << endl;
	free(3);
	cout << "作业1释放130K" << endl;
	free(1);
	cout << "作业5申请140K" << endl;
	alloc(method, 5, 140);
	cout << "作业6申请60K" << endl;
	alloc(method, 6, 60);
	cout << "作业7申请50K" << endl;
	alloc(method, 7, 50);
	cout << "作业6释放60K" << endl;
	free(6);
	return;
}

//显示内存分配情况
void show()
{
	cout << "--------------------------------------------" << endl;
	cout << "内存分配情况如下：" << endl;
	partition* newPartition = first->next;
	while (newPartition)
	{
		cout << "分区号：";
		if (newPartition->ID == FREE)
			cout << "FREE\n";
		else
			cout << newPartition->ID << '\n';
		cout << "起始地址：" << newPartition->address << endl;
		cout << "内存大小：" << newPartition->size << endl;
		cout << "分区状态：";
		if (newPartition->flag == FREE)
			cout << "空闲" << endl;
		else
			cout << "已分配" << endl;
		newPartition = newPartition->next;
	}
	cout << "--------------------------------------------" << endl;
	return;
}

//菜单
void menu()
{
	int tag = 0;
	cout << "动态分区分配方式模拟：" << endl;
	while (tag != 3)
	{
		cout << "请输入要采用的算法或退出：" << endl;
		cout << "1-首次适应算法，2-最佳适应算法，3-退出" << endl;
		cin >> tag;
		init();
		auto p = first->next;
		switch (tag)
		{
		case 1:
			while (p)
			{
				auto temp = p;
				p = p->next;
				delete(temp);
			}
			init();
			flow(tag);
			break;
		case 2:
			while (p)
			{
				auto temp = p;
				p = p->next;
				delete(temp);
			}
			init();
			flow(tag);
			break;
		case 3:
			break;
		default:
			cout << "您输入的指令有误，请重新输入。" << endl;
			break;
		}
	}
	return;
}

int main()
{
	menu();
	delete first;
	delete last;
	return 0;
}
