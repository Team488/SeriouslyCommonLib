package xbot.common.injection;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.wpilib.hardware.bus.CANPort;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.DevicePolice.RegistrationResult;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.logging.assertion.LoudRobotAssertionHandler;
import xbot.common.logging.assertion.RobotAssertionException;
import xbot.common.logging.assertion.RobotAssertionHandler;
import xbot.common.logging.assertion.RobotAssertionManager;
import xbot.common.logging.assertion.SilentRobotAssertionHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDevicePolice extends BaseCommonLibTest {
    static Stream<RobotAssertionHandler> assertionManagers() {
        return Stream.of(new LoudRobotAssertionHandler(), new RecordingAssertions());
    }

    @ParameterizedTest
    @MethodSource("assertionManagers")
    public void invalidRequestsLeaveRegistryUnchanged(RobotAssertionHandler assertions) {
        RobotAssertionManager.configure(assertions);
        DevicePolice police = new DevicePolice();
        DeviceAddress occupied = DeviceAddress.channel(DeviceType.Solenoid, 0);
        police.registerDevice(this, occupied);

        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(new Object(), occupied));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(this, occupied));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.channel(DeviceType.Solenoid, 9000), 0, 7));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.channel(DeviceType.Solenoid, 0), 3, 7));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.channel(DeviceType.Solenoid, 1), 7, 3));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(null, occupied));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(new Object()));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(new Object(), (DeviceAddress[]) null));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(new Object(), (DeviceAddress) null));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.channel(null, 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.channel(DeviceType.DigitalIO, -1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.can((CANBusId) null, 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.can((CANPort) null, 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.can(new CANBusId(" "), 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.can(new CANBusId(null), 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), DeviceAddress.can(new CANBusId("\uD800"), 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), new DeviceAddress(DeviceType.CAN, null, 1)));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                new Object(), new DeviceAddress(DeviceType.DigitalIO, Optional.of(CANBusId.fromPort(CANPort.CAN_S0)), 1)));
        if (assertions instanceof RecordingAssertions recording) {
            assertEquals(18, recording.failures);
        }
    }

    @ParameterizedTest
    @MethodSource("assertionManagers")
    public void multiChannelReservationsAreAllOrNothing(RobotAssertionHandler assertions) {
        RobotAssertionManager.configure(assertions);
        DevicePolice police = new DevicePolice();
        DeviceAddress channelA = DeviceAddress.channel(DeviceType.DigitalIO, 1);
        DeviceAddress channelB = DeviceAddress.channel(DeviceType.DigitalIO, 2);
        Object existing = new Object();
        Object encoder = new Object();
        police.registerDevice(existing, channelB);

        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(encoder, channelA, channelB));
        assertTrue(police.findDevice(channelA).isEmpty());
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(encoder, channelA, channelA));

        DeviceAddress channelC = DeviceAddress.channel(DeviceType.DigitalIO, 3);
        RegistrationResult.Registered result = assertInstanceOf(
                RegistrationResult.Registered.class, police.registerDevice(encoder, channelA, channelC));
        assertEquals(List.of(channelA, channelC), result.addresses());
        assertEquals(List.of(existing, encoder), police.getRegisteredDevices());
        assertSame(encoder, police.findDevice(channelA).orElseThrow());
        assertSame(encoder, police.findDevice(channelC).orElseThrow());
    }

    @Test
    public void invalidLookupsReportFailuresAndReturnEmptyWithoutChangingState() {
        RecordingAssertions assertions = new RecordingAssertions();
        RobotAssertionManager.configure(assertions);
        DevicePolice police = new DevicePolice();
        DeviceAddress occupied = DeviceAddress.channel(DeviceType.DigitalIO, 0);
        police.registerDevice(this, occupied);
        var before = police.getRegisteredChannels();

        assertTrue(police.findDevice(null).isEmpty());
        assertTrue(police.findDevice(DeviceAddress.channel(DeviceType.DigitalIO, -1)).isEmpty());
        assertTrue(police.findDevice(DeviceAddress.can((CANBusId) null, 1)).isEmpty());
        assertEquals(3, assertions.failures);
        assertTrue(police.findDevice(DeviceAddress.channel(DeviceType.DigitalIO, 1)).isEmpty());
        assertEquals(3, assertions.failures);
        assertSame(this, police.findDevice(occupied).orElseThrow());
        assertEquals(before, police.getRegisteredChannels());
        assertEquals(List.of(this), police.getRegisteredDevices());
    }

    @Test
    public void invalidLookupsHonorControlledTestAssertionPolicy() {
        RobotAssertionManager.configure(new LoudRobotAssertionHandler());
        DevicePolice police = new DevicePolice();
        assertThrows(RobotAssertionException.class, () -> police.findDevice(null));
        assertThrows(RobotAssertionException.class,
                () -> police.findDevice(DeviceAddress.channel(DeviceType.DigitalIO, -1)));
        assertTrue(police.getRegisteredChannels().isEmpty());
    }

    @Test
    public void ownersUseReferenceIdentityAndSnapshotsDoNotChange() {
        RobotAssertionManager.configure(new LoudRobotAssertionHandler());
        DevicePolice police = new DevicePolice();
        Object first = new String("equal owners");
        Object second = new String("equal owners");
        police.registerDevice(first, DeviceAddress.channel(DeviceType.Analog, 0));
        var addressesBefore = police.getRegisteredChannels();
        var devicesBefore = police.getRegisteredDevices();
        police.registerDevice(first, DeviceAddress.channel(DeviceType.Analog, 1));
        police.registerDevice(second, DeviceAddress.channel(DeviceType.Analog, 2));

        assertEquals(3, police.getRegisteredChannels().size());
        assertEquals(2, police.getRegisteredDevices().size());
        assertSame(first, police.getRegisteredDevices().get(0));
        assertSame(second, police.getRegisteredDevices().get(1));
        assertEquals(1, addressesBefore.size());
        assertEquals(1, devicesBefore.size());
        assertThrows(UnsupportedOperationException.class, addressesBefore::clear);
        assertThrows(UnsupportedOperationException.class, devicesBefore::clear);
    }

    @Test
    public void channelsAreSeparatedByTypeAndCanBusAndRangesAreInclusive() {
        RobotAssertionManager.configure(new LoudRobotAssertionHandler());
        DevicePolice police = new DevicePolice();
        Object systemCoreDevice = new Object();
        Object otherBusDevice = new Object();
        Object pwmDevice = new Object();
        DeviceAddress systemCore = DeviceAddress.can(CANPort.CAN_S0, 0);
        DeviceAddress otherBus = DeviceAddress.can(CANBusId.Canivore, 0);
        police.registerDevice(systemCoreDevice, systemCore);
        police.registerDevice(otherBusDevice, otherBus);
        police.registerDevice(pwmDevice, DeviceAddress.channel(DeviceType.PWM, 0), 0, 7);
        police.registerDevice(pwmDevice, DeviceAddress.channel(DeviceType.PWM, 7), 0, 7);

        assertSame(systemCoreDevice, police.findDevice(systemCore).orElseThrow());
        assertSame(otherBusDevice, police.findDevice(otherBus).orElseThrow());
        assertEquals(4, police.getRegisteredChannels().size());
        assertEquals(3, police.getRegisteredDevices().size());
    }

    @Test
    public void systemCoreAndMotionCorePortsReserveSeparateAddresses() {
        RobotAssertionManager.configure(new LoudRobotAssertionHandler());
        DevicePolice police = new DevicePolice();
        for (CANPort port : List.of(CANPort.CAN_S0, CANPort.CAN_S1, CANPort.CAN_D0)) {
            Object owner = new Object();
            DeviceAddress address = DeviceAddress.can(port, 5);
            police.registerDevice(owner, address);
            assertSame(owner, police.findDevice(address).orElseThrow());
        }
        assertEquals(3, police.getRegisteredChannels().size());
        assertEquals(3, police.getRegisteredDevices().size());
        DeviceAddress equivalentS1 = DeviceAddress.can(new CANBusId("can_s1"), 5);
        assertThrows(RobotAssertionException.class, () -> police.registerDevice(new Object(), equivalentS1));
        assertEquals(3, police.getRegisteredChannels().size());
    }

    @ParameterizedTest
    @MethodSource("assertionManagers")
    public void unsupportedCanBusesDoNotReserveAddresses(RobotAssertionHandler assertions) {
        RobotAssertionManager.configure(assertions);
        DevicePolice police = new DevicePolice();
        DeviceAddress unknown = DeviceAddress.can(new CANBusId("typo"), 5);
        DeviceAddress nativeAddress = DeviceAddress.can(CANPort.CAN_S0, 5);
        DeviceAddress canivoreAddress = DeviceAddress.can(CANBusId.Canivore, 5);

        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(this, unknown));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(this, nativeAddress, unknown));
        assertRejectedUnchanged(police, assertions, () -> police.registerDevice(
                this, DeviceAddress.can(new CANBusId("can_s999"), 5)));

        assertInstanceOf(RegistrationResult.Registered.class,
                police.registerDevice(this, nativeAddress));
        assertInstanceOf(RegistrationResult.Registered.class,
                police.registerDevice(this, canivoreAddress));
        assertEquals(2, police.getRegisteredChannels().size());
        assertSame(this, police.findDevice(nativeAddress).orElseThrow());
        assertSame(this, police.findDevice(canivoreAddress).orElseThrow());
        if (assertions instanceof RecordingAssertions recording) {
            assertEquals(3, recording.failures);
        }
    }

    @Test
    public void concurrentConflictingBatchesHaveOnlyOneWinner() throws Exception {
        RobotAssertionManager.configure(new RecordingAssertions());
        DevicePolice police = new DevicePolice();
        DeviceAddress first = DeviceAddress.channel(DeviceType.DigitalIO, 0);
        DeviceAddress shared = DeviceAddress.channel(DeviceType.DigitalIO, 1);
        DeviceAddress last = DeviceAddress.channel(DeviceType.DigitalIO, 2);
        Object firstOwner = new Object();
        Object secondOwner = new Object();
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstResult = executor.submit(() -> {
                start.await();
                return police.registerDevice(firstOwner, first, shared);
            });
            var secondResult = executor.submit(() -> {
                start.await();
                return police.registerDevice(secondOwner, shared, last);
            });
            start.countDown();
            boolean firstWon = firstResult.get(5, TimeUnit.SECONDS) instanceof RegistrationResult.Registered;
            boolean secondWon = secondResult.get(5, TimeUnit.SECONDS) instanceof RegistrationResult.Registered;
            assertNotEquals(firstWon, secondWon);
            assertEquals(2, police.getRegisteredChannels().size());
            assertEquals(1, police.getRegisteredDevices().size());
            assertSame(firstWon ? firstOwner : secondOwner, police.findDevice(shared).orElseThrow());
            assertEquals(firstWon, police.findDevice(first).isPresent());
            assertEquals(secondWon, police.findDevice(last).isPresent());
        }
    }

    @Test
    public void hardwareFactoriesRegisterActualOwnersAndConfiguredCanBuses() {
        DevicePolice police = getInjectorComponent().devicePolice();
        var motor = getInjectorComponent().motorControllerFactory().create(
                new CANMotorControllerInfo("RegisteredMotor", MotorControllerType.TalonFx, CANBusId.Canivore, 34),
                "RegisteredMotor", "Pid");
        var solenoid = getInjectorComponent().solenoidFactory().create(0);
        var canCoder = getInjectorComponent().canCoderFactory().create(
                new DeviceInfo("RegisteredCanCoder", CANBusId.Canivore, 35), "RegisteredCanCoder");
        var absoluteEncoder = getInjectorComponent().absoluteEncoderFactory().create(
                new DeviceInfo("RegisteredAbsoluteEncoder", CANBusId.Canivore, 36), "RegisteredAbsoluteEncoder");

        assertSame(motor, police.findDevice(DeviceAddress.can(CANBusId.Canivore, 34)).orElseThrow());
        assertSame(solenoid, police.findDevice(DeviceAddress.channel(DeviceType.Solenoid, 0)).orElseThrow());
        assertSame(canCoder, police.findDevice(DeviceAddress.can(CANBusId.Canivore, 35)).orElseThrow());
        assertSame(absoluteEncoder, police.findDevice(DeviceAddress.can(CANBusId.Canivore, 36)).orElseThrow());
        assertTrue(police.findDevice(DeviceAddress.can(CANPort.CAN_S0, 35)).isEmpty());
        assertTrue(police.findDevice(DeviceAddress.can(CANPort.CAN_S0, 36)).isEmpty());
    }

    @Test
    public void encoderConflictReturnsNormallyUnderCompetitionSafePolicy() {
        RecordingAssertions assertions = new RecordingAssertions();
        DevicePolice police = new DevicePolice();
        DeviceAddress occupied = DeviceAddress.channel(DeviceType.DigitalIO, 4);
        police.registerDevice(this, occupied);
        var before = police.getRegisteredChannels();

        MockEncoder encoder = new MockEncoder("ConflictingEncoder", 3, 4, 1, "ConflictingEncoder",
                getInjectorComponent().tunableFactory(), police, new DataFrameRegistry());

        assertEquals(1, assertions.failures);
        assertEquals(before, police.getRegisteredChannels());
        assertEquals(List.of(this), police.getRegisteredDevices());
        assertTrue(police.findDevice(DeviceAddress.channel(DeviceType.DigitalIO, 3)).isEmpty());
        encoder.setDistance(2);
        assertEquals(2, encoder.getAdjustedDistance(), 0.001);
    }

    @Test
    public void encoderFactoryDoesNotLeaveItsFirstChannelReservedAfterSecondChannelConflict() {
        DevicePolice police = getInjectorComponent().devicePolice();
        police.registerDevice(this, DeviceAddress.channel(DeviceType.DigitalIO, 4));
        var before = police.getRegisteredChannels();

        assertThrows(RobotAssertionException.class, () -> getInjectorComponent().encoderFactory()
                .create("ConflictingEncoder", 3, 4, 1, "ConflictingEncoder"));

        assertEquals(before, police.getRegisteredChannels());
        assertEquals(List.of(this), police.getRegisteredDevices());
    }

    private void assertRejectedUnchanged(
            DevicePolice police, RobotAssertionHandler assertions, Supplier<RegistrationResult> registration) {
        var addressesBefore = police.getRegisteredChannels();
        var devicesBefore = police.getRegisteredDevices();
        if (assertions.isExceptionsEnabled()) {
            assertThrows(RobotAssertionException.class, registration::get);
        } else {
            var rejection = assertInstanceOf(RegistrationResult.Rejected.class, registration.get());
            assertFalse(rejection.reason().isBlank());
        }
        assertEquals(addressesBefore, police.getRegisteredChannels());
        assertEquals(devicesBefore, police.getRegisteredDevices());
    }

    private static class RecordingAssertions extends RobotAssertionHandler {
        private int failures;

        @Override
        public boolean isExceptionsEnabled() {
            return false;
        }

        @Override
        protected void handlePlatformException(RuntimeException exception) {
            failures++;
        }
    }
}
