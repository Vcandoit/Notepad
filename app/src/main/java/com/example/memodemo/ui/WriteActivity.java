package com.example.memodemo.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.memodemo.PublicWay;
import com.example.memodemo.R;
import com.example.memodemo.data.MyDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends Activity {

	private EditText editText1;
	private MyDatabaseHelper dbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.writeup);

		PublicWay.activityList.add(this);

		editText1 =(EditText)findViewById(R.id.writedown);
		//new一个新对象，要不然下面会出现空指针
		dbHelper = new MyDatabaseHelper(this,"BookStore.db",null,1);

		init();


		Button saveData = (Button) findViewById(R.id.savedata);
		saveData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss     ");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间
				String str = formatter.format(curDate);
				String str1=editText1.getText().toString();
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				//插入数据
				values.put("things",str1);
				values.put("time", String.valueOf(str));
				db.insert("Book",null,values);
				values.clear();
				Toast.makeText(WriteActivity.this,"add successed",Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(WriteActivity.this,MainActivity.class);
                startActivity(intent);
				finish();
			}
		});


	}

	private void init() {
		Intent intent2 = getIntent();
		Bundle bundle2 = intent2.getExtras();
		int judge = bundle2.getInt("judge");
		if(judge == 1){
			//Toast.makeText(WriteActivity.this,"没有内容",Toast.LENGTH_SHORT).show();

		}else{
			Intent intent1=getIntent();
			Bundle bundle=intent1.getExtras();
			String rewrite=bundle.getString("str");
			editText1.setText(rewrite);
		}
	}

	public void reClick(View view){
		//首页传过来一个值判断是添加还是修改
		//添加提示没有内容
		//修改会显示前面内容
		Intent intent2 = getIntent();
		Bundle bundle2 = intent2.getExtras();
		int judge = bundle2.getInt("judge");
		if(judge == 1){
			Toast.makeText(WriteActivity.this,"没有内容",Toast.LENGTH_SHORT).show();

		}else{
			Intent intent1=getIntent();
			Bundle bundle=intent1.getExtras();
			String rewrite=bundle.getString("str");
			editText1.setText(rewrite);
		}
	}
	public void doClick(View view){
		Intent intent = new Intent(WriteActivity.this,MainActivity.class);
		startActivity(intent);
	}
}


























