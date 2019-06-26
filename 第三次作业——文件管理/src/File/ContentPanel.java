package File;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.*;


import File.Disk.DiskPanel;

//面板
class ContentPanel extends JPanel
{
	//该文件夹下的文件夹
	Vector<Folder> folderList;
	//该文件夹下的文件
	Vector<MyFile> fileList;
	//父文件夹面板
	ContentPanel fatherContentPanel;
	//正在显示哪个文件夹
	private static ContentPanel showingPanel=null;

	ContentPanel(ContentPanel father)
	{
		fatherContentPanel=father;
		setBackground(Color.white);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		//鼠标监听
		MouseListener contentMouseListener = new MouseListener()
		{
			public void mouseClicked(MouseEvent e) {
				//右键
				if (e.getButton() == MouseEvent.BUTTON3) {
					//新建文件
					JPopupMenu menu = new JPopupMenu();
					JMenuItem newFile = new JMenuItem("new file");
					newFile.addActionListener(newFileMenuListener);
					menu.add(newFile);

					//新建文件夹
					JMenuItem newFolder = new JMenuItem("new folder");
					newFolder.addActionListener(newFolderMenuListener);
					menu.add(newFolder);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}

			ActionListener newFileMenuListener = e -> createFile();

			ActionListener newFolderMenuListener = e -> createFolder();
		};
		addMouseListener(contentMouseListener);
		folderList=new Vector();
		fileList=new Vector();
	}


	private Timer refreshTime;
	private void deleteTime()
	{
		if (refreshTime==null) return ;
		refreshTime.cancel();
		refreshTime=null;
	}

	//刷新
	void refresh()
	{
		refreshTime=new Timer();
		class RefreshTask extends TimerTask
		{
			public void run()
			{
				//	显示应该显示的文件和文件夹
				for (Folder folder : folderList) add(folder.folderView);

				for (MyFile myFile : fileList) add(myFile.fileView);

				repaint();
				updateUI();

				deleteTime();
			}
		}

		removeAll();
		updateUI();
		refreshTime.schedule(new RefreshTask(), 50);

		Folder folder=getFolder();
		if (folder==null) FolderToolBar.getToolBar().setAddress("LocalDisk/");
		else FolderToolBar.getToolBar().setAddress(folder.getAddress());
	}

	//获取所有文件夹
	Folder getFolder()
	{
		if (this==Disk.contentPanel) return null;
		for (int i=0; i<fatherContentPanel.folderList.size(); i++)
			if (fatherContentPanel.folderList.get(i).contentPanel==this) return fatherContentPanel.folderList.get(i);

		return null;
	}

	//获取该块
	private Block getContentBlock()
	{
		Folder fatherFolder=getFolder();
		if (fatherFolder==null) return Disk.block[0];
		else return fatherFolder.block;
	}

	//创建文件夹
	private void createFolder()
	{
		Block block=Disk.fat.getBlock();
		if (block==null)
		{
			JOptionPane.showMessageDialog(null, "not enough room to add new files");
			return ;
		}

		Folder folder=new Folder(this);
		folder.block=block;
		folderList.add(folder);

		Folder fatherFolder=folder.fatherContentPanel.getFolder();
		if (fatherFolder==null)
			folder.setFatherAddress("LocalDisk/");
		else
			folder.setFatherAddress(fatherFolder.getAddress());

		folder.block.setProperty(folder);
		Disk.fat.setUseBlock(folder.block.index);

		getContentBlock().setData(this);
		refresh();
	}

	//新建文件
	private void createFile()
	{
		Block block=Disk.fat.getBlock();
		if (block==null)
		{
			JOptionPane.showMessageDialog(null, "not enough room to add new files");
			return ;
		}

		MyFile file=new MyFile(this);
		file.block=block;
		fileList.add(file);

		if (file.fatherContentPanel.getFolder()==null) file.setFatherAddress("LocalDisk/");
		else file.setFatherAddress(file.fatherContentPanel.getFolder().getAddress());

		file.block.setProperty(file);
		Disk.fat.setUseBlock(file.block.index);

		getContentBlock().setData(this);
		refresh();
	}

	//删除文件
	void delete(MyFile file)
	{
		if (fileList.remove(file))
		{
			refresh();
		}
	}

	//搜索文件或文件夹
	void addKeyStringDocument(String key, JPanel panel)
	{
		for (Folder folder : folderList) {
			if (folder.absAddress.contains(key)) panel.add(folder.folderView);
			folder.contentPanel.addKeyStringDocument(key, panel);
		}
		for (MyFile myFile : fileList) {
			if (myFile.absAddress.contains(key)) panel.add(myFile.fileView);
		}
	}


	static ContentPanel getShowingPanel()
	{
		return showingPanel;
	}

	static void switchPanel(ContentPanel contentPanel)
	{
		showingPanel=contentPanel;
		Disk.getMainPanel().removeAll();
		Disk.getMainPanel().add(contentPanel);
		ContentPanel.getShowingPanel().refresh();
	}

	static void switchPanel(DiskPanel panel)
	{
		showingPanel=null;
		Disk.getMainPanel().removeAll();
		Disk.getMainPanel().add(panel);
		Disk.getMainPanel().repaint();
		Disk.getMainPanel().updateUI();
	}
}
