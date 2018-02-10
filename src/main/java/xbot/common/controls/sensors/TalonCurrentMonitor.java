package xbot.common.controls.sensors;

import java.util.ArrayDeque;
import xbot.common.controls.actuators.XCANTalon;

public class TalonCurrentMonitor {

    XCANTalon talon;
    final int current_averaging_window=25;
    ArrayDeque<Double> currentHistory;

    public TalonCurrentMonitor(XCANTalon talon) {
        this.talon = talon;
        currentHistory = new ArrayDeque<Double>();
    }

    public void updateCurrent() {
        currentHistory.addFirst(talon.getOutputCurrent());
        if (currentHistory.size() > current_averaging_window) {
            currentHistory.removeLast();
        }
    }

    public  double calculateAverageCurrent() {
        double sum = 0;
        for (Double current : currentHistory) {
            sum += current;
        }
        return sum / currentHistory.size();
    }

    public double calculatePeakCurrent() {
        double peakCurrent = 0;
        currentHistory.addFirst(talon.getOutputCurrent());
        if (!currentHistory.isEmpty()) {
            for (Double current : currentHistory) {
                if (peakCurrent < current) {
                    peakCurrent = current;
                }
            }
        }
        return peakCurrent;
    }
}
