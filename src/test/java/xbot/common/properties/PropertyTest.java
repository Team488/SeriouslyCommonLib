package xbot.common.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class PropertyTest extends BaseCommonLibTest {

    XPropertyManager propertyManager;

    @Before
    public void setUp() {
        super.setUp();
        this.propertyManager = getInjectorComponent().propertyManager();
        propertyFactory.setTopLevelPrefix();
    }

    @Test
    public void testDefaultValue() {
        DoubleProperty dbl = propertyFactory.createEphemeralProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createEphemeralProperty("isTrue", true);
        StringProperty str = propertyFactory.createEphemeralProperty("string", "teststring");

        assertEquals(1.0, dbl.get(), 0.001);
        assertTrue(bool.get());
        assertEquals("teststring", str.get());
    }

    @Test
    public void testChangingValue() {
        DoubleProperty dbl = propertyFactory.createEphemeralProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createEphemeralProperty("isTrue", true);
        StringProperty str = propertyFactory.createEphemeralProperty("string", "teststring");

        dbl.set(0.5);
        bool.set(false);
        str.set("test2");

        assertEquals(0.5, dbl.get(), 0.001);
        assertFalse(bool.get());
        assertEquals("test2", str.get());
    }

    @Test
    public void testSavingValue() {
        DoubleProperty dbl = propertyFactory.createPersistentProperty("speed", 0.5);
        BooleanProperty bool = propertyFactory.createPersistentProperty("isTrue", false);
        StringProperty str = propertyFactory.createPersistentProperty("string", "test2");

        assertNull(propertyManager.permanentStore.getDouble("speed"));
        assertNull(propertyManager.permanentStore.getBoolean("isTrue"));
        assertNull(propertyManager.permanentStore.getString("string"));

        assertEquals(0.5, dbl.get(), 0.001);
        assertFalse(bool.get());
        assertEquals("test2", str.get());

        propertyManager.saveOutAllProperties();

        // default values should exist in permanent store
        assertEquals(0.5, propertyManager.permanentStore.getDouble("speed"), 0.001);
        assertFalse(propertyManager.permanentStore.getBoolean("isTrue"));
        assertEquals("test2", propertyManager.permanentStore.getString("string"));

        dbl.set(1.0);
        bool.set(true);
        str.set("new");
        
        propertyManager.saveOutAllProperties();

        // non-defaults should save
        assertEquals(1.0, propertyManager.permanentStore.getDouble("speed"), 0.001);
        assertTrue(propertyManager.permanentStore.getBoolean("isTrue"));
        assertEquals("new", propertyManager.permanentStore.getString("string"));

        dbl.set(0.5);
        bool.set(false);
        str.set("test2");

        propertyManager.saveOutAllProperties();

        // default values should be still be in permanent store
        assertEquals(0.5, propertyManager.permanentStore.getDouble("speed"), 0.001);
        assertFalse(propertyManager.permanentStore.getBoolean("isTrue"));
        assertEquals("test2", propertyManager.permanentStore.getString("string"));

        // note that we still need to save default values, because we don't want
        // to lose them if a different robot program is loaded that doesn't have
        // the same set of properties
    }

    @Test
    public void testBadPropertyName() {
        DoubleProperty dbl = propertyFactory.createEphemeralProperty("commas are bad ,", 0.5);
        assertEquals("commas are bad ", dbl.key);

        DoubleProperty dbl2 = propertyFactory.createEphemeralProperty("new lines are bad too\n", 0.5);
        assertEquals("new lines are bad too", dbl2.key);

    }

    @Test
    public void testLoadingValue() {
        propertyManager.permanentStore.setDouble("speed", 0.5);
        propertyManager.permanentStore.setBoolean("isTrue", true);
        propertyManager.permanentStore.setString("string", "teststring");

        propertyManager.loadPropertiesFromStorage();

        DoubleProperty dbl = propertyFactory.createPersistentProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createPersistentProperty("isTrue", false);
        StringProperty str = propertyFactory.createPersistentProperty("string", "blahblah");

        assertEquals(0.5, dbl.get(), 0.001);
        assertTrue(bool.get());
        assertEquals("teststring", str.get());
    }

    @Test
    public void testLoadingValueAfterCreation() {

        propertyFactory.createPersistentProperty("speed", 0.5);
        propertyFactory.createPersistentProperty("isTrue", true);
        propertyFactory.createPersistentProperty("string", "teststring");

        propertyManager.saveOutAllProperties();

        DoubleProperty dbl = propertyFactory.createPersistentProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createPersistentProperty("isTrue", false);
        StringProperty str = propertyFactory.createPersistentProperty("string", "blahblah");

        propertyManager.loadPropertiesFromStorage();

        // Values should not change because the old properties should be loaded
        // rather than the new defaults.
        assertEquals(0.5, dbl.get(), 0.001);
        assertTrue(bool.get());
        assertEquals("teststring", str.get());
    }
}
