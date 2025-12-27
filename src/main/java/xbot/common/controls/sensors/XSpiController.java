package xbot.common.controls.sensors;

import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.SPI;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XSpiController {
    public interface XSpiControllerFactory {
        XSpiController create(
                SPI.Port port);
    }

    public XSpiController(
            SPI.Port port,
            DevicePolice police) {

        police.registerDevice(DeviceType.SPI, port.value, this);
    }

    public abstract int write(ByteBuffer dataToSend, int size);
    public abstract void close();
}
