package com.kaifa.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class ServerThread implements Runnable {
	private int account = 0;

	int index = 0;
	Socket s = null;
	int j;
	BufferedReader br = null;
	FileOutputStream fos_ZhuCe = null;
	BufferedReader br_DengLu = null;
	FileOutputStream fos_ZhuCe_UserOwnData = null;
	OutputStream os_TianJiaHaoYou = null;
	OutputStream os_TianJiaHaoYouSocket = null;

	// String myNameTemp =
	// null;//第一次发送时确定，事实证明无法确定，会出现nullpointerexception，所以count0，1.都写
	// String friendTemp = null;//第一次发送时确定
	public ServerThread(Socket s) throws IOException {
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream(),
				"utf-8"));
	}

	@Override
	public void run() {
		try {
			String content = null;
			while ((content = readFromClient()) != null) {
				
				//当用户退出和好友的聊天界面时，将会进入此条件，负责删除相应的socket
				if(content.contains("Delete_Socket_MainActivity")){
					String []data = content.split("#");
					for(int i=0; i<Server.myName.size(); i++){
						if(Server.myName.get(i).equals(data[0])){
							Server.friendName.remove(i);
							Server.myName.remove(i);
							Server.socketList.remove(i);
							break;
						}
					}
				}
				// 添加好友功能，从客户端发送数据TianJiaHaoYou，进入此功能
				if (content.contains("TianJiaHaoYou")) {
					boolean flag = false;
					System.out.println(content);
					String[] data = content.split("#");

					// 判断是否有此用户
					// 借用br_DengLu的变量D:\\myeclipseworkspace
					File file2 = new File("ServerData",
							"ZhuCeData.txt");
					br_DengLu = new BufferedReader(new InputStreamReader(
							new FileInputStream(file2)));
					String line = null;
					while ((line = br_DengLu.readLine()) != null) {
						String[] data2 = line.split("#");
						System.out.println(data2[0]);
						System.out.println(data[1]);
						if (data2[0].equals(data[1])) {
							flag = true;
							break;
						}
					}
					System.out.println("flag:" + flag);
					if (flag == false) {
						os_TianJiaHaoYouSocket = s.getOutputStream();
						os_TianJiaHaoYouSocket.write(("无该用户信息" + "\r\n")
								.getBytes());
					}
					if (flag == true) {
						// 判断是否已有该好友D:\\myeclipseworkspace\\
						boolean flag2 = true;
						File file = new File(
								"ServerData", data[0]
										+ ".txt");
						BufferedReader in_TianJiaHaoYou = new BufferedReader(
								new InputStreamReader(new FileInputStream(file)));
						String jiaoYan = null;
						while ((jiaoYan = in_TianJiaHaoYou.readLine()) != null) {
							if (jiaoYan.equals(data[1])) {
								flag2 = false;
							}
						}
						if (flag2 == false) {
							os_TianJiaHaoYouSocket = s.getOutputStream();
							os_TianJiaHaoYouSocket
									.write(("已经添加该用户为好友" + "\r\n").getBytes());
						}
						if (flag2 == true) {
							try {
								System.out.println("开始添加好友");
								os_TianJiaHaoYou = new FileOutputStream(file,
										true);
								os_TianJiaHaoYou.write((data[1] + "\r\n")
										.getBytes());
								os_TianJiaHaoYouSocket = s.getOutputStream();
								os_TianJiaHaoYouSocket.write((data[1] + "#"
										+ "Sucess" + "\r\n").getBytes());

							} catch (Exception e) {
								e.printStackTrace();
								try {
									os_TianJiaHaoYouSocket.write("Faile"
											.getBytes());
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							} finally {
								try {
									br_DengLu.close();
									os_TianJiaHaoYou.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// 登录功能，从客户端发送数据DengLu进入此功能；（之后要换更为严谨的口令）D:\\myeclipseworkspace
				if (content.contains("DengLu")) {
					System.out.println("content" + content);
					String data[] = content.split("#");
					boolean flag = true;
					try {
						File file = new File(
								"ServerData",
								"ZhuCeData.txt");
						br_DengLu = new BufferedReader(new InputStreamReader(
								new FileInputStream(file)));
						String line = null;
						while ((line = br_DengLu.readLine()) != null) {
							String zhangHaoHeMiMa[] = line.split("#");
							if (data[0].equals(zhangHaoHeMiMa[0])
									&& data[1].equals(zhangHaoHeMiMa[1])) {
								// 获取该用户所有的好友数据集D:\\myeclipseworkspace
								File file2 = new File(
										"ServerData",
										data[0] + ".txt");
								BufferedReader br = new BufferedReader(
										new InputStreamReader(
												new FileInputStream(file2)));
								String temp = null;
								String result = "";
								while ((temp = br.readLine()) != null) {
									result += temp + "#";
								}

								OutputStream os = s.getOutputStream();
								os.write((result + "Sucess" + "\r\n")
										.getBytes("utf-8"));
								flag = false;
							}
						}
						// 登录失败
						if (flag == true) {

							OutputStream os = s.getOutputStream();
							os.write(("Fail" + "\n").getBytes("utf-8"));

						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							br_DengLu.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				// 注册功能，从客户端发送数据ZhuCe进入此功能D:\\myeclipseworkspace
				if (content.contains("ZhuCe")) {
					System.out.println("进入注册功能");
					String data[] = content.split("#");
					try {
						File file = new File(
								"ServerData",
								"ZhuCeData.txt");
						fos_ZhuCe = new FileOutputStream(file, true);
						fos_ZhuCe.write((data[0] + "#" + data[1] + "\r\n")
								.getBytes());
						File file2 = new File(
								"ServerData", data[0]
										+ ".txt");
						// fos_ZhuCe_UserOwnData = newD:\\myeclipseworkspace
						// FileOutputStream(file2,true);
						// fos_ZhuCe_UserOwnData.write((data[0]+"\r\n").getBytes("utf-8"));
						file2.createNewFile();
						OutputStream os = s.getOutputStream();
						os.write(("Sucess" + "\n").getBytes("utf-8"));
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							// fos_ZhuCe_UserOwnData.close();
							// fos_ZhuCe.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				// 聊天功能，从客户端发送LiaoTian进入此功能
				if (content.contains("LiaoTian")) {
					// 获取该socket对应的list的idex
					String[] data;

					// 一进入聊天界面就进入此条件（点击按钮才进入）
					if (account == 0) {
						System.out.println("First");
						System.out.println("content" + content);
						data = content.split("#");
						Server.myName.add(data[0]);
						Server.friendName.add(data[1]);

						// GC判断是否有myName相同的socket如果有则将其remove
						for (int i = Server.myName.size() - 1; i >= 0; i--) {
							boolean flag = false;
							String nameTemp = Server.myName.get(i);
							if (nameTemp != "" && nameTemp != null) {
								for (int j = i - 1; j >= 0; j--) {
									if (Server.myName.get(j).equals(nameTemp)) {
										Server.myName.remove(j);
										Server.friendName.remove(j);
										Server.socketList.remove(j);
										flag = true;
									}
								}
								if (flag == true)
									i = Server.myName.size();
							}
						}

						// 打印观察服务端情况
						System.out.println(Arrays.toString(data));
						System.out.println("Socket"
								+ Server.socketList.toString());
						System.out.println("myName" + Server.myName.toString());
						System.out.println("friendName"
								+ Server.friendName.toString());
						// for(int i=0; i<Server.myName.size(); i++){
						System.out.println("index" + index);

						// 解决第二种情况,相当于GC，去掉无用socket；
						if (Server.socketList.size() > Server.myName.size()) {
							int t = Server.socketList.size()
									- Server.myName.size();
							for (int i = Server.socketList.size() - 2; t > 0; t--, i--) {
								Server.socketList.remove(i);
							}
						}

						// 正确获取当前的socket的myName和friendName
						for (int i = 0; i < Server.socketList.size(); i++) {
							if (s.equals(Server.socketList.get(i))) {
								index = i;
								break;
							}
						}

						// 每有一个新的socket就会执行一次下面代码，获取对应的socket名字和访问对象的名字
						System.out.println("Index处的情况："
								+ Server.socketList.toString());
						System.out.println("Index处的情况："
								+ Server.myName.toString());
						System.out.println("Index处的情况："
								+ Server.friendName.toString());
						String myNameTemp = Server.myName.get(index); // 健壮性差
						String friendTemp = Server.friendName.get(index);

						// 进入聊天界面，就先检查是否有该好友发来的cache信息D:\\myeclipseworkspace
						if (!friendTemp.equals(myNameTemp)) {

							File file = new File(
									"ServerData",
									friendTemp + "_" + myNameTemp + ".txt");
							System.out.println(friendTemp + "_" + myNameTemp
									+ ".txt");
							BufferedReader br_readCache = null;
							if (file.exists()) {
								try {
									br_readCache = new BufferedReader(
											new InputStreamReader(
													new FileInputStream(file)));
									String line = null;
									while ((line = br_readCache.readLine()) != null) {
										Socket s1 = Server.socketList
												.get(Server.socketList.size() - 1);
										OutputStream os = s1.getOutputStream();
										os.write((line + "\n")
												.getBytes("utf-8"));
										
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									try {
										br_readCache.close();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								boolean result = file.delete();
								System.out.println("删除结果" + result);
							}
						}
					}
					// 发送聊天信息进入此条件
					if (account == 1) {
						System.out.println();
						System.out.println("not First");
						System.out.println("Socket" + Server.socketList);
						System.out.println("myName" + Server.myName.toString());
						System.out.println("friendName"
								+ Server.friendName.toString());

						for (int i = 0; i < Server.socketList.size(); i++) {
							if (s.equals(Server.socketList.get(i))) {
								index = i;
								break;
							}
						}
						// 每有一个新的socket就会执行一次下面代码，获取对应的socket名字和访问对象的名字
						String myNameTemp = Server.myName.get(index);// 健壮性差
						String friendTemp = Server.friendName.get(index);
						try {
							boolean flag_SocketList_ExitThePerson = false;
							for (j = 0; j < Server.myName.size(); j++) {
								// System.out.println("Server.friendName.get(i)"+Server.friendName.get(i));
								System.out.println("Server.myName.get(j)"
										+ Server.myName.get(j));
								System.out.println("j" + j);
								System.out.println(friendTemp);
								if (friendTemp.compareTo(Server.myName.get(j)) == 0) { // 一二两个条件要求交叉相等
									if (Server.friendName.get(j).equals(
											myNameTemp)) {
										if (!myNameTemp.equals(friendTemp)) { // 判断是否为自己对话
											System.out.println("进入发送数据");
											Socket s1 = Server.socketList
													.get(j);
											String[] s = content.split("#");
											OutputStream os = s1
													.getOutputStream();
											os.write((s[0] + "\n")
													.getBytes("utf-8"));
											flag_SocketList_ExitThePerson = true;
										}
									}
								}
							}
							// 如果在socketlist中找不到要发送信息的用户则将消息，缓存到服务端的文件，等到socketlist中有该用户是再将消息全部发送过客户端
							if (flag_SocketList_ExitThePerson == false
									&& !friendTemp.equals(myNameTemp)) {
								File file = new File(
										"ServerData",
										myNameTemp + "_" + friendTemp + ".txt");
								OutputStream os_catch = new FileOutputStream(
										file, true);
								String[] s = content.split("#");
								os_catch.write((s[0] + "\r\n")
										.getBytes("utf-8"));
								os_catch.close();
								//假如好友没有在聊天界面但是登录了的话就发送通知D:\\myeclipseworkspace
								boolean flag = true;	//如果为true则说明该用户没有登录，则要进行数据缓寸到文件
								System.out.println("Server.al_myName_notify.toString()"+Server.al_myName_notify.toString());
								for (int x = 0; x < Server.al_myName_notify
										.size(); x++) {
									System.out.println("这条语句在notify发送消息的里面");
									if (Server.al_myName_notify
											.get(x).contains(friendTemp)) {
										Socket s_notify = Server.al_socket_notify.get(x);
										OutputStream os_notify = s_notify.getOutputStream();
										os_notify.write((friendTemp+"#"+myNameTemp+"#"+"Notify"+ "\n").getBytes("utf-8"));
										System.out.println("发送notify消息");
										flag = false;
									}
								}
								//在通知线程无该仁兄则缓存通知D:\\myeclipseworkspace
								if(flag==true){
									File file_notifyCache = new File("ServerData",friendTemp+"_notifyCache.txt");	
									OutputStream os_notifyCache = new FileOutputStream(file_notifyCache,true);
									
									os_notifyCache.write((myNameTemp+"\r\n").getBytes("utf-8"));
									os_notifyCache.close();
									
								}
								//System.out.println("这条语句在notify发送消息的下面两条");
							}
						} catch (SocketException e) {
							e.printStackTrace();
						}
					}
					account = 1;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			//Server.socketList.remove(j);
			//Server.friendName.remove(j);
			//Server.myName.remove(j);
			System.out.println(Server.socketList);
		}
	}

	private String readFromClient() {
		try {											//TODO 想要更改换行发送消息截断的问题，改下面一行代码即可（改客户端已决解问题）
//			String line = "";
//			String temp = "";
//			line = "";
//			while((temp = br.readLine())!=null){
//				line += temp;
//				System.out.println("temp1"+temp);
//				System.out.println("line1: "+line);
//				temp = "";
//			}		
//			System.out.println("temp2"+temp);
//			System.out.println("line2: "+line);
//			return line;
////			System.out.println("line: "+line);
//			System.out.println();
			return br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			Server.socketList.remove(s);
		}	
		return null;
	}		
}
