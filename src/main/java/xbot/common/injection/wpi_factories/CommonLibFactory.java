package xbot.common.injection.wpi_factories;

import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.I2C;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANVictorSPX;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XDoubleSolenoid;
import xbot.common.controls.actuators.XPWM;
import xbot.common.controls.actuators.XRelay;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.sensors.AdvancedButton;
import xbot.common.controls.sensors.AdvancedJoystickButton;
import xbot.common.controls.sensors.AdvancedPovButton;
import xbot.common.controls.sensors.AnalogHIDButton;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.ChordButton;
import xbot.common.controls.sensors.VirtualButton;
import xbot.common.controls.sensors.XAS5600;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logic.CalibrationDecider;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.StallDetector;
import xbot.common.logic.VelocityThrottleModule;
import xbot.common.math.FieldPose;
import xbot.common.math.FieldPosePropertyManager;
import xbot.common.math.PIDManager;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.subsystems.drive.control_logic.HeadingAssistModule;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.feedback.RumbleManager;

public interface CommonLibFactory {

        public XPowerDistributionPanel createPowerDistributionPanel();

        public XJoystick createJoystick(@Assisted("port") int port, @Assisted("numButtons") int numButtons);

        public XFTCGamepad createGamepad(@Assisted("port") int port, @Assisted("numButtons") int numButtons);

        public XEncoder createEncoder(@Assisted("name") String name, @Assisted("aChannel") int aChannel,
                        @Assisted("bChannel") int bChannel,
                        @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse);

        public XDigitalInput createDigitalInput(@Assisted("channel") int channel);

        public XAnalogInput createAnalogInput(@Assisted("channel") int channel);

        public XXboxController createXboxController(@Assisted("port") int port);

        public XSolenoid createSolenoid(@Assisted("channel") int channel);

        public XDigitalOutput createDigitalOutput(@Assisted("channel") int channel);

        public XServo createServo(@Assisted("channel") int channel);

        public XPWM createPWM(@Assisted("channel") int channel);

        public XRelay createRelay(@Assisted("channel") int channel);

        public XSpeedController createSpeedController(@Assisted("channel") int channel);

        public XCANTalon createCANTalon(@Assisted("deviceInfo") CANTalonInfo deviceInfo);

        public XGyro createGyro();

        public XGyro createGyro(@Assisted("channel") int channel);

        public XCompressor createCompressor();

        public XLidarLite createLidarLite(@Assisted("port") I2C.Port port, @Assisted("prefix") String prefix);

        //public XAnalogDistanceSensor createAnalogDistanceSensor(@Assisted("channel") int channel,
        //                @Assisted("voltageMap") DoubleFunction<Double> voltageMap, @Assisted("prefix") String prefix);

        public AdvancedJoystickButton createAdvancedJoystickButton(@Assisted("joystick") XJoystick joystick,
                        @Assisted("buttonNumber") int buttonNumber);

        public ChordButton createChordButton(@Assisted("a") AdvancedButton a,
                        @Assisted("b") AdvancedButton b);

        public VirtualButton createVirtualButton();

        public AnalogHIDButton createAnalogHIDButton(@Assisted("joystick") XJoystick joystick,
                        @Assisted("axisNumber") int axisNumber,
                        @Assisted("analogMinThreshold") double analogMinThreshold,
                        @Assisted("analogMaxThreshold") double analogMaxThreshold);

        public AnalogHIDButton createAnalogHIDButton(@Assisted("joystick") XJoystick joystick,
                        @Assisted("desc") AnalogHIDDescription desc);

        public AdvancedPovButton createAdvancedPovButton(@Assisted("joystick") XJoystick joystick,
                        @Assisted("povNumber") int povNumber);

        public HeadingModule createHeadingModule(@Assisted("headingDrivePid") PIDManager headingDrivePid);

        /**
         * Creates a heading assist module. Can either hold an orientation, or resist
         * rotational motion.
         * 
         * @param headingModule Tune this one to rotate to a target orientation (PD, or
         *                      PID controller)
         * @param decayModule   Tune this one to resist rotation (D controller)
         * @return
         */
        public HeadingAssistModule createHeadingAssistModule(@Assisted("headingModule") HeadingModule headingModule,
                        @Assisted("decayModule") HeadingModule decayModule, @Assisted("prefix") String prefix);

        public HeadingAssistModule createHeadingAssistModule(@Assisted("headingModule") HeadingModule headingModule,
                        @Assisted("prefix") String prefix);

        public HumanVsMachineDecider createHumanVsMachineDecider(@Assisted("prefix") String prefix);

        public CalibrationDecider createCalibrationDecider(@Assisted("name") String name);

        public VelocityThrottleModule createVelocityThrottleModule(@Assisted("name") String name,
                        @Assisted("velocityPid") PIDManager velocityPid);

        public FieldPosePropertyManager createFieldPosePropertyManager(@Assisted("poseName") String poseName,
                        @Assisted("x") double x, @Assisted("y") double y, @Assisted("heading") double heading);

        public FieldPosePropertyManager createFieldPosePropertyManager(@Assisted("poseName") String poseName,
                        @Assisted("fieldPose") FieldPose fieldPose);

        public OffboardCommunicationClient createZeromqListener(@Assisted("connectionString") String connectionString,
                        @Assisted("topic") String topic);

        public XDoubleSolenoid createDoubleSolenoid(@Assisted("forwardSolenoid") XSolenoid forwardSolenoid, 
                        @Assisted("reverseSolenoid") XSolenoid reverseSolenoid);

        public XCANSparkMax createCANSparkMax(
                @Assisted("deviceInfo") DeviceInfo deviceInfo, 
                @Assisted("owningSystemPrefix") String owningSystemPrefix, 
                @Assisted("name") String name); 

        public XCANSparkMax createCANSparkMax(
                @Assisted("deviceInfo") DeviceInfo deviceInfo, 
                @Assisted("owningSystemPrefix") String owningSystemPrefix, 
                @Assisted("name") String name,
                @Assisted("defaultPIDProperties") XCANSparkMaxPIDProperties defaultPIDProperties); 

        public RumbleManager createRumbleManager(@Assisted("gamepad") XJoystick gamepad);

        public XAS5600 createXAS5600(@Assisted("talon") XCANTalon talon);

        public XCANVictorSPX createCANVictorSPX(@Assisted("deviceId") int deviceId);

        public XAbsoluteEncoder createAbsoluteEncoder(@Assisted("deviceInfo") DeviceInfo deviceInfo, @Assisted("owningSystemPrefix") String owningSystemPrefix);

        public StallDetector createStallDetector(@Assisted("owningSystemPrefix") String owningSystemPrefix);
        
        public XCANCoder createCANCoder(@Assisted("deviceInfo") DeviceInfo deviceInfo, @Assisted("owningSystemPrefix") String owningSystemPrefix);
}