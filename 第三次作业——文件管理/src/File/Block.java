package File;

//属性、数据及索引
class Block
{
	//属性说明字符串
	String property;
	//数据
	String data;
	//块的索引
	int index;

	//设置属性
	void setProperty(FCB fcb)
	{
		property="file type:"+fcb.fileType+'\n';
		property=property+"file name:"+fcb.absAddress+'\n';
		property=property+"address:"+fcb.fatherAddress+'\n';
		property=property+"block:"+ index +'\n';
	}

	//设置数据
	void setData(String str)
	{
		data=str;
	}

	void setData(ContentPanel content)
	{
		data="";
		for (int i=0; i<content.folderList.size(); i++) data=data+content.folderList.get(i).block.index+'\n';

		data=data+"NULL\n";
		for (int i=0; i<content.fileList.size(); i++) data=data+content.fileList.get(i).block.index+'\n';
	}
}