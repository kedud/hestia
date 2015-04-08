package SensorDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Collect.SensorData;

public class SensorDataBase extends SQLiteOpenHelper
{
    public static final String SENSORS_TABLE_NAME = "Sensors";
    public static final String SENSORS_ID = "id";
    public static final String SENSORS_BATTERY_VOLTAGE = "battery_voltage";
    public static final String SENSORS_BATTERY_INDICATOR = "battery_indicator";
    public static final String SENSORS_LIGHT1= "lgiht1";
    public static final String SENSORS_LIGHT2 = "light2";
    public static final String SENSORS_TEMPERATURE = "temperature";
    public static final String SENSORS_HUMIDITY = "humidity";
    public static final String SENSORS_RSSI = "rssi";
    public static final String SENSORS_LAST_UPDATE = "last_update";



    ArrayList<Sensor> list = new ArrayList<Sensor>();

    public static final String SENSORS_TABLE_CREATE =
            "CREATE TABLE " + SENSORS_TABLE_NAME + " (" +
                    SENSORS_ID + " INTEGER PRIMARY KEY, " + //Id is given by the sensor
                    SENSORS_BATTERY_VOLTAGE + " REAL, " +
                    SENSORS_BATTERY_INDICATOR + " REAL, " +
                    SENSORS_LIGHT1 + " REAL, " +
                    SENSORS_LIGHT2 + " REAL, " +
                    SENSORS_TEMPERATURE + " REAL, " +
                    SENSORS_HUMIDITY + " REAL, " +
                    SENSORS_RSSI + " REAL, " +
                    SENSORS_LAST_UPDATE + " TEXT " +
                    ");";

    public static final String SENSORS_TABLE_DROP = "DROP TABLE IF EXISTS " + SENSORS_TABLE_NAME + " ;";

    protected final static int VERSION = 1;

    public SensorDataBase(Context context)
    {
        super(context, SENSORS_TABLE_NAME, null, VERSION);
        Log.d("MySQL operations", "Sensors DB created");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SENSORS_TABLE_CREATE);
        Log.d("MySQL operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SENSORS_TABLE_DROP);
        onCreate(db);
    }


    public void putInformation(SensorDataBase mdb, SensorData sensor_data, String last_update)
    {
        SQLiteDatabase SQ = mdb.getWritableDatabase();
        ContentValues CV = new ContentValues();
        CV.put(this.SENSORS_ID, Integer.parseInt(sensor_data.getNodeID()));
        CV.put(this.SENSORS_BATTERY_VOLTAGE, sensor_data.getBatteryVoltage());
        CV.put(this.SENSORS_BATTERY_INDICATOR, sensor_data.getBatteryIndicator());
        CV.put(this.SENSORS_LIGHT1, sensor_data.getLight1());
        CV.put(this.SENSORS_LIGHT2, sensor_data.getLight2());
        CV.put(this.SENSORS_TEMPERATURE, sensor_data.getTemperature());
        CV.put(this.SENSORS_HUMIDITY, sensor_data.getHumidity());
        CV.put(this.SENSORS_RSSI, sensor_data.getRadioIntensity());
        CV.put(this.SENSORS_LAST_UPDATE, last_update);

        long temp = SQ.insert(this.SENSORS_TABLE_NAME, null, CV);
        Log.d("MySQL operations", "One raw inserted");
    }

    public void deleteInformation(SensorDataBase mdb, int id)
    {
        SQLiteDatabase bdd = mdb.getWritableDatabase();
        bdd.delete(SENSORS_TABLE_NAME, SENSORS_ID + "=" + id, null);
        //bdd.delete(SENSORS_TABLE_NAME, 0 + " = " + id, null);
    }


    public void updateInformation(SensorDataBase mdb, SensorData sensor_data, String last_update){

        SQLiteDatabase SQ = mdb.getWritableDatabase();

        ContentValues CV = new ContentValues();
        CV.put(this.SENSORS_BATTERY_VOLTAGE, sensor_data.getBatteryVoltage());
        CV.put(this.SENSORS_BATTERY_INDICATOR, sensor_data.getBatteryIndicator());
        CV.put(this.SENSORS_LIGHT1, sensor_data.getLight1());
        CV.put(this.SENSORS_LIGHT2, sensor_data.getLight2());
        CV.put(this.SENSORS_TEMPERATURE, sensor_data.getTemperature());
        CV.put(this.SENSORS_HUMIDITY, sensor_data.getHumidity());
        CV.put(this.SENSORS_RSSI, sensor_data.getRadioIntensity());
        CV.put(this.SENSORS_LAST_UPDATE, last_update);
//        int id = Integer.parseInt(sensor_data.getNodeID());
        SQ.update(SENSORS_TABLE_NAME, CV, SENSORS_ID + "=" + sensor_data.getNodeID() , null);
    }

    public ArrayList<Sensor> getInformation(SensorDataBase mdb)
    {
        SQLiteDatabase SQ = mdb.getReadableDatabase();
        String[] columns = {
                this.SENSORS_ID,
                this.SENSORS_BATTERY_VOLTAGE,
                this.SENSORS_BATTERY_INDICATOR,
                this.SENSORS_LIGHT1,
                this.SENSORS_LIGHT2,
                this.SENSORS_TEMPERATURE,
                this.SENSORS_HUMIDITY,
                this.SENSORS_RSSI,
                this.SENSORS_LAST_UPDATE
        };
        Cursor CR = SQ.query(this.SENSORS_TABLE_NAME, columns, null, null, null, null, null);
        return cursorToList(CR);
    }



    private ArrayList<Sensor> cursorToList(Cursor c)
    {
        if (c.getCount() == 0)
            return null;



        Sensor sens;

        if(c.moveToFirst()) {
            do {
                sens = new Sensor();
                sens.setId(c.getInt(0));
                sens.setBattery_voltage(c.getFloat(1));
                sens.setBattery_indicator(c.getFloat(2));
                sens.setLight1(c.getFloat(3));
                sens.setLight2(c.getFloat(4));
                sens.setTemperature(c.getFloat(5));
                sens.setHumidity(c.getFloat(6));
                sens.setRssi(c.getFloat(7));
                sens.setLast_update(c.getString(8));
                list.add(sens);

            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

}
