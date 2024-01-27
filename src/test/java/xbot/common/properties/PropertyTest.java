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
        DoubleProperty dbl = propertyFactory.createPersistentProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createPersistentProperty("isTrue", true);
        StringProperty str = propertyFactory.createPersistentProperty("string", "teststring");

        assertEquals(1.0, dbl.get(), 0.001);
        assertTrue(bool.get());
        assertEquals("teststring", str.get());
    }

    @Test
    public void testChangingValue() {
        DoubleProperty dbl = propertyFactory.createPersistentProperty("speed", 1.0);
        BooleanProperty bool = propertyFactory.createPersistentProperty("isTrue", true);
        StringProperty str = propertyFactory.createPersistentProperty("string", "teststring");

        dbl.set(0.5);
        bool.set(false);
        str.set("test2");

        assertEquals(0.5, dbl.get(), 0.001);
        assertFalse(bool.get());
        assertEquals("test2", str.get());
    }

    @Test
    public void testBadPropertyName() {
        DoubleProperty dbl = propertyFactory.createPersistentProperty("commas are bad ,", 0.5);
        assertEquals("/commas are bad ", dbl.key);

        DoubleProperty dbl2 = propertyFactory.createPersistentProperty("new lines are bad too\n", 0.5);
        assertEquals("/new lines are bad too", dbl2.key);

    }
}
