# 请求调页存储管理方式模拟

本项目是操作系统第二次大作业第二小问问题的解决方案

本报告为该项目的总结，包含思考过程，算法描述等，如对使用有问题或希望查询函数或属性的说明请移步readme文件

作者：1652977——王星洲



## 问题分析

本题要求采用FIFO以及LRU两种方法来进行页面调用。两种算法的实现并不困难，稍微有难度的是将指令按照题目要求给出执行顺序，这里我采用了类似题目背景提供参考的排列方式，有些许改动，保证了基本满足题目要求的情况下缩短了排列所需时间。



## 问题解决

### 指令执行顺序的确定

为指令执行的顺序，我采用了两个数组，一个装入0-319的顺序数组，一个用来储存结果的数组。接下来按照题中的推荐方式，先找数组中随机位置，把值给结果数组，在顺序数组中删除该值，再看下一位，再向前随机，下一位，再向后随机，下一位，反复执行。与题目中不同的是，如果我执行过了[2,3,4,5]指令，当前执行了1指令，那么下一位将会是6，并且成功执行。



#### 流程

1. `countArray[0.1.2.3.4.---319]` `randomList[]`

2. randomNumber <= 143
3. `countArray[0.1.2.3.4.---319](无143)` `randomList[143]`
4. `countArray[0.1.2.3.4.---319](无143 144)` `randomList[143,144]`
5. randomNumber <= 142(在0-142之间)
6. `countArray[0.1.2.3.4.---319](无142 143 144)` `randomList[143,144,142]`
7. `countArray[0.1.2.3.4.---319](无142 143 144 145)` `randomList[143,144,142,145]` (这里本该调用143，然而143，144已经被调用，142后一位是145，所以调用了145)
8. randomNumber <= 319 (在146-319之间)
9. `countArray[0.1.2.3.4.---318](无142 143 144 145)` `randomList[143,144,142,145,319]`
10. 这里本该调用320，然而不存在320指令，所以从(0-318)之间随机数值进行调用
11. 以此类推



#### 具体代码：

```javascript
function init() {
		missPage = 0;
		randomList = []
		memory = [-1, -1, -1, -1]
		countArray = new Array(instructionNumber);
		for(var i = 0; i < countArray.length; i++) {
			countArray[i] = i;
		}
		instructionCount = 0;
		var num = Math.floor(Math.random() * instructionNumber);
		//console.log(num)
		performOrder(num);//这里会将instructionCount+1
		while(instructionCount < instructionNumber) {
			//+1位
			//如果num在范围内
			//console.log(num)
			if(num < countArray.length) {
				performOrder(num);
			}
			if(instructionCount >= instructionNumber) {
				break;
			}
			//向前
			num--;
			num = Math.floor(Math.random() * (num + 1));
			//console.log(num)
			if(num < countArray.length) {
				performOrder(num);
			}
			if(instructionCount >= instructionNumber) {
				break;
			}
			//+1位
			if(num < countArray.length) {
				performOrder(num);
			} else {
				continue;//防止越界
			}
			if(instructionCount >= instructionNumber) {
				break;
			}
			//向后
			num = Math.floor(Math.random() * (countArray.length - num) + num);
			//console.log(num)
			if(num < countArray.length) {
				performOrder(num);
			}
		}
		//randomList.forEach(function(el){
		//	console.log(el);
		//});
		//console.log(randomList.length)
	};
```



### 先入先出(FIFO)算法

如果一个数据最先进入，则应该最早淘汰掉。也就是说，当内存块满的时候，应当把最先进入的页给淘汰掉。这种方法简单易实现。

#### 流程

```flow
st=>start: 开始
ed=>end: 结束
op0=>operation: 初始化内存块为空
op1=>operation: 读取指令，并获得该指令对应的页
op2=>operation: 向下取指
op3=>operation: 将页加入内存块
op4=>operation: 把内存块中最早分配的页移除
op5=>operation: 将页加入内存块
c1=>condition: 查看当前内存块中是否有该指令的页
c2=>condition: 内存块已满
c3=>condition: 指令取尽
st->op0->op1->c1(yes,right)->op2->c3(yes)->ed
c1(no)->c2(yes,down)->op4->op5->op2
c2(no,down)->op3->op2
c3(no)->op1
```

#### 具体代码：

