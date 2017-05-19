package com.dayu.smarttmp;

import android.os.Bundle;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowTmpActivity extends Activity
{
	 ServerSocket server;
	 String[] shebei;
	 String[] shuzhi;
	 static Object wait = "1";
	 private static DatagramSocket ds = null;  
	 private InetSocketAddress inetSocketAddress = null;  
	    private byte[] msgRcv = new byte[1024];  
	    MulticastSocket ms = null;
	    private DatagramPacket dpRcv = null,dpSend = null;  

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_tmp);
		
		shebei = new String[2];
		shuzhi = new String[2];
		
		Thread th_server = new Thread( new Runnable()
		{
			public void run()
			{
				init_server();
			}
		});
		
		// th_server.start();
		
		Thread th_udp_server = new Thread( new Runnable()
		{
			public void run()
			{
				try
				{
					init_UDP_server();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		th_udp_server.start();
		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_tmp, menu);
		return true;
	}
	
	private void init_UDP_server() throws IOException
	{
		
		// inetSocketAddress = new InetSocketAddress("192.168.43.1", 10086);  
//		ds = new DatagramSocket(inetSocketAddress);
		try
		{
			InetAddress groupAddress;
			groupAddress = InetAddress.getByName("127.0.0.1");
			 Log.e("tmp", "UDP监听中");  
			ms = new MulticastSocket(8096);
			
			ms.joinGroup(groupAddress);
			
		
		} 	catch (IOException e) {  
                e.printStackTrace();  
            }  
		 dpRcv = new DatagramPacket(msgRcv, msgRcv.length);  
         Log.e("tmp", "UDP服务器已经启动");  
         while (true) {  
             try {  
                
                 if(ms!=null)
                  {ms.receive(dpRcv);  
   
                 String rcving = new String(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());  
                 Log.e("tmp", "收到信息：" + rcving);  
                 int c = 0;
				 if((c = rcving.indexOf('?'))>=0)
				 {
					 if(rcving.indexOf('&',c)>=0)
					 {
						String[] params =  rcving.substring(c+1).split("&");
						for(int i=0;i<params.length;i++)
						{
							String[] tp_params = params[i].split("=");
						synchronized (wait)
						{
					       shebei[i] =	tp_params[1];
						   shuzhi[i] =   tp_params[1];
						} 
						  Log.e("tmp", shebei[i] + " " +shuzhi[i]);
						}
						
						runOnUiThread( new Runnable()
						 {
							public void run()
							{
								if(shebei[0].equalsIgnoreCase("RFV"))
								{
									    TextView tv_showtmp = (TextView)findViewById(R.id.showtmp);
								    	//tv_showtmp.setText("设备1："+shebei[0]+" 温度："+shuzhi[1]);
									    tv_showtmp.setText(shuzhi[1]);
									 //   LinearLayout show_left = (LinearLayout)findViewById(R.id.show_left);
									 //   show_left.setBackgroundColor(0xff00ff00);
									    Log.e("tmp", "1 OK");
								}
								 else
								 {	  TextView tv_shebei2 = (TextView)findViewById(R.id.shebei2);
								     tv_shebei2.setText(shuzhi[1]);
									  // tv_shebei2.setText("设备2："+shebei[0]+" 温度："+shuzhi[1]);
								};
						    	
							}
						 });
					 }
				 }
                  }
             }catch (IOException e) {  
                 e.printStackTrace();  
             }  
         }
	}

	private void init_server()
	{
		try
		{
			server = new ServerSocket(8096);
			Log.e("tmp", "server start");
			while(true)
			{
				Socket msSocket = server.accept();
				msSocket.setSoTimeout(2000);
				msSocket.setSoLinger(true, 5);
				Log.e("tmp", "new client");
				client mclient = new client(msSocket);
				mclient.start();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
   	class client extends Thread
	{
		Socket msSocket;
		public client(Socket Socket)
		{
			msSocket = Socket;
			
		}

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			super.run();
			  DataInputStream fromclient = null;
			try
			{
				fromclient = new DataInputStream(msSocket.getInputStream());
			/*
		     while(!msSocket.isClosed())	
			  {
		    	 
				  int cb = fromclient.available();
				 if (cb > 0)
				 { byte readbyte[] = new byte[cb];
					 fromclient.read(readbyte);
					 Log.e("tmp","收到数据长度为："+ readbyte.length);
					 String str = new String(readbyte);
					 Log.e("tmp", str);
				 } 
				 msSocket.close();
			  }  */
				
				while(msSocket!=null)
				{  int cb = fromclient.available();
					 if (cb > 0)
					 { byte readbyte[] = new byte[cb];
						 fromclient.read(readbyte);
						 Log.e("tmp","收到数据长度为："+ readbyte.length);
						 final String str = new String(readbyte);
						 int c = 0;
						 if((c = str.indexOf('?'))>=0)
						 {
							 if(str.indexOf('&',c)>=0)
							 {
								String[] params =  str.substring(c+1).split("&");
								for(int i=0;i<params.length;i++)
								{
									String[] tp_params = params[i].split("=");
								synchronized (wait)
								{
							       shebei[i] =	tp_params[1];
								   shuzhi[i] =   tp_params[1];
								} 
								  Log.e("tmp", shebei[i] + " " +shuzhi[i]);
								}
								
								runOnUiThread( new Runnable()
								 {
									public void run()
									{
										if(shebei[0].equalsIgnoreCase("RFV"))
										{
											    TextView tv_showtmp = (TextView)findViewById(R.id.showtmp);
										    	//tv_showtmp.setText("设备1："+shebei[0]+" 温度："+shuzhi[1]);
											    tv_showtmp.setText(shuzhi[1]);
											    LinearLayout show_left = (LinearLayout)findViewById(R.id.show_left);
											 //   show_left.setBackgroundColor(0xff00ff00);
											    Log.e("tmp", "1 OK");
										}
										 else
										 {	  TextView tv_shebei2 = (TextView)findViewById(R.id.shebei2);
										     tv_shebei2.setText(shuzhi[1]);
											  // tv_shebei2.setText("设备2："+shebei[0]+" 温度："+shuzhi[1]);
										};
								    	
									}
								 });
								 Log.e("tmp", str);
							 }
						 }
						 
						Thread.sleep(500);
						msSocket.close();
						msSocket = null;
					 } 
		       	}
			} catch (IOException e)
			{
				Log.e("tmp", "连接关闭");
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
