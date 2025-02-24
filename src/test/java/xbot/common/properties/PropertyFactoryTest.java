package xbot.common.properties;

import xbot.common.injection.BaseCommonLibTest;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
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
        AngleProperty propertyDegrees = factory.createPersistentProperty("test", edu.wpi.first.units.Units.Degrees.of(90.0));
        assertEquals(90.0, propertyDegrees.get().in(edu.wpi.first.units.Units.Degrees), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Degrees", propertyDegrees.suffix);

        // should get back the same value as we put in with the same unit as the defaultValue
        AngleProperty propertyRadians = factory.createPersistentProperty("test", edu.wpi.first.units.Units.Radians.of(Math.PI));
        assertEquals(Math.PI, propertyRadians.get().in(edu.wpi.first.units.Units.Radians), 0.0001);
        // suffix should be modified to state the units
        assertEquals("test-in-Radians", propertyRadians.suffix);
    }
}