```javascript
//先进先出算法
	function FIFO() {
		randomList = randomList.reverse()
		while(randomList.length != 0) {
			var order = randomList[randomList.length - 1];
			randomList.pop();
			var block = Math.floor(order / pageInstructionNumber);
			if(memory.indexOf(block) === -1) {
				memory.shift();
				memory[blockNumber - 1] = block;
				//输出消息
				//console.log(block+"不在memory中,加入块");
				var row = document.getElementById("memoryTable").insertRow();
				row.style = "color:red;"
				row.insertCell(0).innerHTML = order;
				row.insertCell(1).innerHTML = memory[0] == -1 ? "Empty" : memory[0];
				row.insertCell(2).innerHTML = memory[1] == -1 ? "Empty" : memory[1];
				row.insertCell(3).innerHTML = memory[2] == -1 ? "Empty" : memory[2];
				row.insertCell(4).innerHTML = memory[3] == -1 ? "Empty" : memory[3];
				// 不在内存中 替换
				row.insertCell(5).innerHTML = "指令" + order + "在" + block + "页中，发生缺页，将内存块中的" + (memory[0] == -1 ? "Empty" : memory[0]) + "页替换为" + block + "页。";
				missPage++;
			} else {
				//输出消息
				//console.log(block+"在memory中");
				var row = document.getElementById("memoryTable").insertRow();
				row.insertCell(0).innerHTML = order;
				row.insertCell(1).innerHTML = memory[0] == -1 ? "Empty" : memory[0];
				row.insertCell(2).innerHTML = memory[1] == -1 ? "Empty" : memory[1];
				row.insertCell(3).innerHTML = memory[2] == -1 ? "Empty" : memory[2];
				row.insertCell(4).innerHTML = memory[3] == -1 ? "Empty" : memory[3];
				// 在内存中 输出相应信息
				row.insertCell(5).innerHTML = "指令" + order + "在" + block + "页中，该页已在内存块中。";
			}
		}
		console.log(missPage);
		missPageNumberSpan.textContent = missPage;
		missPageRateSpan.textContent = ((missPage / instructionNumber) * 100).toFixed(2);
	};
```



### 最近最久未使用(LRU)算法

**如果一个数据在最近一段时间没有被访问到，那么在将来它被访问的可能性也很小**。也就是说，当限定的空间已存满数据时，应当把最久没有被访问到的数据淘汰。我采用一个数组解决，将最近被调用的页放到数组尾，最近未被调用的放到数组头，每次删去数组头的数据，在数组尾加入新页面即可。



#### 流程

```flow
st=>start: 开始
ed=>end: 结束
op0=>operation: 初始化内存块为空
op1=>operation: 读取指令，并获得该指令对应的页
op2=>operation: 向下取指
op3=>operation: 将页加入内存块尾端
op4=>operation: 把内存块中最顶端的页移除
op5=>operation: 将页加入内存块尾端
op6=>operation: 将对应页面置于尾端，之后的页面依次前移
c1=>condition: 查看当前内存块中是否有该指令的页
c2=>condition: 内存块已满
c3=>condition: 指令取尽
st->op0->op1->c1(yes,right)->op6->op2->c3(yes)->ed
c1(no)->c2(yes,down)->op4->op5->op2
c2(no,down)->op3->op2
c3(no)->op1
```

#### 具体代码：

```javascript
function LRU() {
		randomList = randomList.reverse()
		while(randomList.length != 0) {
			var order = randomList[randomList.length - 1];
			randomList.pop();
			var block = Math.floor(order / pageInstructionNumber);
			var sequance = memory.indexOf(block)
			if(sequance === -1) {
				memory.shift();
				memory[blockNumber - 1] = block;
				//输出消息
				//console.log(`${block}不在memory中,加入块`);
				var row = document.getElementById("memoryTable").insertRow();
				row.style = "color:red;"
				row.insertCell(0).innerHTML = order;
				row.insertCell(1).innerHTML = memory[0] == -1 ? "Empty" : memory[0];
				row.insertCell(2).innerHTML = memory[1] == -1 ? "Empty" : memory[1];
				row.insertCell(3).innerHTML = memory[2] == -1 ? "Empty" : memory[2];
				row.insertCell(4).innerHTML = memory[3] == -1 ? "Empty" : memory[3];
				// 不在内存中 替换
				row.insertCell(5).innerHTML = "指令" + order + "在" + block + "页中，发生缺页，将内存块中的" + (memory[0] == -1 ? "Empty" : memory[0]) + "页替换为" + block + "页。";
				missPage++;
			} else {
				//输出消息
				//console.log(`${block}在memory中.`);
				memory.splice(sequance, 1);
				memory[blockNumber - 1] = block;
				var row = document.getElementById("memoryTable").insertRow();
				row.insertCell(0).innerHTML = order;
				row.insertCell(1).innerHTML = memory[0] == -1 ? "Empty" : memory[0];
				row.insertCell(2).innerHTML = memory[1] == -1 ? "Empty" : memory[1];
				row.insertCell(3).innerHTML = memory[2] == -1 ? "Empty" : memory[2];
				row.insertCell(4).innerHTML = memory[3] == -1 ? "Empty" : memory[3];
				// 在内存中 输出相应信息
				row.insertCell(5).innerHTML = "指令" + order + "在页" + block + "中，该页已在内存块中，将该页优先级提高，其他页面优先级降低。";
			}
		}
		missPageNumberSpan.textContent = missPage;
		missPageRateSpan.textContent = ((missPage / instructionNumber) * 100).toFixed(2);
	};

	function chooseAlgrithm() {
		var ratio = document.querySelector("input:checked");
		if(ratio.value === "FIFO") {
			FIFO();
		} else if(ratio.value === "LRU") {
			LRU();
		}
	};
```



