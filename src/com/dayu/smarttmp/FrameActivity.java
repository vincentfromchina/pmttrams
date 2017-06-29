package com.dayu.smarttmp;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.MonthDisplayHelper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.SensorManager;
import android.inputmethodservice.Keyboard.Key;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
	 static boolean isdebug = true;
	 private static int BatteryN=0,BatteryV=0,BatteryT=0;
	 private static DatagramSocket ds = null;  
	 private InetSocketAddress inetSocketAddress = null;  
	    private byte[] msgRcv = new byte[1024];  
	    private DatagramPacket dpRcv = null,dpSend = null;  
	    MulticastSocket ms = null;
	    private static final int DOWNLOAD_ING = 37, DOWNLOAD_FINISH = 39;
		private static String mSavepath = "", apkurl = "", apkname = "", apkversion = "";
		static long filesize = 0;
		static private String owner="";
		final static String TAG = "smarttemp";
		public static long change_time = 30000;
		 static public ProgressBar mProgressBar;
			HttpURLConnection urlConn = null;  
			static boolean cancelupdate = false;
			private static int progessperct = 0;
			static boolean showtemp = true;
			Thread t_Dochange_face ;
			

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Long UTCtime = System.currentTimeMillis();
		Long binger = 1483200000000L;
		Long ender =  1640966400000L;
		if ( (UTCtime.longValue()-binger.longValue()) < 0 || (UTCtime - ender) > 0)
		{
			timeerrordialog();
		}else
		{
			 SharedPreferences preferences = getSharedPreferences("change_time",MODE_MULTI_PROCESS );   
		        long rec = preferences.getLong("change_time", 30000);    
		        
		        change_time = rec;
		        
		       preferences = getSharedPreferences("showtemp",MODE_MULTI_PROCESS );   
		        boolean rec_showtemp = preferences.getBoolean("showtemp", true);    
		        
		        showtemp = rec_showtemp;
		        
		        
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
		
	    t_Dochange_face = new Thread_dochange_face();
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
		
		
		
		/*
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
		*/
		
		//检查软件版本
				checkupdate ck = new checkupdate();
				ck.start();
		}
	}
	
	 private void timeerrordialog()
	 {
		 final Builder adAlertDialog = new Builder(FrameActivity.this);
		 adAlertDialog.setMessage("请将系统设置于2017-01-01之后");
		 adAlertDialog.setTitle("时间错误");
		 adAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				finish();
			}
		});
		 
		 adAlertDialog.setOnKeyListener(new OnKeyListener()
		{
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				
				return true;
			}
		});
		 
		 adAlertDialog.show();
	 }
	

	public class checkupdate extends Thread
	 {

		@Override
		public void run()
		{
			 String resultData=""; 
			 
			 try
			{
				Thread.sleep(12000);
			} catch (InterruptedException e2)
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			  if ( HttpURLConnection_update()==200)
				{
					try
					{
						InputStreamReader in;
						in = new InputStreamReader(urlConn.getInputStream());
						BufferedReader buffer = new BufferedReader(in);
						String inputLine = null;
						while (((inputLine = buffer.readLine()) != null))
						{
							resultData += inputLine;
						}
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (FrameActivity.isdebug)
						if (FrameActivity.isdebug) Log.e(TAG, "resultData:" + resultData);

					JSONObject mJsonObject;
					try
					{
						mJsonObject = new JSONObject(resultData);
						 apkname = mJsonObject.getString("apkname");
						 apkurl = mJsonObject.getString("apkurl");
						 apkversion = mJsonObject.getString("apkversion");
						 
						 try
						{
							int cunversion = getApplicationContext().getPackageManager().getPackageInfo(FrameActivity.this.getPackageName(), 0).versionCode;
							if (cunversion < Integer.valueOf(apkversion))
							{
								shownoticedialog();
							}
						} catch (NameNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			super.run();
		}
		
	 }
	 
	 private void shownoticedialog()
	 {
		 final Builder adAlertDialog = new Builder(FrameActivity.this);
		 adAlertDialog.setMessage("当前有新版本，需要更新");
		 adAlertDialog.setTitle("软件更新");
		 adAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showdownloaddialog();
			}
		});
		 
		 runOnUiThread( new Runnable()
		{
			public void run()
			{
				 Dialog noticedialog = adAlertDialog.create();
				 noticedialog.show();
			}
		});
		
	 }
	 
	 private void showdownloaddialog()
	 {
		 Builder adAlertDialog = new Builder(FrameActivity.this);
		 final LayoutInflater mInflater  = LayoutInflater.from(getApplicationContext());
		View v = mInflater.inflate(R.layout.updatedialog, null);
		mProgressBar = (ProgressBar) v.findViewById(R.id.update_progressBar);
		
		 adAlertDialog.setView(v);
		 adAlertDialog.setTitle("下载进度");
		 adAlertDialog.setPositiveButton("取消", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				cancelupdate = true;
			}
		});
		 
		 Dialog downloaddialog = adAlertDialog.create();
		 downloaddialog.show();
		 
		 Downloadapk mdDownloadapk = new Downloadapk();
		 mdDownloadapk.setcontext(downloaddialog);
		 mdDownloadapk.start();
	 }
		 
