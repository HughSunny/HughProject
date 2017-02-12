package com.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.set.R;
import com.set.R.id;
import com.set.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ClassName:UdpBroadcastActivtiy
 * Function: TODO ADD FUNCTION
 *
 * @author   wl
 * @version  
 * @Date	 2015-4-23		下午3:52:44
 */
public class UdpBroadcastActivtiy extends Activity implements OnClickListener{
	private static final String TAG = "UdpBroadcastActivtiy";
	private NonUIHandler mHandler;
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
		mHandler = new NonUIHandler(handlerThread.getLooper());
		new Thread(mRunnable).start();
	}
	
	@Override
	public void onClick(View v) {
		mHandler.sendEmptyMessage(1);
	}
	
	private class  NonUIHandler extends Handler {
		public NonUIHandler(Looper loop){
			super(loop);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			sendBroadcast(edit.getText().toString());
		}
	}
	
	
	
	
	private Runnable mRunnable = new Runnable() {
		public void run() {
			Log.v(TAG, "run...");
//			sendBroadcast("");
			while(isRun) {
				Log.v(TAG, "Receive   ====>");
				receiveBroadcast();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private boolean  isRun = true;
	private int MAX_SIZE = 1024;
	DatagramSocket udpsocket = null;
	
	DatagramPacket sendPacket;
	DatagramPacket localPacket;
	
	DatagramPacket recePacket;
	private static int RECE_LENGTH = 4096 ;
	static byte[] recvBuf = new byte[RECE_LENGTH];
	static byte[] sendBuf = new byte[RECE_LENGTH];
	private static String UDP_HOST = "172.27.35.255";
	private int port = 10088;
	private int  SEND_PORT = 7800;
	
	
	private void sendBroadcast(String msgg) {
		byte[] send = "this is the first udp broadcast".getBytes();
		if (msgg != null && !msgg.equals("") ) {
			send = msgg.getBytes();
		}
		int sendLen = send.length;
		System.arraycopy(send, 0, sendBuf, 0, send.length);
		
		sendPacket = new DatagramPacket(sendBuf, sendLen);
		localPacket = new DatagramPacket(sendBuf, sendLen);
		try {
			if (udpsocket == null) {
				udpsocket = new DatagramSocket(port);
			}
			String broHost = getBroadcast();
			if (broHost != null && !broHost.equals("")) {
				Log.e(TAG, "BROADCAST HOST =====> " + broHost);
				sendPacket.setAddress(InetAddress.getByName(broHost));
			}else {
				sendPacket.setAddress(InetAddress.getByName(UDP_HOST));
			}
			
			sendPacket.setPort(SEND_PORT);
//			localPacket.setAddress(InetAddress.getByName("172.0.0.1"));
//			localPacket.setPort(SEND_PORT);
			udpsocket.send(sendPacket);
//			udpsocket.send(localPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getBroadcast() throws SocketException {  
        System.setProperty("java.net.preferIPv4Stack", "true");  
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {  
            NetworkInterface ni = niEnum.nextElement();  
            if (!ni.isLoopback()) {  
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {  
                    if (interfaceAddress.getBroadcast() != null) {  
                        return interfaceAddress.getBroadcast().toString().substring(1);  
                    }  
                }  
            }  
        }  
        return null;  
    }  
	private void receiveBroadcast() {
		try {
			if(udpsocket == null) {
				udpsocket = new DatagramSocket(SEND_PORT);
			}
			recePacket  = new DatagramPacket(recvBuf, RECE_LENGTH);
//			recePacket.setPort(SEND_PORT);
			udpsocket.receive(recePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] receive = recePacket.getData();
		if (receive == null) {
			Log.w(TAG, "receive == NULL");
			return;
		}
		Message message = Message.obtain();
		message.what = 0;
		message.obj = new String(recePacket.getData()).trim();
		handler.sendMessage(message);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String rece = msg.obj.toString();
			Log.i(TAG, "RECE == "  + rece);
			rece_txt.setText(rece_txt.getText() +"\r\n"+ rece);
		}
	};

	
	
}

