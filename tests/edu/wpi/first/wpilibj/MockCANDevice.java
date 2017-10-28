package edu.wpi.first.wpilibj;

import xbot.common.controls.misc.XCANDevice;

public class MockCANDevice implements XCANDevice {

    @Override
    public void send(byte[] data) {
        
    }

    @Override
    public byte[] receive() {
        return null;
    }

}
