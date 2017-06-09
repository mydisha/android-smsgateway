package com.sms.mydisha.sms_server;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SmsServer smsServer;
    public TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsServer = new SmsServer(this);
        Log.d("Jalan", "Running gan");
        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setText(smsServer.getIpAddress() + ":" + smsServer.getPort());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

    }

    protected void onDestroy() {
        super.onDestroy();
        smsServer.onDestroy();
    }
}
