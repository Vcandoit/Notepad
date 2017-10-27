package com.example.memodemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoActivity extends Activity {

    final static int CAMERA_RESULT = 0;//声明一个常量，代表结果码
    private Button button;//声明一个Button对象
    private ImageView imageView;//声明一个ImageView对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.takephoto);
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //实例化Intent对象,使用MediaStore的ACTION_IMAGE_CAPTURE常量调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_RESULT);//开启相机，传入上面的Intent对象
            }
        });
    }
    /**
     * 用onActivityResult()接收传回的图像，当用户拍完照片，或者取消后，系统都会调用这个函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode==RESULT_OK){
            Bundle extras=intent.getExtras();//从Intent中获取附加值
            Bitmap bitmap=(Bitmap) extras.get("data");//从附加值中获取返回的图像
            imageView.setImageBitmap(bitmap);//显示图像
        }
    }


}