package ChatProgram.Client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import ChatProgram.Client.Client.Socket_thread;

public class FileSender extends Thread{
	private Socket socket;
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	private FileInputStream fileInputStream;
	private BufferedInputStream bufferedInputStream;
	private File file;
	public FileSender(Socket socket, File file) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.file = file;
		connection();
	}
	
	private void connection(){
		try {
			outputStream = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			FileSend();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void FileSend() throws IOException{
		fileInputStream = new FileInputStream(file);
		bufferedInputStream = new BufferedInputStream(fileInputStream);
        int len;
        int size = 4096;
        byte[] data = new byte[size];
        while ((len = bufferedInputStream.read(data)) != -1) {
        	dataOutputStream.write(data, 0, len);
        }
        dataOutputStream.flush();
        dataOutputStream.close();
        bufferedInputStream.close();
        fileInputStream.close();
        socket.close();
	}
}
