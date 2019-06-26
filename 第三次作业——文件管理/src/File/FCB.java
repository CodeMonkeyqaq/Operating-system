package File;

import java.awt.*;
import javax.swing.*;

//FCB
public abstract class FCB
{
	JPanel viewPanel=new JPanel();
	JTextField nameField=new JTextField();
	ImageIcon viewImg;

	ContentPanel contentPanel;			//文件夹所拥有的面板  文件没有这个属性
	ContentPanel fatherContentPanel;	//父文件夹
	String fileType;						//文件/文件夹
	String fatherAddress;				//父文件夹地址
	String absAddress;						//绝对地址
	Block block;


	public abstract	boolean delete(boolean isRootPanel);
	public abstract void open();

	void setFatherAddress(String str)
	{
		fatherAddress=str;
	}

	void showProperty()	//属性显示
	{
		JFrame propertyFrame = new JFrame();
		JFrame.setDefaultLookAndFeelDecorated(true);
		propertyFrame.setTitle(absAddress+"attribute");
		propertyFrame.setSize(300, 450);
		propertyFrame.setResizable(false);
		propertyFrame.setVisible(true);
		propertyFrame.setLocationRelativeTo(Disk.mainFrame);
		propertyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel=new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(null);
		propertyFrame.add(panel);

		JLabel viewLabel=new JLabel(viewImg);
		viewLabel.setBounds(10, 10, 70, 70);
		panel.add(viewLabel);

		JLabel nameLabel=new JLabel(absAddress,JLabel.LEFT);
		nameLabel.setBounds(100, 60, 200, 20);
		panel.add(nameLabel);


		JLabel type=new JLabel("type:",JLabel.LEFT);
		type.setBounds(20, 90, 50, 20);
		panel.add(type);

		JLabel typeLabel;
		typeLabel=new JLabel(fileType,JLabel.LEFT);
		typeLabel.setBounds(100, 90, 200, 20);
		panel.add(typeLabel);

		JLabel address=new JLabel("address:",JLabel.LEFT);
		address.setBounds(20, 130, 50, 20);
		panel.add(address);

		JLabel addressLabel=new JLabel(fatherAddress,JLabel.LEFT);
		addressLabel.setBounds(100, 130, 200, 20);
		panel.add(addressLabel);

		JLabel blockNum=new JLabel("block:",JLabel.LEFT);
		blockNum.setBounds(20, 170, 50, 20);
		panel.add(blockNum);

		JLabel fileBlock=new JLabel(Integer.toString(block.index),JLabel.LEFT);
		fileBlock.setBounds(100, 170, 200, 20);
		panel.add(fileBlock);

		if (fileType.equals("folder"))
		{
			JLabel include=new JLabel("include:",JLabel.LEFT);
			include.setBounds(20, 210, 50, 20);
			panel.add(include);

			JLabel includeFile=new JLabel("folder:"+contentPanel.folderList.size()+"  file:"+contentPanel.fileList.size(),JLabel.LEFT);
			includeFile.setBounds(100,210,200,20);
			panel.add(includeFile);
		}
		else
		{
			JLabel size=new JLabel("size:",JLabel.LEFT);
			size.setBounds(20, 210, 50, 20);
			panel.add(size);

			int index;
			for (index=0; index<fatherContentPanel.fileList.size(); index++)
				if (fatherContentPanel.fileList.get(index)==this) break;
			JLabel fileSize=new JLabel(Integer.toString(fatherContentPanel.fileList.get(index).text.length()),JLabel.LEFT);
			fileSize.setBounds(100, 210, 200, 20);
			panel.add(fileSize);
		}
	}

	void getProperty(Block block)
	{
		this.block=block;

		String str=block.property;
		int begin;
		int end;

		begin=str.indexOf("file name:");
		str=str.substring(begin+10);
		end=str.indexOf('\n');
		absAddress=str.substring(0, end);
		str=str.substring(end+1);

		begin=str.indexOf("address:");
		str=str.substring(begin+8);
		end=str.indexOf('\n');
		fatherAddress=str.substring(0,end);
	}

}
