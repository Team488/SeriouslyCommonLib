package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XCANTalon;

public class XAS5600 {

    XCANTalon talon;
    int lastValue = Integer.MIN_VALUE;
    protected Logger log = Logger.getLogger(XAS5600.class);

    @AssistedFactory
    public abstract static class XAS5600Factory {
        public abstract XAS5600 create(@Assisted("talon") XCANTalon talon);
    }

    @AssistedInject
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