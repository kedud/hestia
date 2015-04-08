package SensorDB;

/*
Sensor class used with the database
 */

public class Sensor {

    private int id;
    private float battery_voltage;
    private float battery_indicator;
    private float light1;
    private float light2;
    private float temperature;
    private float humidity;
    private float rssi;
    private String last_update;



    public Sensor(int id,
                  float battery_voltage,
                  float battery_indicator,
                  float light1,
                  float light2,
                  float temperature,
                  float humidity,
                  float rssi,
                  String last_update
    ) {
        super();
        this.setId(id);
        this.setBattery_voltage(battery_voltage);
        this.setBattery_indicator(battery_indicator);
        this.setLight1(light1);
        this.setLight2(light2);
        this.setTemperature(temperature);
        this.setHumidity(humidity);
        this.setRssi(rssi);
        this.setLast_update(last_update);

    }



    public Sensor() {}

    public String toString(){
        return id +
                " " +battery_voltage +
                " " +battery_indicator+
                " " +light1+
                " " +light2+
                " " +temperature+
                " " +humidity+
                " " +rssi+
                " " +last_update;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getBattery_voltage() {
        return battery_voltage;
    }

    public void setBattery_voltage(float battery_voltage) {
        this.battery_voltage = battery_voltage;
    }

    public float getBattery_indicator() {
        return battery_indicator;
    }

    public void setBattery_indicator(float battery_indicator) {
        this.battery_indicator = battery_indicator;
    }

    public float getLight1() {
        return light1;
    }

    public void setLight1(float light1) {
        this.light1 = light1;
    }

    public float getLight2() {
        return light2;
    }

    public void setLight2(float light2) {
        this.light2 = light2;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getRssi() {
        return rssi;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }
}
