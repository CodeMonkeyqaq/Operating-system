package File;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//文件夹类
public class Folder extends FCB
{
	FolderViewPanel  folderView=new FolderViewPanel();

	class FolderViewPanel extends JPanel
	{
		void setViewPanel()
		{
			viewPanel.setBackground(Color.white);
			viewPanel.setBounds(15, 5, 70, 70);
			viewPanel.addMouseListener(viewMouseListener);
            viewImg = new ImageIcon(Folder.class.getResource("/img/folder.png"));
			JLabel imgLabel = new JLabel(viewImg);
			viewPanel.add(imgLabel);
			add(viewPanel);
		}

		void setNamePanel()
		{
			nameField.setHorizontalAlignment(JTextField.CENTER);
			nameField.setEditable(false);
			nameField.setBackground(Color.white);
			nameField.setBounds(10, 80, 80, 20);
			add(nameField);
		}

		FolderViewPanel()
		{
			setBackground(Color.white);
			setLayout(null);
			setPreferredSize(new Dimension(100,100));
			setViewPanel();
			setNamePanel();
		}

		MouseListener viewMouseListener=new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2)
				{
					viewPanel.setBackground(Color.white);
					open();
				}
				else if (e.getButton()==MouseEvent.BUTTON3)
				{
					JPopupMenu menu=new JPopupMenu();
					JMenuItem openMenu=new JMenuItem("open");
					openMenu.addActionListener(openMenuListener);
					menu.add(openMenu);

					JMenuItem resetNameMenu=new JMenuItem("rename");
					resetNameMenu.addActionListener(resetNameMenuListener);
					menu.add(resetNameMenu);

					JMenuItem deleteMenu=new JMenuItem("delete");
					deleteMenu.addActionListener(deleteMenuListener);
					menu.add(deleteMenu);

					JMenuItem propertyMenu=new JMenuItem("attribute");
					propertyMenu.addActionListener(propertyMenuListener);
					menu.add(propertyMenu);

					menu.show(e.getComponent(),e.getX(),e.getY());
				}
			}

			public void mouseEntered(MouseEvent arg0)
			{
				viewPanel.setBackground(Color.gray);
			}

			public void mouseExited(MouseEvent arg0)
			{
				viewPanel.setBackground(Color.white);
			}

			public void mousePressed(MouseEvent arg0)
			{
			}

			public void mouseReleased(MouseEvent e)
			{
			}

			ActionListener openMenuListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					open();
				}
			};
			ActionListener resetNameMenuListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					resetName();
				}
			};

			ActionListener deleteMenuListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					delete(true);
				}
			};

			ActionListener propertyMenuListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					showProperty();
				}
			};
		};
	}

	Folder(ContentPanel father)
	{
		fatherContentPanel=father;
		create();
		contentPanel=new ContentPanel(fatherContentPanel);
	}



	Folder(Block block, ContentPanel father)
	{
		fatherContentPanel=father;
		father.folderList.add(this);
		contentPanel=new ContentPanel(fatherContentPanel);
		getProperty(block);
		nameField.setText(absAddress);
		fileType="folder";

		if (block.data==null) return ;
		String str=block.data;
		while (true)		//文件夹
		{
			int index=str.indexOf('\n');
			if (str.substring(0, index).equals("NULL"))
			{
				str=str.substring(index+1);
				break;
			}
			int blockIndex= Integer.parseInt(str.substring(0, index));
			new Folder(Disk.block[blockIndex],this.contentPanel);
			str=str.substring(index+1);
		}
		while (!str.isEmpty())
		{
			int index=str.indexOf('\n');
			int blockIndex= Integer.parseInt(str.substring(0, index));
			new MyFile(Disk.block[blockIndex],this.contentPanel);
			str=str.substring(index+1);
		}

	}

	//打开文件夹
	public void open()
	{
		block.setProperty(this);
		ContentPanel.switchPanel(contentPanel);
	}

	///删除文件夹
	public boolean delete(boolean isRootPanel)
	{
		if (isRootPanel)
		{
			int option = JOptionPane.showConfirmDialog(null, "delete file?","delete file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
			if (option==JOptionPane.NO_OPTION) return false;
		}
		boolean isDelete=true;

		for (int i=0; i<contentPanel.fileList.size(); i++)
		{
			if (!contentPanel.fileList.get(i).delete(false)) isDelete=false;
			else i--;
		}
		for (int i=0; i<contentPanel.folderList.size(); i++)
		{
			if (!contentPanel.folderList.get(i).delete(false)) isDelete=false;
			else i--;
		}

		if (isDelete)
		{
			Disk.fat.deleteBlock(this);
			contentPanel.fatherContentPanel.folderList.remove(this);

			Block fatherBlock;
			if (fatherContentPanel.getFolder()==null) fatherBlock=Disk.block[0];
			else fatherBlock=fatherContentPanel.getFolder().block;
			fatherBlock.setData(fatherContentPanel);

			if (isRootPanel) fatherContentPanel.refresh();
		}

		return isDelete;
	}

	//是否重名
	private boolean checkName(String str)
	{
		for (int i=0; i<fatherContentPanel.folderList.size(); i++)
		{
			if (fatherContentPanel.folderList.get(i)!=this && fatherContentPanel.folderList.get(i).absAddress.equals(str))	return false;
		}
		return true;
	}

	//检查重命名后的命名是否规范，若不规范则自动修改
	private void resetName(boolean isAutomic)	//isAutomic: 是否为自动取名
	{

		String newName=nameField.getText();
		while (!newName.isEmpty() && newName.charAt(newName.length()-1)==' ') newName=newName.substring(0, newName.length()-1);
		while (!newName.isEmpty() && newName.charAt(0)==' ') newName=newName.substring(1);

		if (newName.length()>10) newName=newName.substring(0,10);

		if (newName.length()==0)
		{
			nameField.setText(absAddress);
			return ;
		}

		if (!checkName(newName))
		{
			int i=0;
			while ( !checkName(newName+"("+ ++i +')') );
			newName=newName+"("+ i +')';
		}
		absAddress=newName;
		nameField.setText(absAddress);
		if (!isAutomic)
		{
			block.setProperty(this);
		}

		if (contentPanel!=null)
		{
			for (int i=0; i<contentPanel.folderList.size(); i++)
			{
				contentPanel.folderList.get(i).setFatherAddress(fatherAddress+absAddress+'/');
			}
			for (int i=0; i<contentPanel.fileList.size(); i++)
			{
				contentPanel.fileList.get(i).setFatherAddress(fatherAddress+absAddress+'/');
			}
		}
	}

	///重命名 更改nameField状态
	private void resetName()
	{
		nameField.setEditable(true);
		nameField.addFocusListener(nameFieldFocusListener);
		nameField.addActionListener(nameFieldActionListener);
	}

	private void create()
	{
		fileType="folder";
		nameField.setText("new folder");
		resetName(true);
	}

	//获取绝对地址
	String getAddress()
	{
		return fatherAddress+absAddress+'/';
	}

	private FocusListener nameFieldFocusListener=new FocusListener()
	{
		public void focusGained(FocusEvent arg0)
		{
		}
		public void focusLost(FocusEvent e)
		{
			nameField.setEditable(false);
			resetName(false);
		}
	};

	private ActionListener nameFieldActionListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent arg0)
		{
			fatherContentPanel.requestFocus();
		}
	};
}

