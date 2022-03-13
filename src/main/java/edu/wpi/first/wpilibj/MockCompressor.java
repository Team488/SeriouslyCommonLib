package edu.wpi.first.wpilibj;

import com.google.inject.Inject;

import xbot.common.controls.actuators.XCompressor;

public class MockCompressor extends XCompressor {
    
    private boolean isEnabled = true;

    @Inject
    public MockCompressor() {

    }

    @Override
    public void disable() {
        isEnabled = false;
    }

    @Override
    public void enable() {
        isEnabled = true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
