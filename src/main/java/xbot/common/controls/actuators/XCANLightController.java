package xbot.common.controls.actuators;

import com.ctre.phoenix6.signals.AnimationDirectionValue;
import com.ctre.phoenix6.signals.LarsonBounceValue;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.wpilibj.util.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANLightControllerInfo;
import xbot.common.injection.electrical_contract.CANLightControllerOutputConfig;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

/**
 * Abstract base class for CAN-based light controllers.
 */
public abstract class XCANLightController {
    protected final CANBusId busId;
    protected final int deviceId;
    protected final CANLightControllerOutputConfig config;

    private static final Logger log = LogManager.getLogger(XCANLightController.class);

    /**
     * Types of animations supported by the light controller.
     */
    public enum AnimationType {
        None,
        ColorFlow,
        Fire,
        Larson,
        Rainbow,
        RgbFade,
        SingleFade,
        Strobe,
        Twinkle,
        TwinkleOff,
    }

    /**
     * Factory interface for creating instances of XCANLightController.
     */
    public interface XCANLightControllerFactory {
        XCANLightController create(
                CANLightControllerInfo info
        );
    }

    /**
     * Constructor for XCANLightController.
     * @param info The CAN light controller information.
     * @param police The device police for registering the device.
     */
    protected XCANLightController(CANLightControllerInfo info, DevicePolice police) {
        this.busId = info.canBusId();
        this.deviceId = info.deviceId();
        this.config = info.outputConfig();

        police.registerDevice(DevicePolice.DeviceType.CAN, this.busId, this.deviceId, this);
    }

    public abstract DeviceHealth getHealth();

    protected int getSlotStartIndex(int slot) {
        var slotLengths = config.ledStripLengths();

        if (slot >= slotLengths.length) {
            log.error("Requested slot {} is out of bounds, only {} LED strips were configured", slot, slotLengths.length);
            return 0;
        }

        int startIndex = 0;
        for (int i = 0; i < slot; i++) {
            startIndex += slotLengths[i];
        }
        return startIndex;
    }

    protected int getSlotEndIndex(int slot) {
        var slotLengths = config.ledStripLengths();

        if (slot >= slotLengths.length) {
            log.error("Requested slot {} is out of bounds, only {} LED strips were configured", slot, slotLengths.length);
            return 0;
        }

        int endIndex = 0;
        for (int i = 0; i <= slot; i++) {
            endIndex += slotLengths[i];
        }
        return endIndex - 1;
    }

    public abstract void clearAnimation(int slot);

    public abstract void colorFlow(int slot, Frequency frameRate, Color color);

    public abstract void fire(int slot, Frequency frameRate, double brightness, double cooling, double sparking);

    public abstract void larson(int slot, Frequency frameRate, Color color, LarsonBounceValue bounceValue);

    public abstract void rainbow(int slot, Frequency frameRate, double brightness, AnimationDirectionValue animationDirection);

    public abstract void rgbFade(int slot, Frequency frameRate, double brightness);

    public abstract void singleFade(int slot, Frequency frameRate, Color color);

    public abstract void strobe(int slot, Frequency frameRate, Color color);

    public abstract void twinkleOff(int slot, Frequency frameRate, int density, Color color);

    public abstract void twinkle(int slot, Frequency frameRate, Color color);
}
