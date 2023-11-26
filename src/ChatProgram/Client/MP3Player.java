package ChatProgram.Client;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Iterator;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.protocol.DataSource;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import ChatProgram.Client.*;




@SuppressWarnings("unused")
public class MP3Player implements ActionListener,ControllerListener,MouseListener,KeyListener {
	
	private JFrame mainFrame;
	private JPanel jp1;
	private JPanel jp2;
	private JPanel jp10;
	private JPanel jp11;
	private JPanel jp12;
	private JButton jb0;
	private JButton jb1;
	private JButton jb2;
	private JButton jb3;
	private JScrollPane jsp;
	private JFileChooser fileChooser = new JFileChooser();
	private JLabel title;
	static JList jl;
	private JPopupMenu popup;
	private JMenuItem itemDel;
	private JMenuItem itemPlay;
	
	private JMenu Mfile = new JMenu("File");
	private JMenu Mhelp = new JMenu("Help");
	
	private JMenuItem MF1;
	private JMenuItem MF2;
	private JMenuItem MH1;
	
	JMenuBar menuBar = new JMenuBar();
	
	private int index = 0;
	
	private int mode = 0;
	
	/*
	 * 0 : 일반재생
	 * 1 : 하나반복
	 * 2: 전체반복
	 * 3: 랜덤재생
	 */
	
	private boolean power = false;
	private boolean doubleClicked = false;
	
	private Player player = null;
	private DataSource ds = null;
	
	static ArrayList<File> fileList = new ArrayList<File>();
	static DefaultListModel playList = new DefaultListModel();
	static Stack<File> historyList  = new Stack<File>();
	private Point popupPoint = new Point();
	
	public JMenuBar createMenu() {
		
		Mfile = new JMenu("File");
		Mhelp = new JMenu("Help");
		
		MF1 = new JMenuItem("Add Tracks");
		MF2 =  new JMenuItem ("Exit");
		MH1 = new JMenuItem("About");
		
		Mfile.add(MF1);
		Mfile.addSeparator();
		Mfile.add(MF2);
		
		Mhelp.add(MH1);
		
		menuBar.add(Mfile);
		menuBar.add(Mhelp);
		
		MF1.addActionListener(this);
		MF2.addActionListener(this);
		MH1.addActionListener(this);
		
		return menuBar;
		
	}
	
	public void createMainFrame() {
		mainFrame = new JFrame();
		mainFrame.setSize(400, 300);
		mainFrame.setJMenuBar(createMenu());
		mainFrame.setVisible(true);
		mainFrame.setTitle("MP3Player");//K-MP3Player
		
		createComponents();
		
		mainFrame.add(jp1, BorderLayout.NORTH);
		mainFrame.add(jp2, BorderLayout.CENTER);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	}
	
	
	public void createComponents() {
		jp1 =new JPanel();
		jp2 =new JPanel();
		jp10 =new JPanel();
		jp11 =new JPanel();
		jp12 =new JPanel();
		
		
		popup = new JPopupMenu("Popup Menu");
		itemPlay = new JMenuItem("Play this Track");
		itemDel = new JMenuItem("Delete Track(s)");
		popup.add(itemPlay);
		popup.addSeparator();
		popup.add(itemDel);
		
		itemPlay.addActionListener(this);
		itemDel.addActionListener(this);
		
		jl = new JList(playList);
		jsp = new JScrollPane(jl);
		title = new JLabel("Paly a Music File");
		
		jb0 = new JButton("|◁");
		jb1 = new JButton("■|▷");
		jb2 = new JButton("No Repeat");
		jb3 = new JButton("▷|");
		
		jp1.setBorder(LineBorder.createBlackLineBorder());
		jp2.setBorder(LineBorder.createBlackLineBorder());
		jp10.setBorder(LineBorder.createBlackLineBorder());
		jp11.setBorder(LineBorder.createBlackLineBorder());
		jp12.setBorder(LineBorder.createBlackLineBorder());
		
		jb0.addActionListener(this);
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		jb3.addActionListener(this);
		jl.addMouseListener(this);
		jl.addKeyListener(this);
		
		jp1.setLayout(new GridLayout(3,1));
		jp10.setLayout(new GridLayout(1,1));
		jp11.setLayout(new GridLayout(1,1));
		jp12.setLayout(new GridLayout(1,4));
		jp2.setLayout(new GridLayout(1,1));
	
		jl.setAutoscrolls(true);
		jl.setTransferHandler(new FileTransferHandler());
		
		jp1.add(jp10);
		jp1.add(jp11);
		jp1.add(jp12);
		jp10.add(title);
		jp12.add(jb0);
		jp12.add(jb1);
		jp12.add(jb3);
		jp12.add(jb2);
		
		jp2.add(jsp);
		
	}
	
	
	
	public static boolean checkConflict(File file) {
		if(fileList.contains(file)) {
			return true;
		}
		return false;
	}
	
