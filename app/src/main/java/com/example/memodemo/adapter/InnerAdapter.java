package com.example.memodemo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.memodemo.ui.MainActivity;
import com.example.memodemo.data.MyDatabaseHelper;
import com.example.memodemo.R;
import com.example.memodemo.ui.WriteActivity;

import java.util.ArrayList;
import java.util.List;

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

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Overwrite",Toast.LENGTH_SHORT).show();
                TextView textView = (TextView) finalView.findViewById(R.id.writedown);
                String overwrite = (String) textView.getText();
                String[] strow = overwrite.split("\n");
                String strover = strow[1];

                dbHelper = new MyDatabaseHelper(mContext, "BookStore.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                TextView textView1 = (TextView) finalView.findViewById(R.id.writedown);
                String str = (String) textView1.getText();
                //java截取字符串  截取\n前后字符串，前面为str1[0],后面为str1[2];
                String[] str1 = str.split("\n");
                String str2 = str1[1];
                //Log.d("onClick: ======", String.valueOf(str2));
                db.delete("Book","things = ?", new String[]{String.valueOf(str2)});

                Intent intent = new Intent(mContext,WriteActivity.class);
                intent.putExtra("stroverwrite", strover);
                mContext.startActivity(intent);

            }
        });

        viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                /*.setTitle("Oldnote")
                .setIcon(R.drawable.logo5)*/
                .setMessage("您确定删除该条信息么？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper = new MyDatabaseHelper(mContext, "BookStore.db", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        TextView textView = (TextView) finalView.findViewById(R.id.writedown);
                        String str = (String) textView.getText();
                        //java截取字符串  截取\n前后字符串，前面为str1[0],后面为str1[2];
                        String[] str1 = str.split("\n");
                        String str2 = str1[1];
                        //Log.d("onClick: ======", String.valueOf(str2));
                        db.delete("Book","things = ?", new String[]{String.valueOf(str2)});
                        Toast.makeText(mContext,"delete successed",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(mContext,MainActivity.class);
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(mContext,MainActivity.class);
//                        mContext.startActivity(intent);
                    }
                })
                .show();
            }
        });
        viewHolder.wButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"repect write",Toast.LENGTH_SHORT).show();
                TextView textView = (TextView) finalView.findViewById(R.id.writedown);
                String rewrite = (String) textView.getText();
                String[] str3 = rewrite.split("\n");
                String str4 = str3[1];

                dbHelper = new MyDatabaseHelper(mContext, "BookStore.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                TextView textView3 = (TextView) finalView.findViewById(R.id.writedown);
                String str = (String) textView3.getText();
                //java截取字符串  截取\n前后字符串，前面为str1[0],后面为str1[2];
                String[] str1 = str.split("\n");
                String str2 = str1[1];
                //Log.d("onClick: ======", String.valueOf(str2));
                db.delete("Book","things = ?", new String[]{String.valueOf(str2)});

                Intent intent = new Intent(mContext,WriteActivity.class);
                intent.putExtra("str", str4);
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
