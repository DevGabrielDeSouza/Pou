package com.emptymind.pou;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity  implements ServiceConnection, Runnable {

    long start;
    float deltaTime;
    String PouState;
    Button pou;
    TextView HungryText;
    TextView ThirstText;
    Handler handler;
    private Counter counter;
    private boolean firstStart;
    final ServiceConnection connection = this;

    boolean firstAlert = false;
    boolean lastAlert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = System.nanoTime();
        pou = (Button) findViewById(R.id.Pou);
        HungryText = (TextView) findViewById(R.id.HungryText);
        ThirstText = (TextView) findViewById(R.id.ThirstText);

        firstStart = true;

        //final Intent intent = new Intent(this, CounterService.class);

        //startService(intent);

        bindService(new Intent(MainActivity.this, CounterService.class), connection, Context.BIND_AUTO_CREATE);

        final Intent intent = new Intent(this, CounterService.class);

        startService(intent);

        handler = new Handler();
        handler.post(this);
    }

    public void Send(String s, int icon)
    {
        //Building the notification.
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle("Pou")
                .setContentText(s)
                .setAutoCancel(true);

        //Intent to be started if user activate the notification panel.
        //PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        //Connecting notification wmBuilder.setContentIntent(pi.setContentIntent(pi);

        //Need permission on manifest.
        mBuilder.setVibrate(new long[]{100, 250, 100, 500});

        //Getting the OS notification service.
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Sending the notification.
        mNotificationManager.notify(R.string.app_name, mBuilder.build());
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        UnbindConnection();
    }

    private void UnbindConnection()
    {
        if(counter != null)
        {
            Log.d("BIND SERVICE SAMPLE", "STOP BIND SERVICE");
            //counter = null;
            //unbindService(connection);
        } else Log.d("BIND SERVICE SAMPLE", "THE SERVICE ISN'T CONNECTED");
    }

    @Override
    public void run()
    {
        if(counter != null){
            if(firstStart) {
                counter.SetCountHungry(400);
                counter.SetCountThirst(200);
                firstStart = false;
            }else{
                String tempHungry = "Hungry:"  + (int)counter.GetCountHungry();
                //String tempHungry = "Hungry:"  + deltaTime;
                HungryText.setText(tempHungry);
                String tempThirst = "Thirst:"  + (int)counter.GetCountThirst();
                ThirstText.setText(tempThirst);
            }
        }

        handler.postDelayed(this, 30);
        long time = System.nanoTime();
        deltaTime = (float)((time - start)/1000000);
        start = time;

        ManageState();
    }



    void ManageState()
    {
        if(counter != null && !firstStart) {
            if ((int) counter.GetCountHungry() > 250 && (int)counter.GetCountThirst() > 100) {
                PouState = "Green";
                firstAlert = false;
                lastAlert = false;
            }
            if ((int) counter.GetCountHungry() < 250 || (int)counter.GetCountThirst() < 100) {
                PouState = "Yellow";
                lastAlert = false;
            }
            if ((int) counter.GetCountHungry() <= 100 || (int) counter.GetCountThirst() <= 60) {
                PouState = "Red";
                firstAlert = false;
            }
            if ((int) counter.GetCountHungry() == 0 || (int) counter.GetCountThirst() == 0) {
                Send("Seu Pou morreu e ressuscitou do mundo dos mortos. Seu assassino MALDITO!!!", R.drawable.btn_green);
                counter.SetCountHungry(400);
                counter.SetCountThirst(200);
                PouState = "Green";
                firstAlert = false;
                lastAlert = false;
            }
            if (PouState.equals("Green")) {
                //pou.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_green));
                pou.setBackgroundColor(Color.parseColor("#8bc34a"));
            }
            if (PouState.equals("Yellow")) {
                //pou.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_yellow));
                pou.setBackgroundColor(Color.parseColor("#ffeb3b"));
                if(!firstAlert){
                    Send("Seu Pou não está saudável.", R.drawable.btn_yellow);
                    firstAlert  = true;
                }
            }
            if (PouState.equals("Red")) {
                //pou.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_red));
                pou.setBackgroundColor(Color.parseColor("#f44336"));
                if(!lastAlert){
                    Send("Seu Pou está em um estado CRÍTICO!", R.drawable.btn_red);
                    lastAlert  = true;
                }
            }
        }
    }

    public void GiveFood(View v)
    {
        if(counter != null && !firstStart && counter.GetCountHungry() <= 450){
            counter.SetCountHungry(counter.GetCountHungry()+50);
        }
    }

    public void GiveWater(View v)
    {
        if(counter != null && !firstStart && counter.GetCountThirst() <= 290){
            counter.SetCountThirst(counter.GetCountThirst()+10);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        //Here we get the reference of the service through IBinder.
        Log.d("BIND SERVICE SAMPLE", "SERVICE CONNECTED");

        CounterService.LocalBinder binder = (CounterService.LocalBinder) service;
        counter = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        counter = null;
    }
}
