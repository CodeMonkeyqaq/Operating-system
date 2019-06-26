package File;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

//文件
public class MyFile extends FCB
{
	private Vector bufferString=new Vector(10);
	FileViewPanel fileView=new FileViewPanel();

	private int bufferIndex;
	String text="";

	private JMenuBar menuBar;
	private JFrame frame;
	private JPanel panel;
	private JTextArea textArea;


	class FileViewPanel extends JPanel
	{
		void setViewPanel()
		{
			viewPanel.setBackground(Color.white);
			viewPanel.setBounds(15, 5, 70, 70);
			viewPanel.addMouseListener(fileMouseListener);
			viewImg = new ImageIcon(MyFile.class.getResource("/img/file.png"));
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

		FileViewPanel()
		{
			setLayout(null);
			setBackground(Color.white);
			setPreferredSize(new Dimension(100,100));
			setViewPanel();
			setNamePanel();
		}

		//文档图标的鼠标监听
		MouseListener fileMouseListener=new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{

				if (e.getButton()==MouseEvent.BUTTON3)
				{
					JPopupMenu menu=new JPopupMenu();
					JMenuItem openMenu=new JMenuItem("open");
					openMenu.addActionListener(openMenuListener);
					menu.add(openMenu);

					JMenuItem deleteMenu=new JMenuItem("delete");
					deleteMenu.addActionListener(deleteMenuListener);
					menu.add(deleteMenu);

					JMenuItem resetNameMenu=new JMenuItem("rename");
					resetNameMenu.addActionListener(resetNameMenuListener);
					menu.add(resetNameMenu);

					JMenuItem propertyMenu=new JMenuItem("attribute");
					propertyMenu.addActionListener(propertyMenuListener);
					menu.add(propertyMenu);

					menu.show(e.getComponent(),e.getX(),e.getY());
				}
				if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2)
				{
					viewPanel.setBackground(Color.white);
					open();
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
			ActionListener deleteMenuListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					delete(true);
				}
			};
			ActionListener resetNameMenuListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					resetName();
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

	private void setMenuBar()
	{
		menuBar=new JMenuBar();
		menuBar.setBounds(0, 0, 500, 20);

		JMenu file=new JMenu("file");
		JMenuItem save=new JMenuItem("save file");
		save.addActionListener(saveButtonListener);
		JMenuItem exit=new JMenuItem("exit");
		exit.addActionListener(exitButtonListener);
		file.add(exit);
		file.add(save);
		menuBar.add(file);
		panel.add(menuBar);
	}

	private void setTextArea()
	{
		textArea=new JTextArea();
		textArea.setBackground(Color.white);
		textArea.addKeyListener(setTextListener);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(scroll);
	}

	//创建记事本
	private void createTxt()
	{
		setMenuBar();
		setTextArea();

		frame=new JFrame();
		frame.setJMenuBar(menuBar);
		frame.add(panel);
		frame.setSize(400, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(closeListener);
	}

	MyFile(ContentPanel father)
	{
		fatherContentPanel=father;
		panel=new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(new GridLayout(1,1));

		createTxt();

		create();
	}

	MyFile(Block block, ContentPanel father)
	{
		fatherContentPanel=father;
		fatherContentPanel.fileList.add(this);

		panel=new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(new GridLayout(1,1));

		createTxt();
		getProperty(block);
		nameField.setText(absAddress);
		text=block.data;
		if (text==null) text="";
		fileType="file";
	}

	private void create()
	{
		fileType="file";
		nameField.setText("new file");
		resetName(true);
	}



	//打开
	public void open()
	{
		if (frame.isShowing())
		{
			frame.requestFocus();
			return ;
		}
		block.setProperty(this);
		frame.setVisible(true);
		frame.setTitle("text_"+absAddress);
		textArea.setText(text);
		bufferString.clear();
		bufferIndex=0;
		bufferString.add(text);
	}

	//删除
	public boolean delete(boolean isRootPanel)
	{
		if (frame.isShowing())
		{
			frame.requestFocus();
			JOptionPane.showMessageDialog(null, "file is under used", "can't be deleted", JOptionPane.ERROR_MESSAGE, null);
			return false;
		}
		else
		{
			if (isRootPanel)
			{
				int option = JOptionPane.showConfirmDialog(
						null, "delete file?",
						"delete file", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null);
				if (option==JOptionPane.NO_OPTION)	return false;
			}

			Disk.fat.deleteBlock(this);
			fatherContentPanel.delete(this);

			Block fatherBlock;
			if (fatherContentPanel.getFolder()==null) fatherBlock=Disk.block[0];
			else fatherBlock=fatherContentPanel.getFolder().block;
			fatherBlock.setData(fatherContentPanel);


			return true;
		}
	}

	//添加入缓冲，给撤消使用
	private void addBuffer()
	{

		if (bufferString.get(bufferIndex).equals(textArea.getText())) return ;
		for (int i=bufferString.size()-1; i>bufferIndex; i--)
		{
			bufferString.remove(i);
		}
		if (bufferString.size()<10)
		{
			bufferIndex++;
			bufferString.add(textArea.getText());
		}
		else
		{
			for (int i=0; i<9; i++) bufferString.set(i, bufferString.get(i+1));
			bufferString.set(9, textArea.getText());
		}
	}

	//退出
	private void fileExit()
	{
		if (!text.equals(textArea.getText()))
		{
			int option = JOptionPane.showConfirmDialog(null, "file has been changed, save it?", "save file", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
			switch (option)
			{
				case JOptionPane.YES_OPTION:
					text=textArea.getText();
					block.setData(text);
					frame.dispose();
					break;
				case JOptionPane.NO_OPTION:
					frame.dispose();
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
			}
		}
		else frame.dispose();

		block.setProperty(this);
	}

	//是否重名
	private boolean checkName(String str)
	{
		for (int i=0; i<fatherContentPanel.fileList.size(); i++)
			if (fatherContentPanel.fileList.get(i)!=this && fatherContentPanel.fileList.get(i).absAddress.equals(str))	return false;
		return true;
	}

	//检查重命名后的命名是否规范，若不规范则自动修改
	private void resetName(boolean isAutomic)
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
			while (!checkName(newName+"("+ ++i +')'));
			newName=newName+"("+ i +')';
		}
		absAddress=newName;
		nameField.setText(absAddress);

		if (!isAutomic)
		{
			block.setProperty(this);
		}
	}

	private void resetName()
	{
		nameField.setEditable(true);
		nameField.addFocusListener(nameFieldFocusListener);
		nameField.addActionListener(nameFieldActionListener);
	}


	//——————————————————————————————————————————————————————监听——————————————————————————————————————————————————————

	//命名
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

	//关闭
	private WindowAdapter closeListener=new WindowAdapter()
	{
		public void windowClosing(WindowEvent   e)
		{
			fileExit();
		}
	};

	//保存
	private ActionListener saveButtonListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			text=textArea.getText();
			block.setData(text);
		}
	};

	//退出
	private ActionListener exitButtonListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			fileExit();
		}
	};

	//修改文档
	private KeyListener setTextListener=new KeyListener()
	{
		public void keyPressed(KeyEvent arg0)
		{
		}

		public void keyReleased(KeyEvent arg0)
		{
			addBuffer();
		}

		public void keyTyped(KeyEvent arg0)
		{
		}

	};
}
