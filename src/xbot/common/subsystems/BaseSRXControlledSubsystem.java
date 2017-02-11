package xbot.common.subsystems;

import org.apache.log4j.Logger;

import competition.subsystems.RobotSide;
import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PIDManager;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public abstract class BaseSRXControlledSubsystem extends BaseSubsystem {
    
    private static Logger log = Logger.getLogger(BaseSRXControlledSubsystem.class);
    
    protected final XCANTalon systemMotor;
    private final RobotSide side;
    
    protected final DoubleProperty systemCurrentSpeed;
    protected final DoubleProperty systemTargetSpeed;
    private final DoubleProperty systemOutputPower;
    private final DoubleProperty systemTalonError;
    private final BooleanProperty atSpeedProp;
    private final BooleanProperty enableSystemLogging;
    
    private final DoubleProperty systemSpeedThresh;
    
    private final PIDManager pidProperties;
    
    public ShooterBeltSubsystem(int motor, RobotSide side, WPIFactory factory, XPropertyManager propManager){
        log.info("Creating " + side + " ShooterBelt");

        this.side = side;
    

}
