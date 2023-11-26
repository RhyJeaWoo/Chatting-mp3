package ChatProgram.Client;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import ChatProgram.Client.*;

public class Client extends JFrame implements ActionListener, KeyListener{
	
	// Login Frame
	private JFrame Login_GUI = new JFrame();
	private JPanel Login_panel;
	private JTextField textField_ip;
	private JTextField textField_port;
	private JTextField textField_id;
	private JButton button_login = new JButton("�� ��");
	
	// Main Frame
	private JPanel contentPane;
	private JTextField textField_message;
	private JButton button_send_note = new JButton("����������");
	private JButton button_join_room = new JButton("ä�ù�����");
	private JButton button_create_room = new JButton("�游���");
	private JButton button_send_message = new JButton("����");
	private JButton button_send_file = new JButton("����");
	private JButton butoon_play_list_music = new JButton("���� ���");
	private JList list_user = new JList();
	private JList list_roomname = new JList();
	private JTextArea textArea_chat;
	
	ImageIcon img = new ImageIcon("D:\\workspace\\1.jpg"); // (1) ��� img �߰� 
	
	// Network Source
	private Socket socket = null;
	private String ip = "127.0.0.1";
	private int port = 7777;
	private String id ="noname";
	
	private InputStream inputStream;
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;



	//etc valuable
	private Vector vector_user_list = new Vector();
	private Vector vector_room_list = new Vector();	
	private StringTokenizer stringTokenizer;
	private String my_room = null; //���� ���� ��
	
