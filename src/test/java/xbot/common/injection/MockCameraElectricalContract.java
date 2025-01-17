package xbot.common.injection;

import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;

import javax.inject.Inject;

public class MockCameraElectricalContract implements XCameraElectricalContract {

    @Inject
    public MockCameraElectricalContract() {
    }

    @Override
    public CameraInfo[] getCameraInfo() {
        return new CameraInfo[0];
    }
}
