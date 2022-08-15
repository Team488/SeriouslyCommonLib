package xbot.common.subsystems;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.math.PIDPropertyManager;
import xbot.common.properties.PropertyFactory;

public class BaseXCANTalonPairSpeedControlledSubsystem extends BaseXCANTalonSpeedControlledSubsystem {

    protected XCANTalon followerMotor;
    
    /**
     * @param systemName What the system is called. This will apply to various Properties.
     * @param masterChannel The CAN index of the master motor (or the only motor, for a simple system)
     * @param followChannel The CAN index of the follow motor (-1 if no follow motor)
     * @param factory The WPIFactory
     * @param pidPropertyManager The default PIDF values the system should use
     * @param propManager The PropertyFactory
     */
    public BaseXCANTalonPairSpeedControlledSubsystem(
            String name,
            int masterChannel,
            int followChannel,
            boolean invertMaster,
            boolean invertMasterSensor,
            boolean invertFollower,
            XCANTalonFactory factory,
            PIDPropertyManager pidPropertyManager,
            PropertyFactory propManager) {
        super(name, masterChannel, invertMaster, invertMasterSensor, factory, pidPropertyManager, propManager);
        
        followerMotor = factory.create(new CANTalonInfo(followChannel, false));
        initializeFollowerMotorConfiguration(invertFollower);
    }
    
    protected void initializeFollowerMotorConfiguration(boolean motorInverted) {
        followerMotor.follow(masterMotor);
        followerMotor.setInverted(motorInverted);
    }

}