package xbot.common.injection.components;

import javax.inject.Named;

import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.command.XScheduler;
import xbot.common.controls.actuators.XCANSparkMax.XCANSparkMaxFactory;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.controls.actuators.XCANVictorSPX.XCANVictorSPXFactory;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.controls.actuators.XDigitalOutput.XDigitalOutputFactory;
import xbot.common.controls.actuators.XDoubleSolenoid.XDoubleSolenoidFactory;
import xbot.common.controls.actuators.XPWM.XPWMFactory;
import xbot.common.controls.actuators.XRelay.XRelayFactory;
import xbot.common.controls.actuators.XServo.XServoFactory;
import xbot.common.controls.actuators.XSolenoid.XSolenoidFactory;
import xbot.common.controls.actuators.XSpeedController.XSpeedControllerFactory;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.XAS5600.XAS5600Factory;
import xbot.common.controls.sensors.XAbsoluteEncoder.XAbsoluteEncoderFactory;
import xbot.common.controls.sensors.XAnalogDistanceSensor.XAnalogDistanceSensorFactory;
import xbot.common.controls.sensors.XAnalogInput.XAnalogInputFactory;
import xbot.common.controls.sensors.XCANCoder.XCANCoderFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XEncoder.XEncoderFactory;
import xbot.common.controls.sensors.XFTCGamepad.XFTCGamepadFactory;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.controls.sensors.XJoystick.XJoystickFactory;
import xbot.common.controls.sensors.XLidarLite.XLidarLiteFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.XXboxController.XXboxControllerFactory;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.ChordTrigger.ChordTriggerFactory;
import xbot.common.controls.sensors.buttons.VirtualTrigger.VirtualTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.logging.RobotSession;
import xbot.common.logic.CalibrationDecider.CalibrationDeciderFactory;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.logic.StallDetector.StallDetectorFactory;
import xbot.common.logic.VelocityThrottleModule.VelocityThrottleModuleFactory;
import xbot.common.math.FieldPosePropertyManager.FieldPosePropertyManagerFactory;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.math.PIDPropertyManager.PIDPropertyManagerFactory;
import xbot.common.networking.XZeromqListener.XZeromqListenerFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;
import xbot.common.simulation.SimulationPayloadDistributor;
import xbot.common.simulation.WebotsClient;
import xbot.common.subsystems.autonomous.AutonomousCommandSelector;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.control_logic.HeadingAssistModule.HeadingAssistModuleFactory;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

/**
 * Base class for all Components that provides methods to get implementations from DI.
 * Implementations of this abstract class map Modules to the Component. Dagger will automatically
 * generate a class with the prefix "Dagger" in the same package.
 */
public abstract class BaseComponent {
    public abstract XTimerImpl timerImplementation();

    public abstract XSettableTimerImpl settableTimerImplementation();

    public abstract ITableProxy tableProxy();

    public abstract @Named(XPropertyManager.IN_MEMORY_STORE_NAME) ITableProxy inMemoryTableProxy();

    public abstract PermanentStorage permanentStorage();

    public abstract RobotAssertionManager robotAssertionManager();

    public abstract DevicePolice devicePolice();

    public abstract SmartDashboardCommandPutter smartDashboardCommandPutter();

    public abstract XScheduler scheduler();

    public abstract XPropertyManager propertyManager();

    public abstract PropertyFactory propertyFactory();

    public abstract FieldPosePropertyManagerFactory fieldPosePropertyManagerFactory();

    public abstract AutonomousCommandSelector autonomousCommandSelector();

    public abstract RobotSession robotSession();

    public abstract WebotsClient webotsClient();

    public abstract SimulationPayloadDistributor simulationPayloadDistributor();

    public abstract PIDManagerFactory pidFactory();

    public abstract PIDPropertyManagerFactory pidPropertyManagerFactory();

    public abstract XPowerDistributionPanelFactory powerDistributionPanelFactory();

    public abstract XJoystickFactory joystickFactory();

    public abstract AdvancedJoystickButtonTriggerFactory joystickButtonFactory();

    public abstract AdvancedPovButtonTriggerFactory povButtonFactory();

    public abstract AnalogHIDButtonTriggerFactory analogHidButtonFactory();

    public abstract XXboxControllerFactory xboxControllerFactory();

    public abstract XFTCGamepadFactory ftcGamepadFactory();

    public abstract XRumbleManagerFactory rumbleManagerFactory();

    public abstract ChordTriggerFactory chordButtonFactory();

    public abstract VirtualTriggerFactory virtualButtonFactory();

    public abstract HumanVsMachineDeciderFactory humanVsMachineDeciderFactory();

    public abstract CalibrationDeciderFactory calibrationDeciderFactory();

    public abstract StallDetectorFactory stallDetectorFactory();

    public abstract VelocityThrottleModuleFactory velocityThrottleModuleFactory();

    public abstract XAnalogInputFactory analogInputFactory();

    public abstract XDigitalInputFactory digitalInputFactory();

    public abstract XDigitalOutputFactory digitalOutputFactory();

    public abstract XPWMFactory pwmFactory();

    public abstract XCompressorFactory compressorFactory();

    public abstract XGyroFactory gyroFactory();

    public abstract XServoFactory servoFactory();

    public abstract HeadingModuleFactory headingModuleFactory();

    public abstract HeadingAssistModuleFactory headingAssistModuleFactory();

    public abstract XEncoderFactory encoderFactory();

    public abstract XAbsoluteEncoderFactory absoluteEncoderFactory();

    public abstract XCANCoderFactory canCoderFactory();

    public abstract XSolenoidFactory solenoidFactory();

    public abstract XRelayFactory relayFactory();

    public abstract XDoubleSolenoidFactory doubleSolenoidFactory();

    public abstract XAnalogDistanceSensorFactory analogDistanceSensorFactory();

    public abstract XCANTalonFactory canTalonFactory();

    public abstract XAS5600Factory as5600Factory();

    public abstract XCANVictorSPXFactory canVictorSpxFactory();

    public abstract XCANSparkMaxFactory canSparkMaxFactory();

    public abstract XLidarLiteFactory lidarLiteFactory();

    public abstract XSpeedControllerFactory speedControllerFactory();

    public abstract XZeromqListenerFactory zeromqListenerFactory();

    public abstract BaseDriveSubsystem driveSubsystem();
    
    public abstract BasePoseSubsystem poseSubsystem();
}
