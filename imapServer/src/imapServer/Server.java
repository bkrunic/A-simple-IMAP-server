package imapServer;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

public static int cmdId=0; // SVAKA KOMANDA IMA SVOJ ID
	
	public Server() throws Exception {
		// TODO Auto-generated constructor stub
		ServerSocket serverSocket=new ServerSocket(143); // IMAP SERVER KORISTI 143 PORT
		System.out.println("* OK IMAP Service Ready");
		
		while(true) {
			Socket socket=serverSocket.accept();
			ServerThread serverThread=new ServerThread(socket,cmdId);
			Thread thread=new Thread(serverThread);
			thread.start();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new Server();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
