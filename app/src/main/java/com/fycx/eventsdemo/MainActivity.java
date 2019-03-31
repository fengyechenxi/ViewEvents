package com.fycx.eventsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fycx.event.ViewEventService;
import com.fycx.event.annotation.OnClick;
import com.fycx.event.annotation.OnLongClick;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       new ViewEventService().setRepeatInterval(500).injectEvents(this);

    }

    @OnClick(
            value = {R.id.btn,R.id.tv},
            interceptors = {UserInfoNullInterceptor.class},
            interceptorsCallback = {UserInfoNullInterceptor.Callback.class}
    )
    public void viewClick(View view){
        Log.e("MainActivity","viewClick");
        switch (view.getId()){
            case R.id.btn:
                showT("Btn show");
                break;
            case R.id.tv:
                showT("tv show");
                break;
        }
    }
//
//    @OnLongClick(
//            value = {R.id.btn,R.id.tv},
//            interceptors = {LoginInterceptor.class},
//            interceptorsCallback = {LoginInterceptor.Callback.class}
//    )
//    public boolean longClick(View view){
//        Log.e("MainActivity","longClick");
//        switch (view.getId()){
//            case R.id.btn:
//                showT("longClick Btn show");
//                break;
//            case R.id.tv:
//                showT("longClick tv show");
//                break;
//        }
//        return true;
//    }



    @LoginInterceptor.Callback
    public void loginInterceptor(){
        Log.e(TAG,"loginInterceptor 拦截住了，因为没有登录");
    }

    @UserInfoNullInterceptor.Callback
    public void hahah(){
        Log.e(TAG,"UserInfoNullInterceptor拦截住了 >>>>>>>>>");
    }

    private void showT(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
