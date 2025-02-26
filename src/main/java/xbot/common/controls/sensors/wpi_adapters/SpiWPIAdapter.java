package xbot.common.controls.sensors.wpi_adapters;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import xbot.common.injection.DevicePolice;
import xbot.common.controls.sensors.XSpiController;

public class SpiWPIAdapter extends XSpiController {
    private static final Character END_CHARACTER = '\uffff';
    private SPI internalSpi;
    private Logger log = LogManager.getLogger(SpiWPIAdapter.class);

    @AssistedFactory
    public abstract static class SpiWPIAdapterFactory implements XSpiControllerFactory {
        @Override
        public abstract SpiWPIAdapter create(
            @Assisted("port") Port port);
    }

    @AssistedInject
    public SpiWPIAdapter(
        @Assisted("port")Port port,
        DevicePolice police
    ) {
        super(port, police);
        internalSpi = new SPI(port);
    }

    @Override
    public int write(ByteBuffer dataToSend, int size) {
        //add end char \uffff to signal end of package
        int packetSize = size + Character.BYTES;
        ByteBuffer packet = ByteBuffer.allocate(packetSize);
        packet.put(dataToSend.array());
        packet.putChar(END_CHARACTER);

        return internalSpi.write(packet, packetSize);
    }

    @Override
    public void close() {
        internalSpi.close();
    }  
}
