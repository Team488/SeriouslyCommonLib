package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.XBaseIO;

public abstract class XDigitalInput implements XBaseIO {

    public abstract boolean get();
    
    public abstract LiveWindowSendable getLiveWindowSendable();
}
