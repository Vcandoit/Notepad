package com.example.memodemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import static android.R.id.content;
import static android.R.id.list;

public class InnerAdapter extends BaseAdapter {

    private MyDatabaseHelper dbHelper;
    private Context mContext;
    private List<String> mList = new ArrayList<>();

    public InnerAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            viewHolder.mTextView = (TextView) view.findViewById(R.id.writedown);
            viewHolder.mButton = (ImageView) view.findViewById(R.id.delete);
            viewHolder.wButton = (ImageView) view.findViewById(R.id.write_down);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mTextView.setText(mList.get(i));
        final View finalView = view;
        viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new MyDatabaseHelper(mContext, "BookStore.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                TextView textView = (TextView) finalView.findViewById(R.id.writedown);
                String str = (String) textView.getText();
                db.delete("Book","things = ?", new String[]{str});
                Toast.makeText(mContext,"delete successed",Toast.LENGTH_SHORT).show();

            }
        });
        viewHolder.wButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"repect write",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,WriteActivity.class);
                mContext.startActivity(intent);

            }
        });
        return view;
    }

    class ViewHolder {
        TextView mTextView;
        ImageView mButton;
        ImageView wButton;
    }

}
