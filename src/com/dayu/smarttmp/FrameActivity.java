package com.dayu.smarttmp;

import android.os.Bundle;
import android.util.Log;
import android.util.MonthDisplayHelper;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Text;

import com.dayu.smarttmp.ShowTmpActivity.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FrameActivity extends Activity
{
	static LinearLayout rel_showtime1;
	static LinearLayout rel_showtmp2;
	static TextView txt_showtime,txt_showtime2;
	static TextView txt_showdate;
	static TextView txt_shownongli;
	static TextView txt_week;
	 boolean change_face = true;
	 static SimpleDateFormat nowdate;
	 static SimpleDateFormat nowtime;
	 static SimpleDateFormat nowweek;
	 static Date date;
	 ServerSocket server;
	 String[] shebei;
	 String[] shuzhi;
	 static Object wait = "1";
	 String[] big_month ;
	 String txt_BT_temp;
	 static dayinfo c_day ;
	 static  ImageView img_shineistatus;
	 static  ImageView img_shiwaistatus;
	 long tongbu_nei;
	 long tongbu_wai;
	 private static int BatteryN=0,BatteryV=0,BatteryT=0;
	 private static DatagramSocket ds = null;  
	 private InetSocketAddress inetSocketAddress = null;  
	    private byte[] msgRcv = new byte[1024];  
	    private DatagramPacket dpRcv = null,dpSend = null;  
	    MulticastSocket ms = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		rel_showtime1 = (LinearLayout)findViewById(R.id.rel_1);
		rel_showtmp2 = (LinearLayout)findViewById(R.id.rel_2);
		txt_showtime = (TextView)findViewById(R.id.rel_1_showtime);
		txt_showdate= (TextView)findViewById(R.id.rel_1_showdate);
		txt_showtime2 = (TextView)findViewById(R.id.rel_2_showtime);
		txt_shownongli = (TextView)findViewById(R.id.rel_1_nongli);
		big_month = getResources().getStringArray(R.array.big_month);
		img_shineistatus = (ImageView)findViewById(R.id.rel_2_img_shinei);
		img_shiwaistatus = (ImageView)findViewById(R.id.rel_2_img_shiwai);
		txt_week = (TextView)findViewById(R.id.rel_1_week);
		
		IntentFilter intentfilter1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(br1, intentfilter1);
		
		Thread t_Dochange_face = new Thread_dochange_face();
		t_Dochange_face.start();
		Thread t_Dochange_time = new Thread_dochange_time();
		t_Dochange_time.start();
		
		shebei = new String[2];
		shuzhi = new String[2];
		
	/*	Thread th_server = new Thread( new Runnable()
		{
			public void run()
			{
				init_server();
			}
		});
		
		th_server.start();*/
		
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
	
	
	
	private void init_UDP_server() throws IOException
	{
		
		// inetSocketAddress = new InetSocketAddress("192.168.43.1", 10086);  
//		ds = new DatagramSocket(inetSocketAddress);
		try
		{
			InetAddress groupAddress;
			groupAddress = InetAddress.getByName("127.0.0.1");
			 Log.e("tmp", "UDP监听中");  
			ms = new MulticastSocket(10086);
			
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
								if(shebei[0].equalsIgnoreCase("nei"))
								{
									    TextView tv_shownei = (TextView)findViewById(R.id.rel_2_showtmp_nei);
								    	//tv_showtmp.setText("设备1："+shebei[0]+" 温度："+shuzhi[1]);
									    tv_shownei.setText(shuzhi[1]);
									//    LinearLayout show_left = (LinearLayout)findViewById(R.id.show_left);
									 //   show_left.setBackgroundColor(0xff00ff00);
									    img_shineistatus.setImageResource(android.R.drawable.presence_online);
									    tongbu_nei = System.currentTimeMillis();
									    Log.e("tmp", "1 OK");
								}
								 else
								 {	  TextView tv_showwai = (TextView)findViewById(R.id.rel_2_showtmp_wai);
								      tv_showwai.setText(shuzhi[1]);
									  // tv_shebei2.setText("设备2："+shebei[0]+" 温度："+shuzhi[1]);
								      img_shiwaistatus.setImageResource(android.R.drawable.presence_online);
								      tongbu_wai = System.currentTimeMillis();
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
	
	BroadcastReceiver br1 = new BroadcastReceiver()
	{	
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
		    
			BatteryN = intent.getIntExtra("level", 0);    //目前电量  
			BatteryV = intent.getIntExtra("voltage", 0);  //电池电压  
			BatteryT = intent.getIntExtra("temperature", 0);  //电池温度  
			
			if (BatteryT>30&&BatteryT<=100)
			{
				BatteryT = BatteryT - 20;
			} else if(BatteryT>100&&BatteryT<=200)
			{
				BatteryT = BatteryT - 30;
			}else if(BatteryT>200&&BatteryT<=300)
			{
				BatteryT = BatteryT - 40;
			}else if(BatteryT>300&&BatteryT<=400)
			{
				BatteryT = BatteryT - 50;
			}else if(BatteryT>400)
			{
				BatteryT = BatteryT - 60;
			}
			
			txt_BT_temp = String.valueOf(BatteryT);
		//	Log.e("tmp", "BatteryT"+txt_BT_temp);
			
			if (txt_BT_temp.length()>0)
			{
				if (txt_BT_temp.length()==3)
				{
					char[] temp = txt_BT_temp.toCharArray();
					txt_BT_temp = String.valueOf(temp[0])+String.valueOf(temp[1])+"."+String.valueOf(temp[2]);
					//Log.e("tmp", "temp[0]"+temp[0]+",temp[1]"+temp[1]+",temp[2]"+temp[2]);
				}
				if (txt_BT_temp.length()==2)
				{
					char[] temp = txt_BT_temp.toCharArray();
					txt_BT_temp = String.valueOf(temp[0])+"."+String.valueOf(temp[1]);
					//Log.e("tmp", "temp[0]"+temp[0]+",temp[1]"+temp[1]+",temp[2]"+temp[2]);
				}
				if (txt_BT_temp.length()==1)
				{
					char[] temp = txt_BT_temp.toCharArray();
					txt_BT_temp = "0."+String.valueOf(temp[0]);
					//Log.e("tmp", "temp[0]"+temp[0]+",temp[1]"+temp[1]+",temp[2]"+temp[2]);
				}
			}
		}
		
	};
	
	class Thread_dochange_time extends Thread
	{

		@Override
		public void run()
		{
			while(true)
			{
				
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						nowdate = new SimpleDateFormat("yyyy 年  M 月 d 日");
						nowtime = new SimpleDateFormat("HH:mm");
					//	nowweek = new SimpleDateFormat("E");
						long UTCtime = System.currentTimeMillis();
						date = new Date(UTCtime);
						String timestr = nowdate.format(date);
						txt_showdate.setText(timestr);
					     timestr = nowtime.format(date);
					     
					     char[] temp = timestr.toCharArray();
					     if (temp[0]=='0')
							{
					    	 timestr = timestr.substring(1, timestr.length());
							}
					     
					     
						txt_showtime.setText(timestr);
						txt_showtime2.setText(timestr);
						String year = new SimpleDateFormat("yyyy").format(date);
						String month = new SimpleDateFormat("MM").format(date);
						String day = new SimpleDateFormat("dd").format(date);
						String week = new SimpleDateFormat("E").format(date);
						c_day = new dayinfo( Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
						txt_shownongli.setText(big_month[c_day.get_todayis()]);
						txt_week.setText(week.replace("周", "星期"));
						c_day = null;
					}
				});
				
				try
				{
					sleep(1000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
			
		}
		
	}
	
	class Thread_dochange_face extends Thread
	{

		@Override
		public void run()
		{
		 while(true)
		 { 
			Log.e("tmp", "in loop"); 
			
			try
			{
				Thread.sleep(30000);
				change_face = !change_face;
				
			 runOnUiThread(new Runnable()
			 {
				public void run()
				{
					 if (System.currentTimeMillis() - tongbu_nei > 180*1000)
					 {
						 img_shineistatus.setImageResource(android.R.drawable.presence_invisible);
						 TextView tv_shownei = (TextView)findViewById(R.id.rel_2_showtmp_nei);
					    	//tv_showtmp.setText("设备1："+shebei[0]+" 温度："+shuzhi[1]);
						//    tv_shownei.setText("--.-");
						 if (BatteryT!=0)
						{
							 tv_shownei.setText(txt_BT_temp);
						}
					 }
					 
					 if (System.currentTimeMillis() - tongbu_wai > 180*1000)
					 {
						 img_shiwaistatus.setImageResource(android.R.drawable.presence_invisible);
						 TextView tv_showwai = (TextView)findViewById(R.id.rel_2_showtmp_wai);
					      tv_showwai.setText("--.-");
					 }
				 
				}
			});	
				
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			runOnUiThread(new Runnable()
			{
			  
				public void run()
				{
					Log.e("tmp", "change face"); 
					if (change_face)
					{rel_showtime1.setVisibility(RelativeLayout.INVISIBLE);
					rel_showtmp2.setVisibility(RelativeLayout.VISIBLE);}
					else
					{rel_showtime1.setVisibility(RelativeLayout.VISIBLE);
					rel_showtmp2.setVisibility(RelativeLayout.INVISIBLE);};
				}
			  });
			
			super.run();
		 }
		}
		
	}
	
	private void init_server()
	{
		try
		{
			server = new ServerSocket(10086);
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
										if(shebei[0].equalsIgnoreCase("nei"))
										{
											    TextView tv_shownei = (TextView)findViewById(R.id.rel_2_showtmp_nei);
										    	//tv_showtmp.setText("设备1："+shebei[0]+" 温度："+shuzhi[1]);
											    tv_shownei.setText(shuzhi[1]);
											//    LinearLayout show_left = (LinearLayout)findViewById(R.id.show_left);
											 //   show_left.setBackgroundColor(0xff00ff00);
											    img_shineistatus.setImageResource(android.R.drawable.presence_online);
											    tongbu_nei = System.currentTimeMillis();
											    Log.e("tmp", "1 OK");
										}
										 else
										 {	  TextView tv_showwai = (TextView)findViewById(R.id.rel_2_showtmp_wai);
										      tv_showwai.setText(shuzhi[1]);
											  // tv_shebei2.setText("设备2："+shebei[0]+" 温度："+shuzhi[1]);
										      img_shiwaistatus.setImageResource(android.R.drawable.presence_online);
										      tongbu_wai = System.currentTimeMillis();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.frame, menu);
		return true;
	}

}
