package com.example.kengo.myfeedbacker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import java.net.SocketException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    OSCPortIn receiver = null;
    OSCListener yoiListener = null;
    OSCListener speedListener = null;
    private TextView yoidata;
    private Button Button;
    public float yoi = 0;
    public int speed=1;
    long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500}; // OFF/ON/OFF/ON...
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button = (Button) findViewById(R.id.button);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // エディットテキストのテキストを取得
                receiver.stopListening();
                receiver.close();
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("com.google.android.gms.samples.vision.face.facetracker");
                startActivity(intent);
            }
        });
        //サーバーから通信を受け取る関数
        startOscListener();
        //幹事モードへの移行


        yoidata = (TextView) findViewById(R.id.text_yoi);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void onResume() {
        super.onResume();



    }

    private void startOscListener() {
        try {
            //10000番ポートで受信
            receiver = new OSCPortIn(10000);
        } catch (SocketException e2) {
            e2.printStackTrace();
        }
        final Handler handler = new Handler();
        yoiListener = new OSCListener() {
            public void acceptMessage(Date time, OSCMessage message) {
                System.out.println("yoi received!");
                System.out.println(message.getAddress());
                for (Object ob : message.getArguments()) {
                    System.out.println((float) ob);
                    yoi = (float) ob;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            yoidata.setText(Integer.toString((int)yoi)+"%");
                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                            ImageView imageView2=(ImageView)findViewById(R.id.imageView2);
                            if(yoi<50) {
                                imageView.setImageResource(R.drawable.haikei1);
                                imageView2.setImageResource(R.drawable.moji1);
                            }
                            else if(yoi<100){
                                imageView.setImageResource(R.drawable.haikei2);
                                imageView2.setImageResource(R.drawable.moji3);
                            }
                            else{
                                imageView.setImageResource(R.drawable.haikei3);
                                imageView2.setImageResource(R.drawable.moji2);
                            }
                        }
                    });
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(pattern, -1);
                }
            }
        };
        speedListener = new OSCListener() {
            public void acceptMessage(Date time, OSCMessage message) {
                System.out.println("speed received!");
                System.out.println(message.getAddress());
                for (Object ob : message.getArguments()) {
                    System.out.println((int) ob);
                    speed=(int)ob;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                            ImageView imageView2=(ImageView)findViewById(R.id.imageView2);
                            if(speed==1) {
                                imageView2.setImageResource(R.drawable.moji1);
                            }
                            else if(speed==2){
                                imageView2.setImageResource(R.drawable.moji3);
                            }
                            else{
                                imageView2.setImageResource(R.drawable.moji4);
                            }
                        }
                    });
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(pattern, -1);
                }
            }
        };
        receiver.addListener("/send/yoi0", yoiListener);
        receiver.addListener("/send/speed",speedListener);
        receiver.startListening();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