private class Downloadapk extends Thread
	  {
			 Dialog dialog;
			 public void setcontext(Dialog dialog)
			 {
				 this.dialog = dialog;
			 }
			 
			@Override
			public void run()
			{
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					String sdpath = Environment.getExternalStorageDirectory()+"/";
					mSavepath = sdpath +"download/";
					if (FrameActivity.isdebug) Log.e(TAG, mSavepath);
					
					try
					{
						URL downurl = new URL(apkurl);
						if (isdebug) Log.e(TAG, downurl.toString());
						HttpURLConnection conn = (HttpURLConnection) downurl.openConnection();
						conn.connect();
						int apklength = conn.getContentLength();
						InputStream is = conn.getInputStream();
						
						File file = new File(mSavepath);
						if (!file.exists())
						{
							file.mkdir();
						}
						
						File apkfile = new File(apkname);
						FileOutputStream fos = new FileOutputStream(mSavepath+apkfile);
						int count = 0;
						byte buf[] = new byte[1024];
						do
						{
							int numred = is.read(buf);
							count += numred;
							progessperct =(int) (((float)count/apklength)*100);
							mHandler.sendEmptyMessage(DOWNLOAD_ING);
							if(numred<=0)
							{
								mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
								dialog.dismiss();
								break;
							}
							
							fos.write(buf,0,numred);
							
						} while (!cancelupdate);
						if (fos!=null) fos.close();
						if (is!=null) is.close();
						if (conn!=null) conn = null;
						
					} catch (MalformedURLException e)
					{
						Toast.makeText(FrameActivity.this, "下载失败", Toast.LENGTH_LONG).show();
						dialog.dismiss();
						e.printStackTrace();
					} catch (IOException e)
					{
						Toast.makeText(FrameActivity.this, "下载失败", Toast.LENGTH_LONG).show();
						dialog.dismiss();
						e.printStackTrace();
					}
				}
					
					super.run();
			}
	}

	private void updateprogessbar(int process)
		{
			mProgressBar.setProgress(process);
		}
		/**
			 * 比较传进来的时间与实际时间的大小，如果当前时间比参数大，返回true，小则返回false
			 * @param time
			 * @return boolean
			 */
	public boolean bijiaotime(String time1,String time2) //比较当前系统时间与传进来的时间大小
			{
				   
				   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    
				   Date dat1,dat2 = null;
				try
				{
					dat1 = df.parse(time1);
					dat2 = df.parse(time2);
					
					if ((dat1.getTime()-dat2.getTime())>0)
					   {
						   return true;
					   }
					   else {
						  return false;
					   }
				} catch (ParseException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				return false;
 }
			
  private int HttpURLConnection_update()
			 {  
				 int recode = 0;
			        try{  
			            //通过openConnection 连接  
			            URL url = new java.net.URL(getResources().getString(R.string.url)+"/smarttemp/updateversion.html");  
			            urlConn=(HttpURLConnection)url.openConnection();  
			            //设置输入和输出流   
			            urlConn.setDoOutput(true);  
			            urlConn.setDoInput(true);  
			              
			            urlConn.setRequestMethod("POST");  
			            urlConn.setUseCaches(false);  
			            urlConn.setReadTimeout(3000);
			            urlConn.setConnectTimeout(3000);
			            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的    
			            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");    
			            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，  
			            // 要注意的是connection.getOutputStream会隐含的进行connect。    
			            urlConn.connect();  
			            //DataOutputStream流  
			            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());  
			            //要上传的参数  
			            String content = "owner=" + URLEncoder.encode(owner, "GBK");   
			            //将要上传的内容写入流中  
			            out.writeBytes(content);     
			            //刷新、关闭  
			            out.flush();  
			            out.close();     

			            recode = urlConn.getResponseCode();
			            
			            if (FrameActivity.isdebug) Log.e(TAG, String.valueOf(recode));
			        }catch(Exception e){  
			            
			            e.printStackTrace();  
			        }  
			        
			        return recode;
 }  		
  
  private Handler mHandler = new Handler()
  {

	@Override
	public void handleMessage(Message msg)
	{
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		if (msg!=null)
		{
			switch (msg.what)
			{
		   /*	
			case BINDPHONE:
				setserial();
				break;
			case REFRESHGPS:
				updatemap();
				break;
			case doplay:
				doplay(msg.arg1);
				break;
			*/
			case DOWNLOAD_ING:
				updateprogessbar(FrameActivity.progessperct);
				break;
			case DOWNLOAD_FINISH:
				installapk();
				break;
			default:
				break;
			}
		}
	}

   };
   
