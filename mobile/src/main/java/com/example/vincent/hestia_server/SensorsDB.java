package com.example.vincent.hestia_server;

//import android.database.sqlite;

/**
 * Created by Vincent on 17/03/2015.
 */
public final class SensorsDB {

    public SensorsDB(){};

    public static abstract class FeedSensor /*implements BaseColumns*/ {


        public static final String TABLE_NAME = "sensors";
        public static final String COLUMN_NAME_SENSOR_ID = "sensorid";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_VALUE = "value";

    }
}
