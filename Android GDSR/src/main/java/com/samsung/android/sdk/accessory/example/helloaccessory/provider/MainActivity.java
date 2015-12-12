package com.samsung.android.sdk.accessory.example.helloaccessory.provider;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.remotesensor.Srs;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensor;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensorEvent;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensorManager;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensorManager.EventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends Activity implements EventListener {
	
	SrsRemoteSensorManager mServiceManager = null;
	TextView wearableStatus;
	TextView userActivity;
	TextView positionData;
	List<SrsRemoteSensor> wearableStatusList;
	List<SrsRemoteSensor> activitySensorList;
	List<SrsRemoteSensor> pedoSensorList;
	Srs remoteSensor = null;
	SrsRemoteSensor wearableStatusSensor = null;
	SrsRemoteSensor userActivitySensor = null;
	SrsRemoteSensor pedometerSensor = null;


	private Context mContext;
	private Button btn;
	BufferedReader mReader;
    BufferedWriter mWriter;
    String dstAddress="192.168.0.6";
    int dstPort=51717;
    String mRecvData = "";
    int flag=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this.getBaseContext();

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		bar.setDisplayShowTitleEnabled(true);
	
		wearableStatus = (TextView)findViewById(R.id.textView4);
		userActivity = (TextView)findViewById(R.id.textView5);
        positionData = (TextView)findViewById(R.id.textView6);
		btn = (Button)findViewById(R.id.button1);
		
		remoteSensor = new Srs();
		
		try {
			remoteSensor.initialize(mContext);
		
		} catch (SsdkUnsupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			switch (e.getType ()) { 
				case SsdkUnsupportedException.LIBRARY_NOT_INSTALLED:
				// Handle the exception 
					break; 
				case SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED:
					// Handle the exception 
					break; 
				default: 
					// Handle the exception 
					break;
				}
			}
			
		mServiceManager = new SrsRemoteSensorManager(remoteSensor);
        NetworkTask myClientTask = new NetworkTask(dstAddress, dstPort);
        myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;

		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               /* Intent intent = new Intent(getApplicationContext(), Alert.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);*/
	            getWearableEvent(v);
	            getActivityEvent(v);
	            getPedometerEvent(v);
            }
        });

	}



	public void getWearableEvent(View view){
		
		wearableStatusList = mServiceManager.getSensorList(SrsRemoteSensor.TYPE_WEARING_STATE);
		wearableStatusSensor = wearableStatusList.get(0);
		mServiceManager.registerListener(this, wearableStatusSensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
	}
	
	public void getActivityEvent(View view){
		activitySensorList = mServiceManager.getSensorList(SrsRemoteSensor.TYPE_USER_ACTIVITY);
		userActivitySensor = activitySensorList.get(0);
		mServiceManager.registerListener(this, userActivitySensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
	}
	
	public void getPedometerEvent(View view){
		pedoSensorList = mServiceManager.getSensorList(SrsRemoteSensor.TYPE_PEDOMETER);
		pedometerSensor = pedoSensorList.get(0);
		mServiceManager.registerListener(this, pedometerSensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
	}
	
	public void stopPedometerEvent(View view){
		
		SrsRemoteSensor wearSensor;
		wearSensor = wearableStatusList.get(0);
		mServiceManager.unregisterListener(this, wearSensor);
	
		SrsRemoteSensor actSensor;
		actSensor = activitySensorList.get(0);
		mServiceManager.unregisterListener(this, actSensor);
	
		SrsRemoteSensor pedSensor;
		pedSensor = pedoSensorList.get(0);
		mServiceManager.unregisterListener(this, pedSensor);
	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mServiceManager.registerListener(this, wearableStatusSensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
		mServiceManager.registerListener(this, userActivitySensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
		mServiceManager.registerListener(this, pedometerSensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
		
	}
    @Override
    protected void onStop() {
        super.onStop();
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		mServiceManager.unregisterListener(this, wearableStatusSensor);
		mServiceManager.unregisterListener(this, userActivitySensor);
		mServiceManager.unregisterListener(this, pedometerSensor);
		
	}
    
    @Override
    protected void onDestroy(){
        super.onDestroy();

        flag=1;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        flag=1;

        finish();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(SrsRemoteSensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorDisabled(SrsRemoteSensor arg0) {
		// TODO Auto-generated method stub
		
	}

	// Called when sensor values have changed.
	@Override
	public void onSensorValueChanged(final SrsRemoteSensorEvent event) {
		runOnUiThread(new Runnable() { @Override
									   public void run() {
			
			if(event.sensor.getType() == SrsRemoteSensor.TYPE_WEARING_STATE){
				if(event.values[0] == 1.0)
				{	wearableStatus.setText("WEARING");
				}else{
					wearableStatus.setText("NOT WEARING");
				}
			}
		
			if (event.sensor.getType() == SrsRemoteSensor.TYPE_USER_ACTIVITY)
			{ 
				if(event.values[0] == 1.0)
				{	userActivity.setText("WALKING");
				}else if(event.values[0] == 2.0){
					userActivity.setText("RUNNING");
				}else{
					userActivity.setText("STOP");
				}
			}
		} 
		});
	}

    public class NetworkTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response;

        NetworkTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                final Socket socket = new Socket(dstAddress, dstPort);
                mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                PrintWriter out = new PrintWriter(mWriter, true);
                while(true){
                    out.println(2);
                    if(flag==1) {
                        break;
                    }
                    mRecvData = mReader.readLine();
                    mReceiver.sendEmptyMessage(0);
                    Thread.sleep(1000);
                }
                out.println(-1);
                socket.close();
                flag=0;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        Handler mReceiver = new Handler() {
            public void handleMessage(Message msg) {
                if(Integer.parseInt(mRecvData)>2000){

                }
                else if(Integer.parseInt(mRecvData)>15){
                    positionData.setText("OUTDOOR");
                }else{
                    positionData.setText("INDOOR");
                }
            }
        };

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Integer.parseInt(mRecvData)>15){
                        positionData.setText("OUTDOOR");
                    }else{
                        positionData.setText("INDOOR");
                    }


                }

            });
        }

    }

}
