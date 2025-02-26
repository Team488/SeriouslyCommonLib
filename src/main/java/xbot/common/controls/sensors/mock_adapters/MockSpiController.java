package xbot.common.controls.sensors.mock_adapters;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.SPI.Port;
import xbot.common.controls.sensors.XSpiController;
import xbot.common.controls.sensors.XEncoder.XEncoderFactory;
import xbot.common.controls.sensors.XSpiController.XSpiControllerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.ISimulatableSensor;

public class MockSpiController extends XSpiController implements ISimulatableSensor{

    @AssistedFactory
    public abstract static class MockSpiControllerFactory implements XSpiControllerFactory {
        public abstract MockSpiController create(
            @Assisted("port") Port port);
    }

    @AssistedInject
    public MockSpiController(
        @Assisted("port") Port port,
        DevicePolice police) {
        super(port, police);
    }

    @Override
    public int write(ByteBuffer dataToSend, int size) {
        return 0;
    }

    @Override
    public void close() {}

    @Override
    public void ingestSimulationData(JSONObject payload) {
        // TODO
        throw new UnsupportedOperationException("Unimplemented method 'ingestSimulationData'");
    }

}