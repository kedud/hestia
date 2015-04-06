package Collect;

/**
 * Created by Vincent on 05/04/15.
 */

public class SensorDataAggregator implements SensorInfo {

    private final Node node;
    private long[] values;
    private int minSeqno = Integer.MAX_VALUE;
    private int maxSeqno = Integer.MIN_VALUE;
    private int seqnoDelta = 0;
    private int dataCount;
    private int duplicates = 0;
    private int lost = 0;
    private int nodeRestartCount = 0;
    private int nextHopChangeCount = 0;
    private int lastNextHop = -1;
    private long shortestPeriod = Long.MAX_VALUE;
    private long longestPeriod = 0;

    public SensorDataAggregator(Node node) {
        this.node = node;
        this.values = new long[VALUES_COUNT];
    }

    public Node getNode() {
        return node;
    }

    public String getNodeID() {
        return node.getID();
    }

    public long getValue(int index) {
        return values[index];
    }

    public double getAverageValue(int index) {
        return dataCount > 0 ? (double)values[index] / (double)dataCount : 0;
    }

    public int getValueCount() {
        return values.length;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void addSensorData(SensorData data) {
        int seqn = data.getValue(SEQNO);
        int s = seqn + seqnoDelta;

        int bestNeighbor = data.getValue(BEST_NEIGHBOR);
        if (lastNextHop != bestNeighbor && lastNextHop >= 0) {
            nextHopChangeCount++;
        }
        lastNextHop = bestNeighbor;

        if (s <= maxSeqno) {
            // Check for duplicates among the last 5 packets
            for(int n = node.getSensorDataCount() - 1, i = n > 5 ? n - 5 : 0; i < n; i++) {
                SensorData sd = node.getSensorData(i);
                if (sd.getValue(SEQNO) != seqn || sd == data || sd.getValueCount() != data.getValueCount()) {
                    // Not a duplicate
                } else if (Math.abs(data.getNodeTime() - sd.getNodeTime()) > 180000) {
                    // Too long time between packets. Not a duplicate.
//          System.err.println("Too long time between packets with same seqno from "
//                  + data.getNode() + ": "
//                  + (Math.abs(data.getNodeTime() - sd.getNodeTime()) / 1000)
//                  + " sec, " + (n - i) + " packets ago");
                } else {
                    data.setDuplicate(true);

                    // Verify that the packet is a duplicate
                    for(int j = DATA_LEN2, m = data.getValueCount(); j < m; j++) {
                        if (sd.getValue(j) != data.getValue(j)) {
                            data.setDuplicate(false);
//              System.out.println("NOT Duplicate: " + data.getNode() + " ("
//                  + (n - i) + ": "
//                  + ((data.getNodeTime() - sd.getNodeTime()) / 1000) + "sek): "
//                  + seqn + " value[" + j + "]: " + sd.getValue(j) + " != "
//                  + data.getValue(j));
                            break;
                        }
                    }
                    if (data.isDuplicate()) {
//            System.out.println("Duplicate: " + data.getNode() + ": " + seqn
//                + ": "
//                + (Math.abs(data.getNodeTime() - sd.getNodeTime()) / 1000)
//                + " sec, " + (n - i) + " packets ago");
                        duplicates++;
                        break;
                    }
                }
            }
        }

        if (!data.isDuplicate()) {
            for (int i = 0, n = Math.min(VALUES_COUNT, data.getValueCount()); i < n; i++) {
                values[i] += data.getValue(i);
            }

            if (node.getSensorDataCount() > 1) {
                long timeDiff = data.getNodeTime() - node.getSensorData(node.getSensorDataCount() - 2).getNodeTime();
                if (timeDiff > longestPeriod) {
                    longestPeriod = timeDiff;
                }
                if (timeDiff < shortestPeriod) {
                    shortestPeriod = timeDiff;
                }
            }
            if (dataCount == 0) {
                // First packet from node.
            } else if (maxSeqno - s > 2) {
                // Handle sequence number overflow.
                seqnoDelta = maxSeqno + 1;
                s = seqnoDelta + seqn;
                if (seqn > 127) {
                    // Sequence number restarted at 128 (to separate node restarts
                    // from sequence number overflow).
                    seqn -= 128;
                    seqnoDelta -= 128;
                    s -= 128;
                } else {
                    // Sequence number restarted at 0. This is usually an indication that
                    // the node restarted.
                    nodeRestartCount++;
                }
                if (seqn > 0) {
                    lost += seqn;
                }
            } else if (s > maxSeqno + 1){
                lost += s - (maxSeqno + 1);
            }
            if (s < minSeqno) minSeqno = s;
            if (s > maxSeqno) maxSeqno = s;
            dataCount++;
        }
        data.setSeqno(s);
    }

    public void clear() {
        for (int i = 0, n = values.length; i < n; i++) {
            values[i] = 0L;
        }
        dataCount = 0;
        duplicates = 0;
        lost = 0;
        nodeRestartCount = 0;
        nextHopChangeCount = 0;
        lastNextHop = 0;
        minSeqno = Integer.MAX_VALUE;
        maxSeqno = Integer.MIN_VALUE;
        seqnoDelta = 0;
        shortestPeriod = Long.MAX_VALUE;
        longestPeriod = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = values.length; i < n; i++) {
            if (i > 0) sb.append(' ');
            sb.append(values[i]);
        }
        return sb.toString();
    }

