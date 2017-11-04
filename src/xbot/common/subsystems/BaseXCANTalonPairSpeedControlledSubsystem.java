package xbot.common.subsystems;

import com.ctre.CANTalon.TalonControlMode;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.math.PIDPropertyManager;
import xbot.common.properties.XPropertyManager;

public class BaseXCANTalonPairSpeedControlledSubsystem extends BaseXCANTalonSpeedControlledSubsystem {

    protected XCANTalon followerMotor;
    
    /**
     * @param systemName What the system is called. This will apply to various Properties.
     * @param masterChannel The CAN index of the master motor (or the only motor, for a simple system)
     * @param followChannel The CAN index of the follow motor (-1 if no follow motor)
     * @param factory The WPIFactory
     * @param pidPropertyManager The default PIDF values the system should use
     * @param propManager The XPropertyManager
     */
    public BaseXCANTalonPairSpeedControlledSubsystem(
            String name,
            int masterChannel,
            int followChannel,
            boolean invertMaster,
            boolean invertMasterSensor,
            boolean invertFollower,
            WPIFactory factory,
            PIDPropertyManager pidPropertyManager,
            XPropertyManager propManager) {
        super(name, masterChannel, invertMaster, invertMasterSensor, factory, pidPropertyManager, propManager);
        
        followerMotor = factory.getCANTalonSpeedController(followChannel);
        initializeFollowerMotorConfiguration(invertFollower);
    }
    
    protected void initializeFollowerMotorConfiguration(boolean motorInverted) {
        followerMotor.setControlMode(TalonControlMode.Follower);
        followerMotor.
        followerMotor.setInverted(motorInverted);
        followerMotor.set(masterChannel);
    }

}