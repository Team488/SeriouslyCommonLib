package xbot.common.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

@SuppressWarnings("deprecation")
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
        DoubleProperty dbl = propertyFactory.createProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createProperty("isTrue", true);
        StringProperty str = propertyFactory.createProperty("string", "teststring");

        assertEquals(1.0, dbl.get(), 0.001);
        assertEquals(true, bool.get());
        assertEquals("teststring", str.get());
    }

    @Test
    public void testChangingValue() {
        DoubleProperty dbl = propertyFactory.createProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createProperty("isTrue", true);
        StringProperty str = propertyFactory.createProperty("string", "teststring");

        dbl.set(0.5);
        bool.set(false);
        str.set("test2");

        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(false, bool.get());
        assertEquals("test2", str.get());
    }

    @Test
    public void testSavingValue() {
        DoubleProperty dbl = propertyFactory.createProperty("speed", 0.5);
        BooleanProperty bool = propertyFactory.createProperty("isTrue", false);
        StringProperty str = propertyFactory.createProperty("string", "test2");

        assertSame(null, propertyManager.permanentStore.getDouble("speed"));
        assertSame(null, propertyManager.permanentStore.getBoolean("isTrue"));
        assertSame(null, propertyManager.permanentStore.getString("string"));

        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(false, bool.get());
        assertEquals("test2", str.get());

        propertyManager.saveOutAllProperties();

        // default values shouldn't exist in permanent store
        assertNull(propertyManager.permanentStore.getDouble("speed"));
        assertNull(propertyManager.permanentStore.getBoolean("isTrue"));
        assertNull(propertyManager.permanentStore.getString("string"));

        dbl.set(1.0);
        bool.set(true);
        str.set("new");
        
        propertyManager.saveOutAllProperties();

        // non-defaults should save
        assertEquals(1.0, propertyManager.permanentStore.getDouble("speed").doubleValue(), 0.001);
        assertEquals(true, propertyManager.permanentStore.getBoolean("isTrue").booleanValue());
        assertEquals("new", propertyManager.permanentStore.getString("string"));

        dbl.set(0.5);
        bool.set(false);
        str.set("test2");

        propertyManager.saveOutAllProperties();

        // default values should be removed from permanent store
        assertNull(propertyManager.permanentStore.getDouble("speed"));
        assertNull(propertyManager.permanentStore.getBoolean("isTrue"));
        assertNull(propertyManager.permanentStore.getString("string"));
    }

    @Test
    public void testBadPropertyName() {
        DoubleProperty dbl = propertyFactory.createProperty("commas are bad ,", 0.5);
        assertEquals("commas are bad ", dbl.key);

        DoubleProperty dbl2 = propertyFactory.createProperty("new lines are bad too\n", 0.5);
        assertEquals("new lines are bad too", dbl2.key);

    }

    @Test
    public void testLoadingValue() {
        propertyManager.permanentStore.setDouble("speed", 0.5);
        propertyManager.permanentStore.setBoolean("isTrue", true);
        propertyManager.permanentStore.setString("string", "teststring");

        propertyManager.loadPropertiesFromStorage();

        DoubleProperty dbl = propertyFactory.createProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createProperty("isTrue", false);
        StringProperty str = propertyFactory.createProperty("string", "blahblah");

        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(true, bool.get());
        assertEquals("teststring", str.get());
    }

    @Test
    public void testLoadingValueAfterCreation() {

        propertyFactory.createProperty("speed", 0.5);
        propertyFactory.createProperty("isTrue", true);
        propertyFactory.createProperty("string", "teststring");

        propertyManager.saveOutAllProperties();

        DoubleProperty dbl = propertyFactory.createProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createProperty("isTrue", false);
        StringProperty str = propertyFactory.createProperty("string", "blahblah");

        propertyManager.loadPropertiesFromStorage();

        assertEquals(1.0, dbl.get(), 0.001);
        assertEquals(false, bool.get());
        assertEquals("blahblah", str.get());
    }
}
