package xbot.common.injection.wpi_factories;

import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANVictorSPX;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XDoubleSolenoid;
import xbot.common.controls.actuators.XPWM;
import xbot.common.controls.actuators.XRelay;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.sensors.XAS5600;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.FieldPose;
import xbot.common.math.FieldPosePropertyManager;
import xbot.common.networking.OffboardCommunicationClient;

public interface CommonLibFactory {
        public XEncoder createEncoder(@Assisted("name") String name, @Assisted("aChannel") int aChannel,
                        @Assisted("bChannel") int bChannel,
                        @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse);

        public XAnalogInput createAnalogInput(@Assisted("channel") int channel);

        public XSolenoid createSolenoid(@Assisted("channel") int channel);

        public XDigitalOutput createDigitalOutput(@Assisted("channel") int channel);

        public XServo createServo(@Assisted("channel") int channel);

        public XPWM createPWM(@Assisted("channel") int channel);

        public XRelay createRelay(@Assisted("channel") int channel);

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

        public XAS5600 createXAS5600(@Assisted("talon") XCANTalon talon);

        public XCANVictorSPX createCANVictorSPX(@Assisted("deviceId") int deviceId);

        public XAbsoluteEncoder createAbsoluteEncoder(@Assisted("deviceInfo") DeviceInfo deviceInfo, @Assisted("owningSystemPrefix") String owningSystemPrefix);

        public XCANCoder createCANCoder(@Assisted("deviceInfo") DeviceInfo deviceInfo, @Assisted("owningSystemPrefix") String owningSystemPrefix);
}