package SensorDB;



public class Sensor {
    private int id;
    private String name;
    private float value;

    public Sensor(int id, String name, float value) {
        super();
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Sensor() {}

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
