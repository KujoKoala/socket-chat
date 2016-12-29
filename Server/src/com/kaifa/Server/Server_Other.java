package com.kaifa.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server_Other {
	public static ArrayList<Socket> al_Other = new ArrayList<Socket>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(45000);
		while(true){
			Socket s = ss.accept();
			al_Other.add(s);
			System.out.println("xin socket"+s);
			new Thread(new ServerThread(s)).start();
		}
	}

}
