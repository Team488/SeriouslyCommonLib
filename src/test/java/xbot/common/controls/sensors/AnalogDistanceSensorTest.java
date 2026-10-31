package xbot.common.controls.sensors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xbot.common.controls.sensors.mock_adapters.MockAnalogInput;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.DeviceAddress;
import xbot.common.injection.DevicePolice.DeviceType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

public class AnalogDistanceSensorTest extends BaseCommonLibTest {

    AnalogDistanceSensor sensor;

    @BeforeEach
    public void setup() {
        sensor = assertInstanceOf(AnalogDistanceSensor.class, getInjectorComponent().analogDistanceSensorFactory()
                .create(0, voltage -> voltage * 2, "DistanceTest"));
    }

    @Test
    public void factoryUsesMockVoltageInputAndAppliesCalibration() {
        MockAnalogInput input = assertInstanceOf(MockAnalogInput.class, sensor.input);
        input.setVoltage(1.25);
        assertEquals(2.5, sensor.getDistance(), 0.001);

        sensor.setVoltageOffset(0.25);
        sensor.setDistanceOffset(0.5);
        assertEquals(3.5, sensor.getDistance(), 0.001);
        assertSame(input, getInjectorComponent().devicePolice()
                .findDevice(DeviceAddress.channel(DeviceType.Analog, 0)).orElseThrow());
    }
}
