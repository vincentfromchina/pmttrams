package com.dayu.smarttmp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btn_open = (Button) findViewById(R.id.open);
		btn_open.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent it = new Intent();
				it.setClass(MainActivity.this, ShowTmpActivity.class);
				startActivity(it);
				
			}
		});
		
		Button btn_test = (Button) findViewById(R.id.btn_test);
		btn_test.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent it = new Intent();
				it.setClass(MainActivity.this, FrameActivity.class );
				startActivity(it);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
