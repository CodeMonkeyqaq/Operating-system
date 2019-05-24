//闭包调用
(function(window) {
	var document = window.document;

	// 获取“开始”按钮
	var startButton = document.getElementById("startButton");

	// 缺页数缺页率对应标签
	var missPageNumberSpan = document.getElementById("missPageNumber");
	var missPageRateSpan = document.getElementById("missPageRate");

	//内存块数,指令总数，每页能存的指令总数
	var blockNumber = 4,
		instructionNumber = 320,
		pageInstructionNumber = 10;
	// 内存
	var memory = [-1, -1, -1, -1];
	// 记录执行的指令个数，缺页数
	var instructionCount = 0,
		missPage = 0;
	//随机数序列
	var randomList = [];
	//从0-319的序列
	var countArray = [];

	//执行指令
	function performOrder(num) {
		randomList.push(countArray[num]);
		countArray.splice(num, 1);
		instructionCount++;
		//console.log(countArray);
	}

	//初始化
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
		performOrder(num);
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
				continue;
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

	function start() {
		// 禁用“开始”按钮
		startButton.disabled = true;

		// 初始化变量
		init();

		$("#memoryTable  tr:not(:first)").remove();

		// 选择算法并开始
		chooseAlgrithm();

		// 启用“开始”按钮
		startButton.disabled = false;
	}

	// 添加开始按钮的监听事件
	startButton.addEventListener('click', start);

})(window)