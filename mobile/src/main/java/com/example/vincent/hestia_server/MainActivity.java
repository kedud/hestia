package com.example.vincent.hestia_server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import Collect.SensorData;
import SensorDB.SensorDataBase;
import SensorDB.Sensor;
import SerialDriver.UsbSerialPort;
import SerialUtils.SerialInputOutputManager;
import SerialUtils.HexDump;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     * <p/>
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    private static UsbSerialPort sPort = null;

    private int lastDataLength = 0;
    private String[] lastData = new String[30];
    private String[] components;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;


    private SensorDataBase SensorsDB = new SensorDataBase(this);
    private ArrayList<Sensor> SensorsList = new ArrayList<Sensor>();


    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mTitleTextView = (TextView) findViewById(R.id.ActivityTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(57600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {

        stopIoManager();
        startIoManager();

    }

    private void updateReceivedData(byte[] data) {
        final String line = HexDump.dumpHexString(data);
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data)  + "\n\n";

        mDumpTextView.append(message);

        try {

            components = line.trim().split("[ \t]+");

            if (components.length == Integer.parseInt(components[0]) && components.length == 30) {     //If the entire message is received
                //Let's parse it
                Log.d("Update Received Data", "Try parsing the incoming data");
                lastDataLength = 0;
                SensorData sensor = SensorData.parseSensorData(HexDump.dumpHexString(data));
                mDumpTextView.append(" SensorID =  " + sensor.getNodeID()
                        + "\t temperature = " + sensor.getTemperature()
                        + "\n\n");
                mDumpTextView.append("Trying DB add\n"); //DEBUG

                AddToDB(sensor);


            } else {
                if (components.length < 30) {                                   //The message is not complete, we have to wait for the 2nd part, or complete the 1st part.
                    if (lastDataLength == 0) {                                  //if the last message is complete, or was completed.
                        lastDataLength = components.length;

                        for (int i = 0; i < components.length; i++) {
                            lastData[i] = components[i];
                        }
                        mDumpTextView.append("Partial MSG size = " + components.length + "\n\n");
                    } else {                                              //The temporary array is already filled with some data, we add the new data.
                        if ((lastDataLength + components.length) <= 30) {       //we check that the data we received and the data that were stored doesn't exceed the array.
                            for (int i = 0; i < components.length; i++) {
                                lastData[i + lastDataLength] = components[i];
                            }
                            lastDataLength += components.length;
                            mDumpTextView.append("Partial MSG size = " + components.length + "\n\n");
                            if (lastDataLength == 30) {      // The message is complete, we can parse the message
                                lastDataLength = 0;
                                mDumpTextView.append("FULL MSG = ");

                                for (int i = 0; i < lastData.length; i++)
                                    mDumpTextView.append(lastData[i]);
                                mDumpTextView.append("\n\n");

                                String joined = new String();
                                for (int i = 0; i < lastData.length; i++) {
                                    joined += lastData[i];
                                    if (i == lastData.length) {

                                    } else joined += "\t";

                                }
                                SensorData sensor = SensorData.parseSensorData(joined);
                                mDumpTextView.append(" SensorID =  " + sensor.getNodeID()
                                        + "\t temperature = " + sensor.getTemperature()
                                        + "\n\n");
                                mDumpTextView.append("Trying DB add\n"); //DEBUG

                                AddToDB(sensor);
                            }

                        } else {                                           //If the message exceed the size, we cannot use it...
                            lastDataLength = 0;
                        }
                    }
                }
            }
        }
        catch(NumberFormatException e)
        {
            mDumpTextView.append(e.getMessage());
        }

    }


    private void AddToDB(SensorData sensor){

        mDumpTextView.append("Trying DB add\n"); //DEBUG

        try {
            Time now = new Time();
            now.setToNow();
            String ActualTime = Long.toString(now.toMillis(false) / (1000 * 60));

            try {

            SensorsList = SensorsDB.getInformation(SensorsDB);           //Get the database
                if(SensorsList == null){
                    mDumpTextView.append("\n\nSENSORLIST == NULL\n");
                    SensorsDB.putInformation(SensorsDB, sensor, ActualTime);
                }
            for(int i=0; i < SensorsList.size(); i++){                    //Check if the sensor is already in the base
                if(SensorsList.get(i).getId() == Integer.parseInt(sensor.getNodeID())   ) //If the sensor's ID is in the database, we only update the data
                {
                    mDumpTextView.append("\n\t ID EXISTANT\n");
                    mDumpTextView.append("\n\t UPDATED !! \n");
                    break;
                }else{
                    if (i == SensorsList.size()){                       //If we reach the end of the database without finding our sensor, we add it to the DB
                        SensorsDB.putInformation(SensorsDB, sensor, ActualTime);
                    }
                }
            }
                SensorsList = SensorsDB.getInformation(SensorsDB);
                for (int i = 0; i < SensorsList.size(); i++) {                    //DEBUG: display sensors list
                    mDumpTextView.append(SensorsList.get(i).toString() + "\n");
                    mDumpTextView.append(String.valueOf(SensorsList.size()));
                }
            }catch (NullPointerException e){
                mDumpTextView.append(e.getMessage());
            }
        }catch(NumberFormatException e)
        {
            mDumpTextView.append(e.getMessage());
        }
    }

    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     * @param port
     */
    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}