    public double getCPUPower() {
        return (values[TIME_CPU] * POWER_CPU) / (values[TIME_CPU] + values[TIME_LPM]);
    }

    public double getLPMPower() {
        return (values[TIME_LPM] * POWER_LPM) / (values[TIME_CPU] + values[TIME_LPM]);
    }

    public double getListenPower() {
        return (values[TIME_LISTEN] * POWER_LISTEN) / (values[TIME_CPU] + values[TIME_LPM]);
    }

    public double getTransmitPower() {
        return (values[TIME_TRANSMIT] * POWER_TRANSMIT) / (values[TIME_CPU] + values[TIME_LPM]);
    }

    public double getAveragePower() {
        return (values[TIME_CPU] * POWER_CPU + values[TIME_LPM] * POWER_LPM
                + values[TIME_LISTEN] * POWER_LISTEN + values[TIME_TRANSMIT] * POWER_TRANSMIT)
                / (values[TIME_CPU] + values[TIME_LPM]);
    }

    public double getAverageDutyCycle(int index) {
        return (double)(values[index]) / (double)(values[TIME_CPU] + values[TIME_LPM]);
    }

    public long getPowerMeasureTime() {
        return (1000L * (values[TIME_CPU] + values[TIME_LPM])) / TICKS_PER_SECOND;
    }

    public double getAverageTemperature() {
        return dataCount > 0 ? (-39.6 + 0.01 * (values[TEMPERATURE] / dataCount)) : 0.0;
    }

    public double getAverageRtmetric() {
        return getAverageValue(RTMETRIC);
    }

    public double getAverageRadioIntensity() {
        return getAverageValue(RSSI);
    }

    public double getAverageLatency() {
        return getAverageValue(LATENCY) / 4096.0;
    }

    public double getAverageHumidity() {
        double v = 0.0;
        if (dataCount > 0) {
            v = -4.0 + 405.0 * (values[HUMIDITY] / dataCount) / 10000.0;
        }
        return v > 100 ? 100 : v;
    }

    public double getAverageLight1() {
        return 10.0 * getAverageValue(LIGHT1) / 7.0;
    }

    public double getAverageLight2() {
        return 46.0 * getAverageValue(LIGHT2) / 10.0;
    }

    public double getAverageBestNeighborETX() {
        return getAverageValue(BEST_NEIGHBOR_ETX) / 8.0;
    }

    public int getPacketCount() {
        return node.getSensorDataCount();
    }

    public int getNextHopChangeCount() {
        return nextHopChangeCount;
    }

    public int getEstimatedRestarts() {
        return nodeRestartCount;
    }

    public int getEstimatedLostCount() {
        return lost;
    }

    public int getDuplicateCount() {
        return duplicates;
    }

    public int getMinSeqno() {
        return minSeqno;
    }

    public int getMaxSeqno() {
        return maxSeqno;
    }

    public long getAveragePeriod() {
        if (dataCount > 1) {
            long first = node.getSensorData(0).getNodeTime();
            long last = node.getSensorData(node.getSensorDataCount() - 1).getNodeTime();
            return (last - first) / dataCount;
        }
        return 0;
    }

    public long getShortestPeriod() {
        return shortestPeriod < Long.MAX_VALUE ? shortestPeriod : 0;
    }

    public long getLongestPeriod() {
        return longestPeriod;
    }

}