package File;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//文件管理系统的工具页面
class FolderToolBar
{
	private JTextField addressField;
	private JTextField searchField;

	JPanel panel;
	private static FolderToolBar folderToolBar;

	static FolderToolBar getToolBar()
	{
		return folderToolBar;
	}

	FolderToolBar()
	{

		folderToolBar=this;

		panel=new JPanel();
		panel.setLayout(new BorderLayout());

		JButton back=new JButton(new ImageIcon(FolderToolBar.class.getResource("/img/back.png")));
		back.setBorderPainted(false);
		back.setFocusPainted(false);
		//在磁盘下或者在磁盘的第一级目录
		ActionListener backButtonListener = e -> {
			if (ContentPanel.getShowingPanel() == null || ContentPanel.getShowingPanel().fatherContentPanel == null)    //在磁盘下或者在磁盘的第一级目录
			{
				setAddress("LocalDisk");
				ContentPanel.switchPanel(Disk.getDiskPanel());
			} else {
				ContentPanel.switchPanel(ContentPanel.getShowingPanel().fatherContentPanel);
			}
		};
		back.addActionListener(backButtonListener);
		panel.add(back,BorderLayout.WEST);


		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());

		JButton enterSearch = new JButton(new ImageIcon(FolderToolBar.class.getResource("/img/search.png")));
		enterSearch.setBorderPainted(false);
		enterSearch.setFocusPainted(false);
		//清空面板
		ActionListener searchButtonListener = e -> {
			JPanel panel = new JPanel();
			String str = searchField.getText();
			while (str.length() != 0 && str.charAt(str.length() - 1) == ' ')
				str = str.substring(0, str.length() - 1);

			if (str.length() == 0) return;

			//清空面板
			Disk.getMainPanel().removeAll();
			Disk.getMainPanel().repaint();
			Disk.getMainPanel().updateUI();


			ContentPanel contentPanel = ContentPanel.getShowingPanel();
			if (contentPanel == null) contentPanel = Disk.contentPanel;
			contentPanel.addKeyStringDocument(str, panel);

			panel.setBackground(Color.white);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			Disk.getMainPanel().add(panel);
		};
		enterSearch.addActionListener(searchButtonListener);
		searchPanel.add(enterSearch,BorderLayout.EAST);


		searchField=new JTextField();
		searchField.setPreferredSize(new Dimension(150,30));
		searchField.setFont(new Font(searchField.getFont().getFontName(),searchField.getFont().getStyle(),15));
		searchPanel.add(searchField,BorderLayout.CENTER);


		panel.add(searchPanel,BorderLayout.EAST);

		JPanel addressPanel = new JPanel();
		addressPanel.setLayout(new BorderLayout());
		addressField=new JTextField();
		addressField.setFont(new Font(addressField.getFont().getFontName(),addressField.getFont().getStyle(),15));

		addressPanel.add(addressField,BorderLayout.CENTER);
		panel.add(addressPanel,BorderLayout.CENTER);
	}

	void setAddress(String str)
	{
		addressField.setText(str);
	}

}
