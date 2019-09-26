package com.example.localbinderexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText firstValue, secondValue;
    TextView tvResult;
    Intent bindIntent;
    CalculatorService calculatorService;

    Handler activityHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int sum = bundle.getInt("result");
            tvResult.setText(String.valueOf(sum));
            Log.v("Activity Process", "PID - " + Process.myPid());
        }
    };

    Messenger serviceMessenger = null;
    Messenger activityMessenger = new Messenger(activityHandler);


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstValue = findViewById(R.id.aValue);
        secondValue = findViewById(R.id.bValue);
        tvResult = findViewById(R.id.cValue);

        bindIntent = new Intent(this, CalculatorService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void Sumvalue(View view) {
        int a = Integer.parseInt(firstValue.getText().toString());
        int b = Integer.parseInt(secondValue.getText().toString());
        Message message = Message.obtain(null, 1, a, b); // created Message object
        message.replyTo = activityMessenger; // putting reference of activity messenger in Message object
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //tvResult.setText(String.valueOf(c));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
