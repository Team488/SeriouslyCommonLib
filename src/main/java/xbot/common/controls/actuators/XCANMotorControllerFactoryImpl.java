package xbot.common.controls.actuators;

import xbot.common.controls.actuators.wpi_adapters.CANSparkMaxWpiAdapter;
import xbot.common.controls.actuators.wpi_adapters.CANTalonFxWpiAdapter;
import xbot.common.controls.actuators.wpi_adapters.CANVictorSPXWpiAdapter;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class XCANMotorControllerFactoryImpl implements XCANMotorController.XCANMotorControllerFactory {

    private final CANTalonFxWpiAdapter.CANTalonFxWpiAdapterFactory talonFxFactory;
    private final CANSparkMaxWpiAdapter.CANSparkMaxWpiAdapterFactory sparkMaxFactory;
    private final CANVictorSPXWpiAdapter.CANVictorSPXWpiAdapterFactory victorSPXFactory;

    @Inject
    public XCANMotorControllerFactoryImpl(
            CANTalonFxWpiAdapter.CANTalonFxWpiAdapterFactory talonFxFactory,
            CANSparkMaxWpiAdapter.CANSparkMaxWpiAdapterFactory sparkMaxFactory,
            CANVictorSPXWpiAdapter.CANVictorSPXWpiAdapterFactory victorSPXFactory
    ) {
        this.talonFxFactory = talonFxFactory;
        this.sparkMaxFactory = sparkMaxFactory;
        this.victorSPXFactory = victorSPXFactory;
    }

    @Override
    public XCANMotorController create(
            CANMotorControllerInfo info,
            String owningSystemPrefix,
            String pidPropertyPrefix,
            XCANMotorControllerPIDProperties defaultPIDProperties) {
        XCANMotorController result;
        switch (info.type()) {
            case TalonFx -> {
                result = talonFxFactory.create(info, owningSystemPrefix, pidPropertyPrefix, defaultPIDProperties);
            }
            case SparkMax -> {
                result = sparkMaxFactory.create(info, owningSystemPrefix, pidPropertyPrefix, defaultPIDProperties);
            }
            case VictorSPX -> {
                result = victorSPXFactory.create(info, owningSystemPrefix, pidPropertyPrefix, defaultPIDProperties);
            }
            // TODO: can't throw exceptions unless they come from the RobotAssertionManager.
            default -> throw new IllegalArgumentException("Unknown motor controller type: " + info.type());
        }
        // Note: Motor controllers self-register with DataFrameRegistry in their constructor
        return result;
    }
}
