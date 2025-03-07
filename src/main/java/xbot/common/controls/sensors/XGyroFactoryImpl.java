package xbot.common.controls.sensors;

import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.wpi_adapters.Pigeon2Adapter;
import xbot.common.injection.electrical_contract.IMUInfo;

import javax.inject.Inject;

public class XGyroFactoryImpl extends XGyro.XGyroFactory {
    private final Pigeon2Adapter.Pigeon2AdapterFactory pigeon2Factory;
    private final InertialMeasurementUnitAdapter.InertialMeasurementUnitAdapterFactory navXFactory;

    @Inject
    public XGyroFactoryImpl(
            Pigeon2Adapter.Pigeon2AdapterFactory pigeon2Factory,
            InertialMeasurementUnitAdapter.InertialMeasurementUnitAdapterFactory navXFactory
    ) {
        this.pigeon2Factory = pigeon2Factory;
        this.navXFactory = navXFactory;
    }

    @Override
    public XGyro create(IMUInfo imuInfo) {
        switch (imuInfo.imuType()) {
            case pigeon2 -> {
                return pigeon2Factory.create(imuInfo);
            }
            default -> {
                return navXFactory.create(imuInfo);
            }
        }
    }
}