	public void fileOpen() {
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(mainFrame)==JFileChooser.APPROVE_OPTION) {
			File[] tempFiles = fileChooser.getSelectedFiles();
			for(int i=0; i<tempFiles.length; i++){
				if(!checkConflict(tempFiles[i])) {
					if(tempFiles[i].getName().toLowerCase().endsWith("mp3")) {
						fileList.add(tempFiles[i]);
					}else {
						System.out.println("Invalid file");
					}
					
				}else {
					System.out.println("Already existing file");
				}
			}
			reList();
		}
	}
	
	
	public void startMedia(File file) {
		jl.setSelectedIndex(index);
		
		title.setText(file.getName());
		
		if(player != null) {
			player.stop();
			player.removeControllerListener(this);
			player.deallocate();
			player.close();
		}
		
		try {
			ds = Manager.createDataSource(file.toURI().toURL());
			
			player = Manager.createPlayer(ds);
			player.addControllerListener(this);
			power = true;
			jb1.setText("□|▶");
			player.start();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	
	public static int getPreviousTrack() {
		int i;
		
		while(!historyList.empty()) {
			File previousFile = historyList.pop();
			if(fileList.contains(previousFile)) {
				i = fileList.indexOf(previousFile);
				return i;
			}
		}
		
		i = (int)(Math.random()*fileList.size());
		return i;
	}
	
	public static void reList() {
		Iterator<File> it = MP3Player.fileList.iterator();
		
		
		try {
			File tempFile;
			while(it.hasNext()) {
			tempFile = it.next();
				if(!MP3Player.playList.contains(tempFile.getName())) {
					MP3Player.playList.add(playList.getSize(), tempFile.getName());
				}
			}
		}catch(Exception ex) {
				System.out.println(ex);
			}
			jl.setModel(playList);
			MP3Player.jl.repaint();
		}
	
	
		public void nextTrack(boolean doubleClicked) {
			if(jp11.getComponents().length>0) {
				jp11.removeAll();
			}
			
			if(mode == 0) {
				if(fileList.size() > index) {
					power = true;
					if(!doubleClicked) {
						index ++;
					}
					if(fileList.size() <=index) {
						index = 0;
					}
					historyList.add(fileList.get(index));
					startMedia(fileList.get(index));
				}else {
					if(player != null) {
					player.stop();
				}
				jp11.setVisible(false);
				if(jp11.getComponents().length>0) {
					jp11.removeAll();
		
				}
				index = 0;
				power = false;
				jb1.setText("■|▷");
				}
				}else if(mode == 1) {
					power = true;
					historyList.add(fileList.get(index));
					startMedia(fileList.get(index));
				}else if(mode == 2) {
					if(!doubleClicked) {
						index++;
					}
					if(fileList.size() <= index) {
						index = 0;
					}
					power = true;
					historyList.add(fileList.get(index));
					startMedia(fileList.get(index));
				}else if(mode == 3) {
					if(!doubleClicked) {
						index = (int)(Math.random()*fileList.size());
					}
					power = true;
					historyList.add(fileList.get(index));
					startMedia(fileList.get(index));
				}
			}
		
			
			
			public void previousTrack() {
				if(jp11.getComponents().length>0) {
					jp11.removeAll();
				}
				
				if(mode == 0) {
					if(fileList.size() > index) {
						power = true;
						if(index> 0) {
							index--;
						}else {
							index = fileList.size()-1;
						}
						startMedia(fileList.get(index));
					}else {
						index = 0;
						power = false;
						jb1.setText("■|▷");
					}
				}else if(mode==1) {
					power = true;
					startMedia(fileList.get(index));
				}else if(mode==2) {
					index --;
					if(index <0) {
						index = fileList.size()-1;
					}
					power = true;
					startMedia(fileList.get(index));
				}else if(mode == 3) {
					if(!doubleClicked) {
						index = getPreviousTrack();
					}
					power = true;
					startMedia(fileList.get(index));
				}

			}
			
			public void deleteTrack(int[] i) {
				for(int j=i.length-1;j>=0;j--) {
					fileList.remove(i[j]);
					playList.remove(i[j]);
				}
			if(fileList.size() <= index) {
				index = fileList.size() -1;
			}
			reList();
			}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MF2.getText())) {
			System.exit(0);
		}else if(e.getActionCommand().equals(MF1.getText())) {
			fileOpen();
		}else if(e.getActionCommand().equals(MH1.getText())) {
			JOptionPane.showMessageDialog(mainFrame, "Verseion: 1.0 " + "\nProkgrammer : Jeawoo woo" + 
		"\nEmail ujo235@naver.com", "MP3Play", JOptionPane.PLAIN_MESSAGE);
		}else if(e.getActionCommand().equals(itemPlay.getText())) {
			index = jl.locationToIndex(popupPoint);
			if(index >=0) {
				power = true;
				doubleClicked = true;
				nextTrack(doubleClicked);
				doubleClicked =false;
			}
			
			
		}else if(e.getActionCommand().equals(itemDel.getText())) {
			deleteTrack(jl.getSelectedIndices());
		}else if(e.getSource() == (JButton)jb0) {
			previousTrack();
		}else if(e.getSource() == (JButton)jb3) {
			nextTrack(doubleClicked);
		}else if(e.getSource() == (JButton)jb1) {
			if(!power && fileList.size()!=0) {
				jb1.setText("□|▶");
				power = true;
				if(jp11.getComponents().length == 0) {
					//nextTrack(doubleClick, skipped);
					historyList.add(fileList.get(0));
					startMedia(fileList.get(0));
				}
				
				player.start();

			}else if(power) {
				jb1.setText("■|▷");
				power = false;
				
				player.stop();
			}
		}else if(e.getSource() == (JButton)jb2) {
			mode++;
			if(mode >= 4) {
				mode = 0;
			}
			
			switch(mode) {
			case 0://일반 재생
				jb2.setText("No repeat");
				break;
			case 1://한번 반복
				jb2.setText("Repeat This");
				break;
			case 2://전체 반복
				jb2.setText("Repeat All");
				break;
			case 3://랜덤 재생
				jb2.setText("Random");
				break;
			}

		}
	}
	
	
	public void mouseClicked(MouseEvent e) {
		if(jl == (JList)e.getComponent() && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			
				index = jl.locationToIndex(e.getPoint());
				if(index >= 0) {
					power = true;
					doubleClicked = true;
					nextTrack(doubleClicked);
					doubleClicked = false;
				}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e){
		// TODO 자동 생성된 메소드 스텁
		
	}

	@Override
	public void mouseExited(MouseEvent e){
		// TODO 자동 생성된 메소드 스텁
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO 자동 생성된 메소드 스텁
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO 자동 생성된 메소드 스텁
		if(jl == e.getComponent() && e.getButton() == MouseEvent.BUTTON3) {
			popupPoint.setLocation(e.getPoint());
			popup.show(jl,e.getX(),e.getY());
			jl.setSelectedIndex(jl.locationToIndex(e.getPoint()));
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO 자동 생성된 메소드 스텁
		if(e.getKeyCode()== KeyEvent.VK_DELETE) {
			deleteTrack(jl.getSelectedIndices());
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO 자동 생성된 메소드 스텁
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO 자동 생성된 메소드 스텁
		
	}

	@Override
	public void controllerUpdate(ControllerEvent e) {

		if(e instanceof RealizeCompleteEvent) {
			Component controlComponent;
			
			if((controlComponent = player.getControlPanelComponent()) != null) {
				jp11.setLayout(new GridLayout(1,1));
				jp11.setVisible(false);
				jp11.add(controlComponent);
				jp11.setVisible(true);
			}
			
		}else if(e instanceof EndOfMediaEvent) {
			jp11.setVisible(false);
			nextTrack(doubleClicked);
		}
		
	}
	
	public static void main(String[] args) {
		new MP3Player().createMainFrame();
	}
	
}
	

class FileTransferHandler extends TransferHandler{
	private static final long serialVersionUID = 1L;
	public boolean canImoprt(JComponent arg0, DataFlavor[] arg1) {
		for(int i=0 ; i<arg1.length; i++) {
			if(!arg1[i].equals(DataFlavor.javaFileListFlavor)) {
				
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean importData(JComponent comp,Transferable t) {
		try {
			List<File> l = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			Iterator<File> iter = l.iterator();
			while(iter.hasNext()) {
				File file = iter.next();
				if(file.isDirectory()) {
					File[] tempFiles = getFiles(file);
					for(int i=0; i<tempFiles.length; i++) {
						MP3Player.fileList.add(tempFiles[i]);
					}
				}
				
				if(file.getName().toLowerCase().endsWith("mp3")) {
					if(!MP3Player.checkConflict(file)) {
						MP3Player.fileList.add(file);
					}
				}else if(!file.isDirectory()) {
					System.out.println("Invalid file");
					return false;
				}
			}
			MP3Player.reList();
			return true;
		}catch(Exception e) {System.out.println(e);}
		
		Toolkit.getDefaultToolkit().beep();
		return false;
	}
	
	
	public static File[] getFiles(File file) {
		ArrayList<File> l = new ArrayList <File>();
		for(int i = 0; i< file.listFiles().length; i++) {
			if(file.listFiles()[i].getName().toLowerCase().endsWith("mp")) {
				if(!MP3Player.checkConflict(file.listFiles()[i])) {
					l.add(file.listFiles()[i]);
				}
			}else if(file.listFiles()[i].isDirectory()) {
				File[] tempFiles = getFiles(file.listFiles()[i]);
				for(int j = 0; j<tempFiles.length; j++) {
					l.add(tempFiles[j]);
				}
			}
		}
		File[] files = new File[l.size()];
		for(int i= 0; i<l.size();i++) {
			files[i] =l.get(i);
		}
		return files;
	}
	
	
	
	
}

	

