package com.set.network.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * ClassName:UDPMulticastServer
 * Function: TODO ADD FUNCTION
 *
 * @author   wl
 * @version  
 * @Date	 2015-4-20		下午3:37:27
 */
public class UDPMulticastServer {
	final static int RECEIVE_LENGTH = 1024;
	static final String multicastHost = "224.0.0.1";
	static final int localPort = 9998;
	public void main(String[] args) throws Exception {
		InetAddress receiveAddress = InetAddress.getByName(multicastHost);
		if (!receiveAddress.isMulticastAddress()) {// 测试是否为多播地址
			throw new Exception("请使用多播地址");
		}
		int port = localPort;
		MulticastSocket receiveMulticast = new MulticastSocket(port);
		receiveMulticast.joinGroup(receiveAddress);
		DatagramPacket dp = new DatagramPacket(new byte[RECEIVE_LENGTH],
				RECEIVE_LENGTH);
		receiveMulticast.receive(dp);
		System.out.println(new String(dp.getData()).trim());
		receiveMulticast.close();
	}

	
}

