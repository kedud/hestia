package Collect;

/**
 * Created by Vincent on 05/04/15.
 */
public class Link {

    public final Node node;

    private double etx;
    private int quality = 100;
    private long lastActive = 0L;

    public Link(Node node) {
        this.node = node;
        this.lastActive = System.currentTimeMillis();
    }

    public Node getNode() {
        return node;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public double getETX() {
        return etx;
    }

    public void setETX(double etx) {
        this.etx = etx;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long time) {
        this.lastActive = time;
    }

}