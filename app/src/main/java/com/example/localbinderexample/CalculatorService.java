package com.example.localbinderexample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CalculatorService extends Service {

    Messenger serviceMessenger = null;
    Messenger activityMessenger = null;

    Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int num1 = msg.arg1;
            int num2 = msg.arg2;
            int sum = num1 + num2;

            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putInt("result", sum);
            message.setData(bundle);

            activityMessenger = msg.replyTo; // retrieving the activity messenger
            try {
                activityMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v("Service Process", "PID - "+Process.myPid());
            //Toast.makeText(CalculatorService.this, "Service Process ID " + Process.myPid(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        serviceMessenger = new Messenger(serviceHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = serviceMessenger.getBinder();
        return binder;
    }

}
