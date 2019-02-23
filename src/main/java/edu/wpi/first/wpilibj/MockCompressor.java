package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.actuators.XCompressor;

@Singleton
public class MockCompressor extends XCompressor {
    
    private double current;

    @Inject
    public MockCompressor(double initialCurrent) {
        current = initialCurrent;
    }

    public double getCompressorCurrent(){
        return current;
    }
    public void setCompressorCurrent(double newCurrent){
        current = newCurrent;
    }
}
