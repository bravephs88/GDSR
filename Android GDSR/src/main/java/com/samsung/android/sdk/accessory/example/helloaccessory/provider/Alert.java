package com.samsung.android.sdk.accessory.example.helloaccessory.provider;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ajou on 2015-11-27.
 */


public class Alert extends Activity {
    private Button btn;
    SoundPool m_alram;
    int m_alram_id;
    String dstAddress1="192.168.0.6";
    int dstPort1=51720;
    int flag=0;
    BufferedWriter mWriter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        btn = (Button)findViewById(R.id.button1);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        bar.setDisplayShowTitleEnabled(true);
        m_alram = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        m_alram_id=m_alram.load(this, R.raw.alarm2, 1);
        m_alram.play(m_alram_id, 1, 1, 0, -1, 1);
        NetworkTask1 myClientTask1 = new NetworkTask1(dstAddress1, dstPort1);
        myClientTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    flag=1;
                    m_alram.stop(m_alram_id);
                    NetworkTask1 myClientTask1 = new NetworkTask1(dstAddress1, dstPort1);
                    myClientTask1.cancel(true);
                    finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    public class NetworkTask1 extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;

        NetworkTask1(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }



        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Socket socket1 = new Socket(dstAddress1, dstPort1);
                mWriter1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
                PrintWriter out1 = new PrintWriter(mWriter1, true);
                while(true){
                    out1.println(2);
                    if(flag==1){
                        break;
                    }
                    Thread.sleep(1000);
                }
                out1.println(-1);
                socket1.close();
                flag=0;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


