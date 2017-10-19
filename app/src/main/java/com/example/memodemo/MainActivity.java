package com.example.memodemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private MyDatabaseHelper dbHelper;
	private ListView listViewBasic;
	private String things;
	private InnerAdapter adapter;
	private ArrayList<String> list = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listViewBasic = (ListView) super.findViewById(R.id.listview);
		dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 1);
		Button bewang = (Button) findViewById(R.id.writeClick);
		bewang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				list.clear();
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor cursor = db.query("Book", null, null, null, null, null, null);
				if (cursor.moveToFirst()) {
					do {
						//遍历CURSOR对象，取出数据
						things = cursor.getString(cursor.getColumnIndex("things"));
						list.add(things);
						adapter = new InnerAdapter(MainActivity.this, list); // 加载listiew
						listViewBasic.setAdapter(adapter);
						String date = cursor.getString(cursor.getColumnIndex("date"));
						String time = cursor.getString(cursor.getColumnIndex("time"));
					} while (cursor.moveToNext());
				}
			}
		});







	}

	public void doClick(View v) {
        dbHelper.getWritableDatabase();
		Intent intent = new Intent(MainActivity.this, WriteActivity.class);
		startActivity(intent);

	}

}


