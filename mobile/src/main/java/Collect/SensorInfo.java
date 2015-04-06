package Collect;

/**
 * Created by Vincent on 05/04/15.
 */

public interface SensorInfo {

    public static final long TICKS_PER_SECOND = 4096L;
    public static final double VOLTAGE = 3;
    public static final double POWER_CPU = 1.800 * VOLTAGE;       /* mW */
    public static final double POWER_LPM = 0.0545 * VOLTAGE;      /* mW */
    public static final double POWER_TRANSMIT = 17.7 * VOLTAGE;   /* mW */
    public static final double POWER_LISTEN = 20.0 * VOLTAGE;     /* mW */

    public static final int DATA_LEN = 0;
    public static final int TIMESTAMP1 = 1;
    public static final int TIMESTAMP2 = 2;
    public static final int TIMESYNCTIMESTAMP = 3;
    public static final int NODE_ID = 4;
    public static final int SEQNO = 5;
    public static final int HOPS = 6;
    public static final int LATENCY = 7;
    public static final int DATA_LEN2 = 8;
    public static final int CLOCK = 9;
    public static final int TIMESYNCHTIME = 10;
    public static final int TIME_CPU = 11;
    public static final int TIME_LPM = 12;
    public static final int TIME_TRANSMIT = 13;
    public static final int TIME_LISTEN = 14;
    public static final int BEST_NEIGHBOR = 15;
    public static final int BEST_NEIGHBOR_ETX = 16;
    public static final int RTMETRIC = 17;
    public static final int NUM_NEIGHBORS = 18;
    public static final int BEACON_INTERVAL = 19;
    public static final int BATTERY_VOLTAGE = 20;
    public static final int BATTERY_INDICATOR = 21;
    public static final int LIGHT1 = 22;
    public static final int LIGHT2 = 23;
    public static final int TEMPERATURE = 24;
    public static final int HUMIDITY = 25;
    public static final int RSSI = 26;

    public static final int VALUES_COUNT = 30;

}