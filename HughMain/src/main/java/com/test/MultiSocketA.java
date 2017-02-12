package com.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.set.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * ClassName:MultiSocketA Function: TODO ADD FUNCTION
 * 
 * @author xxshi
 * @version
 * @Date 2015-4-17 下午4:15:43
 */
public class MultiSocketA extends Activity implements OnClickListener {
	private static final String TAG = "MultiSocketA";
	private Handler mHandler;
	private Button sendBtn;
	private EditText edit;
	private TextView rece_txt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mutisocket);
		sendBtn = (Button)findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(this);
		edit = (EditText)findViewById(R.id.send_edit);
		rece_txt = (TextView)findViewById(R.id.rece_txt);
		// 通过Handler启动线程
		HandlerThread handlerThread = new HandlerThread("MultiSocketA");
		handlerThread.start();
		mHandler = new Handler(handlerThread.getLooper());
	}
	
	@Override
	public void onClick(View v) {
		mHandler.post(mRunnable);
	}
	
	private Runnable mRunnable = new Runnable() {
		public void run() {
			Log.v(TAG, "run...");
			try {
				sendMultiBroadcast();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private int port = 9988;
	private void sendMultiBroadcast() throws IOException {
		Log.v(TAG, "sendMultiBroadcast...");
		// 在多播中设置了TTl值（Time to live），每一个ip数据报文中都包含一个TTL，每当有路由器转发该报文时，TTL减1，知道减为0时，生命周期结束，报文即时没有到达目的地，也立即宣布死亡。当然在Java中，ttl并不是十分准确的，曾经在一本书中介绍过报文的传播距离是不会超过ttl所设置的值的
		int TTLTime = 4;  
		/*
		 * 实现多点广播时，MulticastSocket类是实现这一功能的关键，
		 * 当MulticastSocket把一个DatagramPacket发送到多点广播IP地址，
		 * 该数据报将被自动广播到加入该地址的所有MulticastSocket。MulticastSocket类既可以将数据报发送到多点广播地址，
		 * 也可以接收其他主机的广播信息
		 */
		MulticastSocket socket = new MulticastSocket(port);
		// IP协议为多点广播提供了这批特殊的IP地址，这些IP地址的范围是224.0.0.0至239.255.255.255
		InetAddress address = InetAddress.getByName("224.0.0.1");
		socket.setTimeToLive(TTLTime);  
		/*
		 * 创建一个MulticastSocket对象后，还需要将该MulticastSocket加入到指定的多点广播地址，
		 * MulticastSocket使用jionGroup()方法来加入指定组；使用leaveGroup()方法脱离一个组。
		 */
		socket.joinGroup(address);

		/*
		 * 在某些系统中，可能有多个网络接口。这可能会对多点广播带来问题，这时候程序需要在一个指定的网络接口上监听，
		 * 通过调用setInterface可选择MulticastSocket所使用的网络接口；
		 * 也可以使用getInterface方法查询MulticastSocket监听的网络接口。
		 */
		// socket.setInterface(address);

		DatagramPacket packet;
//		// 发送数据包
		Log.v(TAG, "send packet");
		byte[] buf = "Hello I am MultiSocketA".getBytes();
		packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);
		
		receiveMutiBroadcast();
		
		// 退出组播
		socket.leaveGroup(address);
		socket.close();
	}
	
	static byte[] rev = new byte[512];
	
	private void receiveMutiBroadcast() {
		Log.v(TAG, "receiver packet");
		MulticastSocket socket;
		DatagramPacket packet = null ;
		try {
			socket = new MulticastSocket(port);
			packet = new DatagramPacket(rev, rev.length);
			socket.receive(packet);
			Log.v(TAG, "get data = " + new String(packet.getData()).trim()); // 不加trim，则会打印出512个byte，后面是乱码
		} catch (IOException e) {
			e.printStackTrace();
		}
		Message message = Message.obtain();
		message.what = 0;
		message.obj = new String(packet.getData()).trim();
		handler.sendMessage(message);
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
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String rece = msg.obj.toString();
			rece_txt.setText(rece);
		}
	};


}
