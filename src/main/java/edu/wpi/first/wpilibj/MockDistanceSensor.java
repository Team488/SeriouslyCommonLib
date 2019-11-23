package edu.wpi.first.wpilibj;

import xbot.common.controls.sensors.DistanceSensor;

public class MockDistanceSensor implements DistanceSensor {

    double distance;

    public MockDistanceSensor() {
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void incrementDistance(double delta) {
        this.distance += delta;
    }

    @Override
    public void setAveraging(boolean shouldAverage) {
        // Nothing to do in mock implementation
    }

}
