package xbot.common.controls.actuators.mock_adapters;

import com.ctre.phoenix6.signals.AnimationDirectionValue;
import com.ctre.phoenix6.signals.LarsonBounceValue;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.wpilibj.util.Color;
import xbot.common.controls.actuators.XCANLightController;
import xbot.common.controls.actuators.wpi_adapters.CANdleWpiAdapter;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANLightControllerInfo;
import xbot.common.resiliency.DeviceHealth;

/**
 * Mock adapter for the CAN light controller for testing purposes.
 */
public class MockCANLightController extends XCANLightController {
    @AssistedFactory
    public abstract static class MockCANLightControllerFactory implements XCANLightController.XCANLightControllerFactory {
        public abstract CANdleWpiAdapter create(
                @Assisted("info") CANLightControllerInfo info);
    }

    @AssistedInject
    protected MockCANLightController(@Assisted("info") CANLightControllerInfo info,
                                     DevicePolice police) {
        super(info, police);
    }

    @Override
    public DeviceHealth getHealth() {
        return DeviceHealth.Healthy;
    }

    @Override
    public void clearAnimation(int slot) {

    }

    @Override
    public void colorFlow(int slot, Frequency frameRate, Color color) {

    }

    @Override
    public void fire(int slot, Frequency frameRate, double brightness, double cooling, double sparking) {

    }

    @Override
    public void larson(int slot, Frequency frameRate, Color color, LarsonBounceValue bounceValue) {

    }

    @Override
    public void rainbow(int slot, Frequency frameRate, double brightness, AnimationDirectionValue animationDirection) {

    }

    @Override
    public void rgbFade(int slot, Frequency frameRate, double brightness) {

    }

    @Override
    public void singleFade(int slot, Frequency frameRate, Color color) {

    }

    @Override
    public void strobe(int slot, Frequency frameRate, Color color) {

    }

    @Override
    public void twinkleOff(int slot, Frequency frameRate, double density, Color color) {

    }

    @Override
    public void twinkle(int slot, Frequency frameRate, Color color) {

    }
}
