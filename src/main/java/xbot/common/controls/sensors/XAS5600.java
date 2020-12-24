package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.apache.log4j.Logger;

import xbot.common.controls.actuators.XCANTalon;

public class XAS5600 {

    XCANTalon talon;
    int lastValue = Integer.MIN_VALUE;
    protected Logger log = Logger.getLogger(XAS5600.class);

    @Inject
    public XAS5600(@Assisted("talon") XCANTalon talon) {
        this.talon = talon;
        log.info("Creating XAS5600 using Talon with ID:" + talon.getDeviceID());
    }

    public int getPosition() {
        int raw = talon.getPulseWidthRiseToFallUs();
        if (raw == 0) {
            int lastValue = this.lastValue;
            if (lastValue == Integer.MIN_VALUE) {
                return 0;
            }
            return lastValue;
        }
        int actualValue = Math.min(4096, raw - 128);
        lastValue = actualValue;
        return actualValue;
    }
}