private void installapk()
	{
		File apkfile = new File(mSavepath,apkname);
		if (!apkfile.exists())
		{
			return;
		}
		Intent ins = new Intent(Intent.ACTION_VIEW);
		ins.setDataAndType(Uri.parse("file://"+apkfile), "application/vnd.android.package-archive");
		startActivity(ins);
}
	
	
	
	private void init_UDP_server() throws IOException
	{
		
		// inetSocketAddress = new InetSocketAddress("192.168.43.1", 10086);  
//		ds = new DatagramSocket(inetSocketAddress);
		try
		{
			InetAddress groupAddress;
			groupAddress = InetAddress.getByName("127.0.0.1");
			 Log.e(TAG, "UDP监听中");  
			ms = new MulticastSocket(10086);
			
			ms.joinGroup(groupAddress);
			
		
		} 	catch (IOException e) {  
                e.printStackTrace();  
            }  
		 dpRcv = new DatagramPacket(msgRcv, msgRcv.length);  
         Log.e(TAG, "UDP服务器已经启动");  
         while (true) {  
             try {  
                
                 if(ms!=null)
                  {ms.receive(dpRcv);  
   
                 String rcving = new String(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());  
                 Log.e(TAG, "收到信息：" + rcving);  
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
						  Log.e(TAG, shebei[i] + " " +shuzhi[i]);
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
									    Log.e(TAG, "1 OK");
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
			Log.e(TAG, "in loop"); 
			
			if (showtemp)
			{
				
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
			 
			
			runOnUiThread(new Runnable()
			{
			  
				public void run()
				{
					Log.e(TAG, "change face"); 
					if (change_face)
					{rel_showtime1.setVisibility(RelativeLayout.INVISIBLE);
					rel_showtmp2.setVisibility(RelativeLayout.VISIBLE);}
					else
					{rel_showtime1.setVisibility(RelativeLayout.VISIBLE);
					rel_showtmp2.setVisibility(RelativeLayout.INVISIBLE);};
				}
			  });
			
		   }else {
			   
			   runOnUiThread(new Runnable()
				{
				   public void run()
					{
					   rel_showtime1.setVisibility(RelativeLayout.VISIBLE);
						rel_showtmp2.setVisibility(RelativeLayout.INVISIBLE);
					}
				});
			   
		    }
			
			try
			{
				Thread.sleep(change_time);
				change_face = !change_face;
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		 }
		 
		}
		
	}
	
	private void init_server()
	{
		try
		{
			server = new ServerSocket(10086);
			Log.e(TAG, "server start");
			while(true)
			{
				Socket msSocket = server.accept();
				msSocket.setSoTimeout(2000);
				msSocket.setSoLinger(true, 5);
				Log.e(TAG, "new client");
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
						 Log.e(TAG,"收到数据长度为："+ readbyte.length);
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
											    Log.e(TAG, "1 OK");
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
								 Log.e(TAG, str);
							 }
						 }
						 
						Thread.sleep(500);
						msSocket.close();
						msSocket = null;
					 } 
		       	}
			} catch (IOException e)
			{
				Log.e(TAG, "连接关闭");
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
   	
   	

	@Override
	public void finish()
	{
		unregisterReceiver(br1);
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 过滤按键动作
		
		 if( keyCode == KeyEvent.KEYCODE_MENU){  
			 
			 final Builder menu_dialog = new Builder(FrameActivity.this);
			 View menu_dialog_view = View.inflate(FrameActivity.this,  R.layout.menu_v, null);
			 menu_dialog.setView(menu_dialog_view);
		
			 Button btn_soft = (Button)menu_dialog_view.findViewById(R.id.btn_soft);
			 
			 btn_soft.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					Intent excel = new Intent();		
					excel.setClass(FrameActivity.this, WebActivity.class);
				    excel.putExtra("urls", "http://jsonok.jsp.fjjsp.net/othersoft/index.jsp");
					startActivity(excel);
					
				}
			});
			 
			 final  TextView tv2 = (TextView)menu_dialog_view.findViewById(R.id.textView3);
			 tv2.setText(String.valueOf(change_time/1000)+"秒");
			 
			 ToggleButton tb1 = (ToggleButton)menu_dialog_view.findViewById(R.id.toggleButton1);
			 tb1.setChecked(showtemp);
			 
			 tb1.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					showtemp = isChecked;
					
					
					Log.e(TAG, ""+isChecked);
				}
			});
			 
			 SeekBar sk1 = (SeekBar)menu_dialog_view.findViewById(R.id.seekBar1);
			 sk1.setMax(10);
			 
			 sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					Log.e(TAG, ""+progress);
					if (progress==0)
					{
						progress = 1;
					}
					change_time = progress * 10000;
					tv2.setText(String.valueOf(progress*10)+"秒");
				}
			});
			 
			 menu_dialog.setInverseBackgroundForced(true);
			 menu_dialog.setTitle("系统设置");
			 menu_dialog.setOnKeyListener(new OnKeyListener()
				{
					
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
					{
						if (keyCode==KeyEvent.KEYCODE_BACK)
						{
							SharedPreferences preferences = getSharedPreferences("change_time",MODE_MULTI_PROCESS );
							 Editor editor = preferences.edit();    
						        //存入数据      
						        editor.putLong("change_time", change_time);    
						        //提交修改      
						        editor.commit();
						        
						      preferences = getSharedPreferences("showtemp",MODE_MULTI_PROCESS );
							     editor = preferences.edit();    
							        //存入数据      
							        editor.putBoolean("showtemp", showtemp);    
							        //提交修改      
							        editor.commit();
						        
							dialog.dismiss();
						}
						return true;
					}
				});
				 
			 menu_dialog.show();
			 
	                return true;  
	         }  
	         return super.onKeyDown(keyCode, event);  
	}

}
