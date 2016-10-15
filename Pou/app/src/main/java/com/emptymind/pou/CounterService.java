package com.emptymind.pou;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CounterService extends Service implements Runnable, Counter
{
    long start;
    float deltaTime;
    protected int countThirst;
    protected int countHungry;
    private boolean active;
    private final LocalBinder connection = new LocalBinder();

    //This class returns to Activity the service reference.
    //With this reference is possible to get the Counter value and show to user.
    public class LocalBinder extends Binder
    {
        public Counter getService() { return CounterService.this; }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("SERVICE SAMPLE", "SERVICE SAMPLE onCreate()");
        active = true;
        new Thread(this).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("SERVICE SAMPLE", "SERVICE SAMPLE onStart()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.d("SERVICE SAMPLE", "SERVICE SAMPLE onDestroy()");
        //active = false;
    }

    //When the service is connected to the Activity,
    //this method is called to return the reference of this service to the Activity.
    @Override
    public IBinder onBind(Intent intent) { return connection; }

    public int GetCountThirst() { return countThirst; }
    public void SetCountThirst(int n) {this.countThirst = n; }
    public int GetCountHungry() { return countHungry; }
    public void SetCountHungry(int n) {this.countHungry = n; }

    @Override
    public void run()
    {

        long time = System.nanoTime();
        deltaTime = (float)((time - start)/1000000);
        start = time;



        while(active)
        {
            //Log.d("SERVICE SAMPLE", "EXECUTING SERVICE: " + countHungry);
            if(countHungry > 500){
                countHungry = 500;
            }else if(countHungry > 0){
                countHungry -= 2;
            }

            if(countThirst > 300){
                countThirst = 300;
            }else if(countThirst > 0){
                countThirst -= 5;
            }

            if (countHungry > 250 && countThirst > 100) {

            }
            if (countHungry < 250 || countThirst < 100) {

            }
            if (countHungry <= 100 || countThirst <= 60) {

            }
            if (countHungry <= 0 || countThirst <= 0) {

            }

            Log.d("SERVICE SAMPLE", "TIME: " + deltaTime);

            SetInterval();
        }

        countHungry = 0;
        //Log.d("SERVICE SAMPLE", "SERVICE SAMPLE: FIM");

        stopSelf();
    }





    private void SetInterval()
    {
        try { Thread.sleep(1000); }
        catch(InterruptedException e) { e.printStackTrace(); }
    }


}