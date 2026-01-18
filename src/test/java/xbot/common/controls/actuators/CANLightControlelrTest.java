package xbot.common.controls.actuators;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANLightControllerInfo;
import xbot.common.injection.electrical_contract.CANLightControllerOutputConfig;
import xbot.common.injection.electrical_contract.LEDStripType;
import xbot.common.injection.electrical_contract.LightControllerType;

import static org.junit.Assert.assertEquals;

public class CANLightControlelrTest extends BaseCommonLibTest {
    @Test
    public void testSlotToIndexTranslation() {
        var factory = getInjectorComponent().lightControllerFactory();
        var lightController = factory.create(
                new CANLightControllerInfo(
                        "test",
                        LightControllerType.Candle,
                        CANBusId.RIO,
                        999,
                        new CANLightControllerOutputConfig(
                                LEDStripType.RGB,
                                1.0,
                                new int[]{8, 30, 30}
                        )));

        assertEquals(0, lightController.getSlotStartIndex(0));
        assertEquals(7, lightController.getSlotEndIndex(0));

        assertEquals(8, lightController.getSlotStartIndex(1));
        assertEquals(37, lightController.getSlotEndIndex(1));

        assertEquals(38, lightController.getSlotStartIndex(2));
        assertEquals(67, lightController.getSlotEndIndex(2));

        assertEquals(0, lightController.getSlotStartIndex(3));
        assertEquals(0, lightController.getSlotEndIndex(3));
    }
}
