package xbot.common.injection;

import edu.wpi.first.math.geometry.Transform3d;
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.subsystems.vision.CameraCapabilities;

import javax.inject.Inject;
import java.util.EnumSet;

public class MockCameraElectricalContract implements XCameraElectricalContract {

    @Inject
    public MockCameraElectricalContract() {
    }

    @Override
    public CameraInfo[] getCameraInfo() {
        return new CameraInfo[] {
                new CameraInfo("test", "test", new Transform3d(), EnumSet.of(CameraCapabilities.APRIL_TAG))
        };
    }
}
