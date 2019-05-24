#include <list>
#include <iostream>
using namespace std;

const bool FREE = false;
const bool BUSY = true;
const int MAX_LENGTH = 640;

//�����ṹ��
struct partition
{
	//״̬
	bool flag;
	//��С
	int size;
	//����װ��Ľ���
	int ID;
	//������ʼ��ַ
	int address;
	partition* front;
	partition* next;
	partition() { flag = FREE; size = 0; ID = 0; address = 0; front = NULL; next = NULL; }
	partition(int insize, int inID, int inaddress, int inflag) { flag = inflag; size = insize; ID = inID; address = inaddress; front = NULL; next = NULL; }
};

//����Ľ��ṹ��
struct node
{
	//����
	int ID;
	//��С
	int size;
	node(int inID, int insize) {ID = inID; size = insize;}
};

partition *first;
partition *last;
//�ڴ����
void alloc(int, int, int);
//�ڴ����
bool free(int);
//�״���Ӧ�㷨
bool first_fit(int, int);
//�����Ӧ�㷨
bool best_fit(int, int);
//�鿴����
void show();
//������
void flow(int);
//��ʼ��
void init();
//�˵�
void menu();

//��ʼ����������
void init()
{
	first = new partition();
	last = new partition(MAX_LENGTH, FREE, 0, FREE);
	first->flag = BUSY;
	first->next = last;
	last->front = first;
}

//�����ڴ�
void alloc(int method, int ID, int size)
{
	if (method == 1)//�����״���Ӧ�㷨
	{
		if (first_fit(ID, size))
		{
			cout << "ָ��" << ID << "����ɹ���" << endl;
			show();
		}
		else
		{
			cout  << "ָ��" << ID << "����ʧ�ܣ�" << endl;
			show();
		}
		return;
	}
	else
	{
		if (best_fit(ID, size))
		{
			cout << "ָ��" << ID << "����ɹ���" << endl;
			show();
		}
		else
		{
			cout << "ָ��" << ID << "����ʧ�ܣ�" << endl;
			show();
		}
		return;
	}
}

//�״���Ӧ�㷨
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

//�����Ӧ�㷨
bool best_fit(int ID, int size)
{
	//��¼�����ڴ��������ڴ�Ĳ�ֵ
	int difference;
	partition *newPartition = first->next;
	//��¼���λ��
	partition* q = NULL;
	//�ҵ���һ��q
	while (newPartition)
	{
		if (newPartition->flag == FREE && newPartition->size >= size)
		{
			//���
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
	//��������
	while (newPartition)
	{
		//���
		if (newPartition->flag == FREE && newPartition->size == size)
		{
			newPartition->flag = BUSY;
			newPartition->ID = ID;
			return true;
			break;
		}
		//��һ������λ��
		if (newPartition->flag == FREE && newPartition->size > size)
		{
			//λ�ø�����
			if (difference > newPartition->size - size)
			{
				difference = newPartition->size - size;
				q = newPartition;
			}
		}
		newPartition = newPartition->next;
	}
	//���û���ҵ�λ��
	if (q == NULL)
	{
		return false;
	}
	//�ҵ������λ��
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

//�������
bool free(int ID)
{
	partition* newPartition = first->next;
	while (newPartition)
	{
		//�ҵ�Ҫ���յ�ID����
		if (newPartition->ID == ID)
		{
			newPartition->flag = FREE;
			newPartition->ID = FREE;
			//ǰΪ���У�һ��ϲ�
			if (newPartition->front->flag == FREE && newPartition->next->flag != FREE)
			{
				newPartition->front->size += newPartition->size;
				newPartition->front->next = newPartition->next;
				newPartition->next->front = newPartition->front;
			}
			//��Ϊ���У�һ��ϲ�
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
			//ǰ��Ϊ���У�һ��ϲ�
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
	cout << "ָ��" << ID << "���ճɹ���" << endl;
	show();
	return true;
}

void flow(int method)
{
	cout << "��ҵ1����130K" << endl;
	alloc(method, 1, 130);
	cout << "��ҵ2����60K" << endl;
	alloc(method, 2, 60);
	cout << "��ҵ3����100k" << endl;
	alloc(method, 3, 100);
	cout << "��ҵ2�ͷ�60K" << endl;
	free(2);
	cout << "��ҵ4����200K" << endl;
	alloc(method, 4, 200);
	cout << "��ҵ3�ͷ�100K" << endl;
	free(3);
	cout << "��ҵ1�ͷ�130K" << endl;
	free(1);
	cout << "��ҵ5����140K" << endl;
	alloc(method, 5, 140);
	cout << "��ҵ6����60K" << endl;
	alloc(method, 6, 60);
	cout << "��ҵ7����50K" << endl;
	alloc(method, 7, 50);
	cout << "��ҵ6�ͷ�60K" << endl;
	free(6);
	return;
}

//��ʾ�ڴ�������
void show()
{
	cout << "--------------------------------------------" << endl;
	cout << "�ڴ����������£�" << endl;
	partition* newPartition = first->next;
	while (newPartition)
	{
		cout << "�����ţ�";
		if (newPartition->ID == FREE)
			cout << "FREE\n";
		else
			cout << newPartition->ID << '\n';
		cout << "��ʼ��ַ��" << newPartition->address << endl;
		cout << "�ڴ��С��" << newPartition->size << endl;
		cout << "����״̬��";
		if (newPartition->flag == FREE)
			cout << "����" << endl;
		else
			cout << "�ѷ���" << endl;
		newPartition = newPartition->next;
	}
	cout << "--------------------------------------------" << endl;
	return;
}

//�˵�
void menu()
{
	int tag = 0;
	cout << "��̬�������䷽ʽģ�⣺" << endl;
	while (tag != 3)
	{
		cout << "������Ҫ���õ��㷨���˳���" << endl;
		cout << "1-�״���Ӧ�㷨��2-�����Ӧ�㷨��3-�˳�" << endl;
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
			cout << "�������ָ���������������롣" << endl;
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
