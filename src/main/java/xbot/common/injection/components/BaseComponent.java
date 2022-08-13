package xbot.common.injection.components;

import javax.inject.Named;

import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.command.XScheduler;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.AdvancedJoystickButton.AdvancedJoystickButtonFactory;
import xbot.common.controls.sensors.AdvancedPovButton.AdvancedPovButtonFactory;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDButtonFactory;
import xbot.common.controls.sensors.ChordButton.ChordButtonFactory;
import xbot.common.controls.sensors.VirtualButton.VirtualButtonFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XFTCGamepad.XFTCGamepadFactory;
import xbot.common.controls.sensors.XJoystick.XJoystickFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.XXboxController.XXboxControllerFactory;
import xbot.common.injection.factories.PIDFactory;
import xbot.common.injection.factories.PIDPropertyManagerFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.logging.RobotSession;
import xbot.common.logic.CalibrationDecider.CalibrationDeciderFactory;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.logic.StallDetector.StallDetectorFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;
import xbot.common.simulation.SimulationPayloadDistributor;
import xbot.common.simulation.WebotsClient;
import xbot.common.subsystems.autonomous.AutonomousCommandSelector;

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

    public abstract AutonomousCommandSelector autonomousCommandSelector();

    public abstract RobotSession robotSession();

    public abstract WebotsClient webotsClient();

    public abstract SimulationPayloadDistributor simulationPayloadDistributor();

    public abstract PIDFactory pidFactory();

    public abstract PIDPropertyManagerFactory pidPropertyManagerFactory();

    public abstract XPowerDistributionPanelFactory powerDistributionPanelFactory();

    public abstract XJoystickFactory joystickFactory();

    public abstract AdvancedJoystickButtonFactory joystickButtonFactory();

    public abstract AdvancedPovButtonFactory povButtonFactory();

    public abstract AnalogHIDButtonFactory analogHidButtonFactory();

    public abstract XXboxControllerFactory xboxControllerFactory();

    public abstract XFTCGamepadFactory ftcGamepadFactory();

    public abstract ChordButtonFactory chordButtonFactory();

    public abstract VirtualButtonFactory virtualButtonFactory();

    public abstract HumanVsMachineDeciderFactory humanVsMachineDeciderFactory();

    public abstract CalibrationDeciderFactory calibrationDeciderFactory();

    public abstract StallDetectorFactory stallDetectorFactory();

    public abstract XDigitalInputFactory digitalInputFactory();
}
