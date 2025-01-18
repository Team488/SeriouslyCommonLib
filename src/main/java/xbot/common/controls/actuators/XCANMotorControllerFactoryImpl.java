package xbot.common.controls.actuators;

import xbot.common.controls.actuators.wpi_adapters.CANSparkMaxWpiAdapter;
import xbot.common.controls.actuators.wpi_adapters.CANTalonFxWpiAdapter;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class XCANMotorControllerFactoryImpl implements XCANMotorController.XCANMotorControllerFactory {

    private final CANTalonFxWpiAdapter.CANTalonFxWpiAdapterFactory talonFxFactory;
    private final CANSparkMaxWpiAdapter.CANSparkMaxWpiAdapterFactory sparkMaxFactory;

    @Inject
    public XCANMotorControllerFactoryImpl(
            CANTalonFxWpiAdapter.CANTalonFxWpiAdapterFactory talonFxFactory,
            CANSparkMaxWpiAdapter.CANSparkMaxWpiAdapterFactory sparkMaxFactory
    ) {
        this.talonFxFactory = talonFxFactory;
        this.sparkMaxFactory = sparkMaxFactory;
    }

    @Override
    public XCANMotorController create(
            CANMotorControllerInfo info,
            String owningSystemPrefix,
            String pidPropertyPrefix,
            XCANMotorControllerPIDProperties defaultPIDProperties) {
        switch (info.type()) {
            case TalonFx -> {
                return talonFxFactory.create(info, owningSystemPrefix, pidPropertyPrefix, defaultPIDProperties);
            }
            case SparkMax -> {
                return sparkMaxFactory.create(info, owningSystemPrefix, pidPropertyPrefix, defaultPIDProperties);
            }
            default -> throw new IllegalArgumentException("Unknown motor controller type: " + info.type());
        }
    }
}
