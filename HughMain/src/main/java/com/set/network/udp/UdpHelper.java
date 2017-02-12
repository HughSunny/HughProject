package com.set.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.set.app.MainApp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * UdpHelper帮助类
 */
public class UdpHelper implements Runnable {
	public Boolean IsThreadDisable = false;// 指示监听线程是否终止
	private static WifiManager.MulticastLock lock;
	InetAddress mInetAddress;
	// UDP服务器监听的端口
	int port = 8903;
	static int server_port = 8904;

	public UdpHelper(WifiManager manager) {
		this.lock = manager.createMulticastLock("UDPwifi");
	}

	public void startListen() {
		// 接收的字节大小，客户端发送的数据不能超过这个大小
		byte[] message = new byte[100];
		try {
			// 建立Socket连接
			DatagramSocket datagramSocket = new DatagramSocket(port);
			datagramSocket.setBroadcast(true);
			DatagramPacket datagramPacket = new DatagramPacket(message,
					message.length);
			try {
				while (!IsThreadDisable) {
					// 准备接收数据
					Log.d("UDP Demo", "准备接受");
					this.lock.acquire();
					datagramSocket.receive(datagramPacket);
					String strMsg = new String(datagramPacket.getData()).trim();
					Log.d("UDP Demo", datagramPacket.getAddress()
							.getHostAddress().toString()
							+ ":" + strMsg);
					this.lock.release();
				}
			} catch (IOException e) {// IOException
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

	public static void send(String message) {
		message = (message == null ? "Hello IdeasAndroid!" : message);
		Log.d("UDP Demo", "UDP发送数据:" + message);
		DatagramSocket s = null;
		try {
			s = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress local = null;
		try {
			local = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int msg_length = message.length();
		byte[] messageByte = message.getBytes();
		DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,
				server_port);
		try {

			s.send(p);
			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		startListen();
	}

	public String getLocalHostIp() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress()
//							&& InetAddressUtils.isIPv4Address(ip.getHostAddress())
							) {
						return ipaddress = "本机的ip是" + "：" + ip.getHostAddress();
					}
				}

			}
		} catch (SocketException e) {
			Log.e("feige", "获取本地ip地址失败");
			e.printStackTrace();
		}
		return ipaddress;
	}

	// 得到本机Mac地址
	public String getLocalMac() {
		String mac = "";
		// 获取wifi管理器
		WifiManager wifiMng = (WifiManager) MainApp.getInstance().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfor = wifiMng.getConnectionInfo();
		mac = "本机的mac地址是：" + wifiInfor.getMacAddress();
		return mac;
	}

}
