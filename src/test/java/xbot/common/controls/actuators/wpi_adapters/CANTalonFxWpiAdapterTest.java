package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.sim.TalonFXSimState;
import com.ctre.phoenix6.unmanaged.Unmanaged;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import xbot.common.command.BaseRobot;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.resiliency.DeviceHealth;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Rotation;
import static edu.wpi.first.units.Units.Rotations;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
public class CANTalonFxWpiAdapterTest extends BaseCommonLibTest implements AutoCloseable {
    CANTalonFxWpiAdapter talonFx;
    DCMotorSim motorSim;
    TalonFXSimState simState;

    @Before
    public void setup() throws InterruptedException {
        Unmanaged.loadPhoenix();

        RobotController.setTimeSource(() -> (long) (timer.getFPGATimestamp() * 1000000));
        assert HAL.initialize(1000, 0);

        CANBus canBus = new CANBus("sim");

        /* delay ~100ms so the devices can start up */
        timer.advanceTimeInSecondsBy(0.100);

        /* create the TalonFX */
        talonFx = new CANTalonFxWpiAdapter(new CANMotorControllerInfo("test", 1), "TestMotorController", getInjectorComponent().propertyFactory(),
                getInjectorComponent().devicePolice(), "TestMotorController", null);

        var gearbox = DCMotor.getKrakenX60(1);
        motorSim = new DCMotorSim(
                LinearSystemId.createDCMotorSystem(gearbox, 0.001, 1),
                gearbox
        );

        simState = talonFx.getSimState();
        simState.setSupplyVoltage(RobotController.getMeasureBatteryVoltage());

        /* enable the robot */
        DriverStationSim.setEnabled(true);
        DriverStationSim.notifyNewData();

        HAL.simPeriodicBefore();
        HAL.simPeriodicAfter();

        /* wait for the TalonFX to be healthy */
        for (int i = 0; i < 50; i++) {
            if (talonFx.getHealth() == DeviceHealth.Healthy) {
                break;
            }
            System.out.println("Waiting for TalonFX to be healthy");
            Thread.sleep(200);
        }
    }

    @After
    public void tearDown() {
        close();
        DriverStationSim.resetData();
    }

    @Test
    public void testLifecycle() {
        talonFx.setVelocityTarget(RPM.of(100), XCANMotorController.MotorPidMode.Voltage);
        motorSim.setAngularVelocity(RPM.of(100).in(RadiansPerSecond));
        assertEquals(0, talonFx.getPosition().in(Rotations), 0.001);

        motorSim.update(0.2);
        simState.setRawRotorPosition(motorSim.getAngularPosition());
        simState.setRotorVelocity(motorSim.getAngularVelocity());
        timer.advanceTimeInSecondsBy(0.2);

        talonFx.refreshDataFrame();
        talonFx.periodic();

        assertEquals(47.8125, talonFx.getPosition().in(Degrees), 0.001);
        assertEquals(10.5468, talonFx.getVelocity().in(RPM), 0.001);
    }

    @Test
    public void testPosition() {
        assertEquals(0, talonFx.getPosition().in(Rotations), 0.001);
        assertEquals(0, talonFx.getRawPosition().in(Rotations), 0.001);
        talonFx.setPosition(Degrees.of(180));

        timer.advanceTimeInSecondsBy(0.2);
        talonFx.refreshDataFrame();
        talonFx.periodic();

        assertEquals(0.5, talonFx.getPosition().in(Rotations), 0.001);
        assertEquals(0.5, talonFx.getRawPosition().in(Rotations), 0.001);

        talonFx.setAngleScaleFactor(Rotations.per(Rotation).of(2));
        assertEquals(1.0, talonFx.getPosition().in(Rotations), 0.001);
        assertEquals(0.5, talonFx.getRawPosition().in(Rotations), 0.001);
    }

    public void close() {
        talonFx.close();
    }
}