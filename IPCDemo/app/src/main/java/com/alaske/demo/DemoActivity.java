package com.alaske.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import demo.ipc.alaske.com.aidl.IMyAidlInterface;
import demo.ipc.alaske.com.ipcdemo.R;

/**
 * Author: zhaocheng
 * Date: 2016-06-03
 * Time: 16:12
 * Name:DemoActivity
 * Introduction:
 */
public class DemoActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText num1,num2;
    private TextView result;
    private Button measure;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        measure = (Button) findViewById(R.id.measure);
        result = (TextView) findViewById(R.id.result);
        measure.setOnClickListener(this);

        //bind service
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("demo.ipc.alaske.com.aidl","demo.ipc.alaske.com.aidl.IRemoteService"));
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    private IMyAidlInterface iMyAidl;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyAidl = IMyAidlInterface.Stub.asInterface(service);
            Log.d("zc","connection onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyAidl = null;
            Log.d("zc","connection onServiceDisconnected");
        }
    };

    @Override
    public void onClick(View v) {
        if (iMyAidl==null) return;
        try {
            int  i = iMyAidl.add(Integer.parseInt(num1.getText().toString()),Integer.parseInt(num2.getText().toString()));
            result.setText("" + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
