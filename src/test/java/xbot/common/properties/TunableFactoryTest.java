package xbot.common.properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.tunable.MockTunableBackend;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Distance;

import xbot.common.logging.LoudRobotAssertionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Inches;
import static org.wpilib.units.Units.Meters;

public class TunableFactoryTest {
    private TunableFactory tunableFactory;
    private MockTunableBackend tunableBackend;

    @BeforeEach
    public void setUp() {
        TunableRegistry.reset();
        tunableBackend = new MockTunableBackend();
        TunableRegistry.registerBackend("", tunableBackend);
        TunableManager manager = new TunableManager(
                new InMemoryTunablePersistence(),
                new InMemoryTunablePersistence(),
                new LoudRobotAssertionManager(),
                false);
        tunableFactory = new TunableFactory(manager, new LoudRobotAssertionManager());
    }

    @AfterEach
    public void tearDown() {
        TunableRegistry.reset();
    }

    @Test
    public void cleansPrefixes() {
        tunableFactory.setPrefix("my//myPrefixWithDoubleSlashes");
        assertEquals("my/myPrefixWithDoubleSlashes/", tunableFactory.getCleanPrefix());

        tunableFactory.setPrefix("simplePrefixWithNoSlashes");
        assertEquals("simplePrefixWithNoSlashes/", tunableFactory.getCleanPrefix());
    }

    @Test
    public void requiresExplicitPrefix() {
        assertThrows(
                RuntimeException.class,
                () -> tunableFactory.createDouble("value", 1.0));
    }

    @Test
    public void createsDirectPrimitiveTunables() {
        tunableFactory.setPrefix("drive");
        TunableDouble speed = tunableFactory.createDouble("speed", 2.0);

        assertEquals(2.0, speed.get(), 0.001);
        assertEquals(2.0, tunableBackend.getDouble("/drive/speed"), 0.001);
    }

    @Test
    public void createsUnitAwareMeasureTunables() {
        tunableFactory.setPrefix("drive");
        TunableMeasure<Distance, ?> distance =
                tunableFactory.createMeasure("distance", Inches.of(12));
        TunableMeasure<Angle, ?> angle =
                tunableFactory.createMeasure("angle", Degrees.of(90));

        assertEquals(12.0, distance.get().in(Inches), 0.001);
        distance.set(Meters.of(1));
        assertEquals(1.0, distance.get().in(Meters), 0.001);
        assertEquals(
                Meters.of(1).in(Inches),
                tunableBackend.getDouble("/drive/distance-in-Inches"),
                0.001);
        assertEquals(90.0, angle.get().in(Degrees), 0.001);
    }
}
