package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.FireAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.RgbFadeAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;
import com.ctre.phoenix6.controls.TwinkleOffAnimation;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.AnimationDirectionValue;
import com.ctre.phoenix6.signals.LarsonBounceValue;
import com.ctre.phoenix6.signals.LossOfSignalBehaviorValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.wpilibj.util.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.controls.actuators.XCANLightController;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANLightControllerInfo;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

/**
 * WPI adapter for the CANdle light controller from CTRE.
 */
public class CANdleWpiAdapter extends XCANLightController {
    @AssistedFactory
    public abstract static class CANdleWpiAdapterFactory implements XCANLightController.XCANLightControllerFactory {
        public abstract CANdleWpiAdapter create(
                @Assisted("info") CANLightControllerInfo info);
    }

    public static final int SLOT_COUNT = 8;

    private static final Logger log = LogManager.getLogger(CANdleWpiAdapter.class);

    public final CANdle candle;

    @AssistedInject
    public CANdleWpiAdapter(
            @Assisted("info") CANLightControllerInfo deviceInfo,
            DevicePolice police) {
        super(deviceInfo, police);
        candle = new CANdle(deviceInfo.deviceId(), deviceInfo.canBusId().toPhoenixCANBus());

        CANdleConfiguration configuration = new CANdleConfiguration();
        configuration.LED.StripType = deviceInfo.outputConfig().stripType().toPhoenixStripTypeValue();
        configuration.LED.LossOfSignalBehavior = LossOfSignalBehaviorValue.DisableLEDs;
        configuration.LED.BrightnessScalar = deviceInfo.outputConfig().brightness();
        configuration.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Disabled;
        candle.getConfigurator().apply(configuration);

        // Clear all animation slots
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            clearAnimation(slot);
        }
    }

    @Override
    public DeviceHealth getHealth() {
        return this.candle.isConnected() ? DeviceHealth.Healthy : DeviceHealth.Unhealthy;
    }

    @Override
    public void clearAnimation(int slot) {
        this.candle.setControl(new EmptyAnimation(slot));
    }

    @Override
    public void colorFlow(int slot, Frequency frameRate, Color color) {
        this.candle.setControl(
                new ColorFlowAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withColor(toRGBWColor(color)));
    }

    @Override
    public void fire(int slot, Frequency frameRate, double brightness, double cooling, double sparking) {
        this.candle.setControl(
                new FireAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withBrightness(brightness)
                        .withCooling(cooling)
                        .withSparking(sparking));
    }

    @Override
    public void larson(int slot, Frequency frameRate, Color color, LarsonBounceValue bounceValue) {
        this.candle.setControl(
                new LarsonAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withColor(toRGBWColor(color))
                        .withBounceMode(bounceValue));
    }

    @Override
    public void rainbow(int slot, Frequency frameRate, double brightness, AnimationDirectionValue animationDirection) {
        this.candle.setControl(
                new RainbowAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withBrightness(brightness)
                        .withDirection(animationDirection));
    }

    @Override
    public void rgbFade(int slot, Frequency frameRate, double brightness) {
        this.candle.setControl(
                new RgbFadeAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withBrightness(brightness));
    }

    @Override
    public void singleFade(int slot, Frequency frameRate, Color color) {
        this.candle.setControl(
                new SingleFadeAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withColor(toRGBWColor(color)));
    }

    @Override
    public void strobe(int slot, Frequency frameRate, Color color) {
        this.candle.setControl(
                new StrobeAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withColor(toRGBWColor(color)));
    }

    @Override
    public void twinkleOff(int slot, Frequency frameRate, double density, Color color) {
        this.candle.setControl(
                new TwinkleOffAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withMaxLEDsOnProportion(density)
                        .withColor(toRGBWColor(color)));
    }

    @Override
    public void twinkle(int slot, Frequency frameRate, Color color) {
        this.candle.setControl(
                new TwinkleAnimation(getSlotStartIndex(slot), getSlotEndIndex(slot))
                        .withSlot(slot)
                        .withFrameRate(frameRate)
                        .withColor(toRGBWColor(color)));
    }

    /**
     * Converts a WPILib Color to a CTRE RGBWColor.
     */
    private RGBWColor toRGBWColor(Color color) {
        return new RGBWColor(color);
    }
}
