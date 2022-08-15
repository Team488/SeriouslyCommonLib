package edu.wpi.first.wpilibj;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XCompressor;

public class MockCompressor extends XCompressor {
    
    private boolean isEnabled = true;

    @AssistedFactory
    public abstract static class MockCompressorFactory implements XCompressorFactory {
        public abstract MockCompressor create();
    }

    @AssistedInject
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
