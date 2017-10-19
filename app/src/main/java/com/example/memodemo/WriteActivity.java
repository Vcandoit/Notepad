package com.example.memodemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;
public class WriteActivity extends Activity {

	private EditText editText1;
	private MyDatabaseHelper dbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writeup);


		//String str = formatter.format(curDate);

		editText1 =(EditText)findViewById(R.id.writedown);
		//new一个新对象，要不然下面会出现空指针
		dbHelper = new MyDatabaseHelper(this,"BookStore.db",null,1);

		Button saveData = (Button) findViewById(R.id.savedata);
		saveData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss     ");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间
				String str1=editText1.getText().toString();
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				//插入数据
				values.put("things",str1);
				values.put("date", String.valueOf(formatter));
				values.put("time", String.valueOf(curDate));
				db.insert("Book",null,values);
				values.clear();
				Toast.makeText(WriteActivity.this,"add successed",Toast.LENGTH_SHORT).show();
                finish();
			}
		});
	}
	public void doClick(View view){
		Intent intent = new Intent(WriteActivity.this,MainActivity.class);
		startActivity(intent);
	}
}


























