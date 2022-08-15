package edu.wpi.first.wpilibj;

import javax.inject.Inject;

import xbot.common.controls.sensors.DistanceSensor;
import xbot.common.controls.sensors.DistanceSensorPair;

public class MockDistanceSensorPair implements DistanceSensorPair {

    private DistanceSensor sensorA;
    private DistanceSensor sensorB;
    
    @Inject
    public MockDistanceSensorPair() {
        sensorA = new MockDistanceSensor();
        sensorB = new MockDistanceSensor();
    }
    
    @Override
    public DistanceSensor getSensorA() {
        return sensorA;
    }

    @Override
    public DistanceSensor getSensorB() {
        return sensorB;
    }

    @Override
    public void update() {
        // Intentionally left blank
    }
}