	public Client(){
		Login_init(); //Login GUI
		Main_init();  //Main GUI
		start();	  //ACTION
	}
	private void start(){ //ACTION
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			// TODO: handle exception
		}
		button_login.addActionListener(this);
		button_send_note.addActionListener(this);
		button_join_room.addActionListener(this);
		button_create_room.addActionListener(this);
		button_send_message.addActionListener(this);
		button_send_file.addActionListener(this);
		butoon_play_list_music.addActionListener(this);
		textField_message.addKeyListener(this);
	}
	
	
	private void Main_init(){ //Main GUI
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 674, 481);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("�� ü �� �� ��");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 10, 100, 15);
		contentPane.add(lblNewLabel);
		
		
		list_user.setBounds(12, 33, 100, 125);
		contentPane.add(list_user);
		list_user.setListData(vector_user_list);
		
		button_send_note.setBounds(12, 168, 100, 23);
		button_send_note.setFont(new Font("���������230", Font.PLAIN, (int) 12));
		contentPane.add(button_send_note);
		
		
		
		butoon_play_list_music.setBounds(12, 198 , 100 ,23);
		butoon_play_list_music.setFont(new Font("���������230", Font.PLAIN, (int) 12));
		contentPane.add(butoon_play_list_music);
		
		JLabel lblNewLabel_1 = new JLabel("ä �� �� �� ��");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(12, 223, 100, 15);
		contentPane.add(lblNewLabel_1);
		
		
		list_roomname.setBounds(12, 248, 100, 125);
		contentPane.add(list_roomname);
		list_roomname.setListData(vector_room_list);
		
		
		button_join_room.setBounds(12, 379, 100, 23);
		button_join_room.setFont(new Font("���������230", Font.PLAIN, (int) 12));
		contentPane.add(button_join_room);
		
		button_create_room.setBounds(12, 412, 100, 23);
		button_create_room.setFont(new Font("���������230", Font.PLAIN, (int) 12));
		contentPane.add(button_create_room);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(124, 6, 522, 396);
		
		contentPane.add(scrollPane);
		
		textArea_chat = new JTextArea() {
	          {
	             setOpaque(false);
	          }
	     
	

	          public void paintComponent(Graphics g) { //(1) ��� img �߰�
	             g.drawImage(img.getImage(), 0, 0, null);
	             super.paintComponent(g);     
	             int pos = textArea_chat.getText().length();
		          
		          textArea_chat.setCaretPosition(pos);
		          
	          }
	          
	          
	       };

	    scrollPane.setViewportView(textArea_chat);

		textArea_chat.setEditable(false);
		
		textField_message = new JTextField();
		textField_message.setBounds(124, 413, 300, 21);
		contentPane.add(textField_message);
		textField_message.setColumns(10);
		textField_message.setEnabled(false);
		
		button_send_message.setBounds(446, 412, 95, 23);
		button_send_message.setFont(new Font("���������230", Font.PLAIN, (int) 12));
		button_send_message.setEnabled(false);
		contentPane.add(button_send_message);
		
		button_send_file.setBounds(551, 412, 95, 23);
		button_send_file.setFont(new Font("���������230", Font.PLAIN, (int) 12));
		button_send_file.setEnabled(false);
		
		contentPane.add(button_send_file);

		
		
		
		this.setVisible(false);
	}
	
	
	private void Login_init(){ //Login GUI
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Login_GUI.setBounds(100, 100, 318, 399);
		Login_panel = new JPanel();
		Login_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_panel);
		Login_panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(44, 141, 77, 15);
		Login_panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Port");
		lblNewLabel_1.setBounds(44, 197, 77, 15);
		Login_panel.add(lblNewLabel_1);
		
		textField_ip = new JTextField();
		textField_ip.setBounds(120, 138, 137, 21);
		Login_panel.add(textField_ip);
		textField_ip.setColumns(10);
		
		textField_port = new JTextField();
		textField_port.setBounds(120, 194, 137, 21);
		Login_panel.add(textField_port);
		textField_port.setColumns(10);
		
		JLabel lblId = new JLabel("ID");
		lblId.setBounds(44, 254, 57, 15);
		Login_panel.add(lblId);
		
		textField_id = new JTextField();
		textField_id.setBounds(119, 251, 138, 21);
		Login_panel.add(textField_id);
		textField_id.setColumns(10);
		
		
		button_login.setBounds(44, 328, 213, 23);
		Login_panel.add(button_login);
		Login_GUI.setVisible(true);;
	}
	private void network(){
		try {
			socket = new Socket(ip, port);
			if(socket != null){ //socket ok!!
				connection();
			}
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void connection(){
		try {
			inputStream = socket.getInputStream();
			dataInputStream = new DataInputStream(inputStream);
			outputStream = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		}
		this.setVisible(true);
		this.Login_GUI.setVisible(false);
		send_message(id); //first connect -> send id
		vector_user_list.add(id); //add my id in user_list
		Thread thread = new Thread(new Socket_thread());
		thread.start();
		
	}
	public class Socket_thread implements Runnable{
		public void run() {
			// TODO Auto-generated method stub		
			
			 String message = null;
		     String[] receivedMsg = null;
		     
		     boolean isStop = false;
			
			while(true){
				try {
					
					
					InMessage(dataInputStream.readUTF());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					try{
						outputStream.close();
						inputStream.close();
						dataInputStream.close();
						dataOutputStream.close();
						socket.close();
						
						JOptionPane.showMessageDialog(null, "������ ���� ������", "�˸�", 
								JOptionPane.ERROR_MESSAGE);
					}catch(IOException e1){}
					break;
					
				}
			}
		}
	}
	private void InMessage(String str){ //all message from server
		stringTokenizer = new StringTokenizer(str, "/");
		String protocol = stringTokenizer.nextToken();
		String message = stringTokenizer.nextToken();
		System.out.println("�������� : " + protocol);
		System.out.println("���� : " + message);
		if(protocol.equals("NewUser")){
			vector_user_list.add(message);
		}
		else if(protocol.equals("OldUser")){
			vector_user_list.add(message);
		}
		else if(protocol.equals("Note")){
			String note = stringTokenizer.nextToken();
			System.out.println(message + " ����ڿ��� �� ���� " + note);
			JOptionPane.showMessageDialog(null, note, message + "������ ���� �� ����", JOptionPane.CLOSED_OPTION); //basic support dialog
		}
		else if(protocol.equals("user_list_update")){
			list_user.setListData(vector_user_list);
		}
		else if(protocol.equals("CreateRoom")){
			my_room = message;
			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
			button_send_message.setEnabled(true);
			button_send_file.setEnabled(true);
			textField_message.setEnabled(true);
			button_login.setEnabled(false);
		}
		else if(protocol.equals("CreateRoomFail")){
			JOptionPane.showMessageDialog(null, "�� ����� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		}
		else if(protocol.equals("NewRoom")){
			vector_room_list.add(message);
			list_roomname.setListData(vector_room_list);
		}
		else if(protocol.equals("OldRoom")){
			vector_room_list.add(message);
		}
		else if(protocol.equals("room_list_update")){
			list_roomname.setListData(vector_room_list);
		}
		else if(protocol.equals("JoinRoom")){
			my_room = message;
			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
			button_send_message.setEnabled(true);
			button_send_file.setEnabled(true);
			textField_message.setEnabled(true);
		}
		else if(protocol.equals("ExitRoom")){
			vector_room_list.remove(message);
		}
		else if(protocol.equals("Chatting")){
			String msg = stringTokenizer.nextToken();
			textArea_chat.append(message + " : " + msg + "\n");
		}
		else if(protocol.equals("UserOut")){
			vector_user_list.remove(message);
		}
	}
	private String getLocalServerIp()
	{
		try
		{
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
		        {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
		            {
		            	return inetAddress.getHostAddress().toString();
		            }
		        }
		    }
		}
		catch (SocketException ex) {}
		return null;
	}
	private void send_message(String message){ //button
		try {
			dataOutputStream.writeUTF(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Client();
	//	MP3Player ad = new MP3Player();
		
		
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == button_login){
			System.out.println("login");
			if(textField_ip.getText().length() == 0){
				JOptionPane.showMessageDialog(null, "IP�� �Է����ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
				textField_ip.requestFocus();
			}
			else if(textField_port.getText().length() == 0){
				JOptionPane.showMessageDialog(null, "Port��ȣ�� �Է����ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
				textField_port.requestFocus();
			}
			else if(textField_id.getText().length() == 0){
				JOptionPane.showMessageDialog(null, "ID�� �Է����ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
				textField_id.requestFocus();
			}
			else{
				ip = textField_ip.getText().trim();
				port = Integer.parseInt(textField_port.getText().trim());
				id = textField_id.getText().trim();
				network();
			}
		}
		else if(e.getSource() == button_send_note){
			System.out.println("send_note");
			String user = (String) list_user.getSelectedValue();
			String note = JOptionPane.showInputDialog("�����޼���"); //basic support dialog
			if(note != null){
				send_message("Note/" + user + "/" + note); //ex) Note/User2/hi
			}
			System.out.println("�޴»�� : " + user + " | ���� ���� : " + note);
		}
		else if(e.getSource() == button_join_room){
			String JoinRoom = (String) list_roomname.getSelectedValue();
			
			if(my_room!= null){
				if(my_room.equals(JoinRoom)){
					JOptionPane.showMessageDialog(null, "���� ä�ù��Դϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				}
				send_message("ExitRoom/"+my_room);
				textArea_chat.setText("");
			}
			
			
			send_message("JoinRoom/" + JoinRoom);
			System.out.println("join_room");
			
		}
		else if(e.getSource() == button_create_room){
			String roomname = null;
			if(my_room == null){
				roomname = JOptionPane.showInputDialog("�� �̸�");
			}
			if(roomname != null){
				send_message("CreateRoom/"+roomname);
				button_create_room.setText("�泪����");
			}else{
				send_message("ExitRoom/"+my_room);
				button_send_message.setEnabled(false);
				button_send_file.setEnabled(false);
				my_room = null;
				button_create_room.setText("�游���");
				textArea_chat.setText("");
				JOptionPane.showMessageDialog(null, "ä�ù濡�� �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
			}
			System.out.println("create_room");
		}
		else if(e.getSource() == button_send_message){
			System.out.println("send_message");
			if(my_room == null){
				JOptionPane.showMessageDialog(null, "ä�ù濡 �������ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
			else{
				send_message("Chatting/"+my_room+"/" + textField_message.getText().trim());
				textField_message.setText("");
				textField_message.requestFocus();
			}
		}
		else if(e.getSource() == button_send_file){
			System.out.println("send_file");
			if(my_room == null){
				JOptionPane.showMessageDialog(null, "ä�ù濡 �������ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
			else{
				JFileChooser jFileChooser = new JFileChooser("C://");
				jFileChooser.setDialogTitle("���� ����");
				jFileChooser.setMultiSelectionEnabled(true);
				jFileChooser.setApproveButtonToolTipText("������ ������ �����ϼ���");
				jFileChooser.showDialog(this, "����");
				File path = jFileChooser.getSelectedFile();
				if (path != null) {
					send_message("FileStart/"+path.getName());
					Socket socket_tmp;
					try {
						socket_tmp = new Socket(ip, port+1);
						FileSender fileSender = new FileSender(socket_tmp,path);
						fileSender.start();
					} 
					catch (UnknownHostException e1) {} 
					catch (IOException e1) {}
					
				
				}
				textField_message.requestFocus();
			}
		}else if(e.getSource() == butoon_play_list_music)
		{
			System.out.println("play_music_on");
			if(my_room == null) {
				JOptionPane.showMessageDialog(null, "ä�ù濡 �������ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
			}else {
				final MP3Player ad = new MP3Player();
				ad.createMainFrame();
			}
				
			
		}
	}
	
	  public void Clear() {
		  textArea_chat.setText(""); //�ʱ�ȭ�ǰ�
		  textField_message.requestFocus();
		}
	
	public void keyPressed(KeyEvent arg0) { // ������ ��
		// TODO Auto-generated method stub
		
	}
	public void keyReleased(KeyEvent e) { //������ ���� ��
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 10){ //enter
			send_message("Chatting/"+my_room+"/" + textField_message.getText().trim());
			textField_message.setText("");
			textField_message.requestFocus();
		}
	}
	public void keyTyped(KeyEvent arg0) { //Ÿ����
		// TODO Auto-generated method stub
		
	}

}
