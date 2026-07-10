package xbot.common.properties;

import edu.wpi.first.units.measure.AngularVelocity;
import xbot.common.injection.BaseCommonLibTest;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyFactoryTest extends BaseCommonLibTest {

    @Test
    public void testNoDoubleSlashes() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        factory.setPrefix("my//myPrefixWithDoubleSlashes");

        assertEquals(-1, factory.getCleanPrefix().indexOf("//"));

        factory.setPrefix("simplePrefixWithNoSlashes");
        assertEquals(-1, factory.getCleanPrefix().indexOf("//"));
    }

    @Test
    public void testDistanceProperty() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        factory.setPrefix("myPrefix");

        // should get back the same value as we put in with the same unit as the defaultValue
        DistanceProperty propertyMeters = factory.createPersistentProperty("test", Meters.of(1.0));
        assertEquals(1.0, propertyMeters.get().in(Meters), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Meters", propertyMeters.suffix);

        propertyMeters.set(Meters.of(2.0));
        assertEquals(2.0, propertyMeters.get().in(Meters), 0.0001);

        // should get back the same value as we put in with the same unit as the defaultValue
        DistanceProperty propertyInches = factory.createPersistentProperty("test", Inches.of(12.0));
        assertEquals(12.0, propertyInches.get().in(Inches), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Inches", propertyInches.suffix);
    }

    @Test
    public void testAngleProperty() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        factory.setPrefix("myPrefix");

        // should get back the same value as we put in with the same unit as the defaultValue
        AngleProperty propertyDegrees = factory.createPersistentProperty("test", Degrees.of(90.0));
        assertEquals(90.0, propertyDegrees.get().in(Degrees), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Degrees", propertyDegrees.suffix);
        propertyDegrees.set(Degrees.of(180.0));
        assertEquals(180.0, propertyDegrees.get().in(Degrees), 0.0001);

        // should get back the same value as we put in with the same unit as the defaultValue
        AngleProperty propertyRadians = factory.createPersistentProperty("test", Radians.of(Math.PI));
        assertEquals(Math.PI, propertyRadians.get().in(Radians), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Radians", propertyRadians.suffix);
    }

    @Test
    public void testAngularVelocityProperty() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        factory.setPrefix("myPrefix");

        // should get back the same value as we put in with the same unit as the defaultValue
        AngularVelocityProperty propertyDegrees = factory.createPersistentProperty("test", DegreesPerSecond.of(90.0));
        assertEquals(90.0, propertyDegrees.get().in(DegreesPerSecond), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Degree per Seconds", propertyDegrees.suffix);
        propertyDegrees.set(DegreesPerSecond.of(180.0));
        assertEquals(180.0, propertyDegrees.get().in(DegreesPerSecond), 0.0001);

        // should get back the same value as we put in with the same unit as the defaultValue
        AngularVelocityProperty propertyRadians = factory.createPersistentProperty("test", RadiansPerSecond.of(Math.PI));
        assertEquals(Math.PI, propertyRadians.get().in(RadiansPerSecond), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Radian per Seconds", propertyRadians.suffix);
    }

    @Test
    public void testTimeProperty() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        factory.setPrefix("myPrefix");

        // should get back the same value as we put in with the same unit as the defaultValue
        TimeProperty propertySeconds = factory.createPersistentProperty("test", Seconds.of(5));
        assertEquals(5, propertySeconds.get().in(Seconds), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Seconds", propertySeconds.suffix);
        propertySeconds.set(Seconds.of(10));
        assertEquals(10, propertySeconds.get().in(Seconds), 0.0001);

        // should get back the same value as we put in with the same unit as the defaultValue
        TimeProperty propertyMilliseconds = factory.createPersistentProperty("test", Milliseconds.of(Math.PI));
        assertEquals(Math.PI, propertyMilliseconds.get().in(Milliseconds), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Milliseconds", propertyMilliseconds.suffix);
    }
}