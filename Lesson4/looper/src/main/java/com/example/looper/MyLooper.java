package com.example.looper;
import android.os.Message;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
public class MyLooper extends Thread{
    public Handler mHandler;
    private Handler mainHandler;
    public MyLooper(Handler mainThreadHandler){
        mainHandler = mainThreadHandler;
    }
    public void run(){
        Log.d("MyLooper","run");
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper()){
            public void handleMessage(Message msg){
                Integer age = msg.getData().getInt("AGE");
                Log.d("MyLooper get message", String.valueOf(age));
                String Job = msg.getData().getString("JOB");
                Log.d("MyLooper get message", Job);

                Message message = new Message();
                Bundle bundle = new Bundle();
                try{
                    Thread.sleep(age * 1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                bundle.putString("result",String.format("Возраст: %d ||| Работа: %s", age, Job));
                message.setData(bundle);

                mainHandler.sendMessage(message);
            }
        };
        Looper.loop();

    }
}