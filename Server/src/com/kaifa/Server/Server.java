package com.kaifa.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA_2_3.portable.OutputStream;

public class Server {
	public static ArrayList<Socket> socketList = new ArrayList<Socket>();
	public static List<String> myName = new ArrayList<String>();
	public static List<String> friendName = new ArrayList<String>();
	public static ArrayList<Socket> al_socket_notify = new ArrayList<Socket>();
	public static ArrayList<String> al_myName_notify = new ArrayList<String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(25000);
		final ServerSocket ss2 = new ServerSocket(15000);
		new Thread() {
			public void run() {
				while (true) {
					try {
						Socket s = ss2.accept();
						final BufferedReader br = new BufferedReader(
								new InputStreamReader(s.getInputStream()));
						String name;
						name = br.readLine();
						al_myName_notify.add(name);
						al_socket_notify.add(s);
						
						// 判断是否有myName相同的socket如果有则将其remove
						for (int i = Server.al_myName_notify.size() - 1; i >= 0; i--) {
							boolean flag = false;
							String nameTemp = Server.al_myName_notify.get(i);
							if (nameTemp != "" && nameTemp != null) {
								for (int j = i - 1; j >= 0; j--) {
									if (j >= 0) {
										if (Server.al_myName_notify.get(j).equals(
												nameTemp)) {
											Server.al_myName_notify.remove(j);
											Server.al_socket_notify.remove(j);
											flag = true;
										}
									}
								}
								if (flag == true)
									i = Server.al_myName_notify.size();
							}
						}

						// /没登录的人进行消息缓存
						int account = 0;
						File file = new File(
								"ServerData", name
										+ "_notifyCache.txt");
						if (file.exists()) {
							BufferedReader br_notify = new BufferedReader(
									new InputStreamReader(new FileInputStream(
											file)));
							String content = null;
							String friendName = null;
							while ((content = br_notify.readLine()) != null) {
								account++;
								if(content!=null){
									friendName = content;
								}
							}
							java.io.OutputStream os = s.getOutputStream();
							os.write((name+"#"+friendName+"#"+account+"#"+"Notify" + "\n").getBytes("utf-8"));
							System.out.println(name+"#"+friendName+"#"+account+"#"+"Notify" + "\n");
							br_notify.close();
							file.delete();
						}
						// 退出登录是将通知相程移除
						System.out.println("通知线程启动");
						System.out.println("al_myName_notify"
								+ al_myName_notify.toString());
						new Thread() {
							public void run() {
								try {
									String finish = br.readLine();
									for (int i = 0; i < al_myName_notify.size(); i++) {
										if (al_myName_notify.get(i).equals(
												finish)) {
											al_myName_notify.remove(i);
											al_socket_notify.remove(i);
											break;
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}.start();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		while (true) {
			Socket s = ss.accept();
			socketList.add(s);
			System.out.println("xin socket" + s);
			new Thread(new ServerThread(s)).start();
		}
	}
}
