package xbot.common.injection.wpi_factories;

import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.networking.OffboardCommunicationClient;

public interface CommonLibFactory {
        public OffboardCommunicationClient createZeromqListener(@Assisted("connectionString") String connectionString,
                        @Assisted("topic") String topic);

        public XCANSparkMax createCANSparkMax(
                @Assisted("deviceInfo") DeviceInfo deviceInfo, 
                @Assisted("owningSystemPrefix") String owningSystemPrefix, 
                @Assisted("name") String name); 

        public XCANSparkMax createCANSparkMax(
                @Assisted("deviceInfo") DeviceInfo deviceInfo, 
                @Assisted("owningSystemPrefix") String owningSystemPrefix, 
                @Assisted("name") String name,
                @Assisted("defaultPIDProperties") XCANSparkMaxPIDProperties defaultPIDProperties); 

        public XAbsoluteEncoder createAbsoluteEncoder(@Assisted("deviceInfo") DeviceInfo deviceInfo, @Assisted("owningSystemPrefix") String owningSystemPrefix);

        public XCANCoder createCANCoder(@Assisted("deviceInfo") DeviceInfo deviceInfo, @Assisted("owningSystemPrefix") String owningSystemPrefix);
}