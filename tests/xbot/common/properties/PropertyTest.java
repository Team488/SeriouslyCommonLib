package xbot.common.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.MockPermanentStorage;
import xbot.common.properties.Property.PropertyPersistenceType;

public class PropertyTest extends BaseWPITest {
	
	@Before
	public void setUp() {
		super.setUp();
		
	}
	
    @Test
    public void testDefaultValue() {
        DoubleProperty dbl = propertyManager.createProperty("speed", 1.0);
        BooleanProperty bool = propertyManager.createProperty("isTrue", true);
        StringProperty str = propertyManager.createProperty("string", "teststring");
        
        assertEquals(1.0, dbl.get(), 0.001);
        assertEquals(true,bool.get());
        assertEquals("teststring",str.get());
    }
    
    @Test
    public void testChangingValue() {
        DoubleProperty dbl = propertyManager.createProperty("speed", 1.0);
        BooleanProperty bool = propertyManager.createProperty("isTrue", true);
        StringProperty str = propertyManager.createProperty("string", "teststring");
        
        dbl.set(0.5);
        bool.set(false);
        str.set("test2");
        
        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(false,bool.get());
        assertEquals("test2",str.get());
    }
    
    @Test
    public void testSavingValue() {
        DoubleProperty dbl = propertyManager.createProperty("speed", 0.5);
        BooleanProperty bool = propertyManager.createProperty("isTrue", false);
        StringProperty str = propertyManager.createProperty("string", "test2");
        
        assertSame(null, propertyManager.permanentStore.getDouble("speed"));
        assertSame(null, propertyManager.permanentStore.getBoolean("isTrue"));
        assertSame(null, propertyManager.permanentStore.getString("string"));
        
        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(false,bool.get());
        assertEquals("test2",str.get());
        
        propertyManager.saveOutAllProperties();
        
        assertEquals(0.5, propertyManager.permanentStore.getDouble("speed").doubleValue(), 0.001);
        assertEquals(false,propertyManager.permanentStore.getBoolean("isTrue").booleanValue());
        assertEquals("test2",propertyManager.permanentStore.getString("string"));
    }
    
    @Test
    public void testSavingValuePersistent() {
        DoubleProperty dbl1 = propertyManager.createProperty("speed", 0.5);
        BooleanProperty bool1 = propertyManager.createProperty("isTrue", false);
        StringProperty str1 = propertyManager.createProperty("string", "test2");
        
        DoubleProperty dbl2 = propertyManager.createProperty("weight", 2.3, PropertyPersistenceType.Ephemeral);
        BooleanProperty bool2 = propertyManager.createProperty("isFalse", true, PropertyPersistenceType.Ephemeral);
        StringProperty str2 = propertyManager.createProperty("robotname", "xbot", PropertyPersistenceType.Ephemeral);
        
        DoubleProperty dbl3 = propertyManager.createProperty("height", 4.8, PropertyPersistenceType.Persistent);
        BooleanProperty bool3 = propertyManager.createProperty("isAwesome", true, PropertyPersistenceType.Persistent);
        StringProperty str3 = propertyManager.createProperty("team", "488", PropertyPersistenceType.Persistent);
        
        assertSame(null, propertyManager.permanentStore.getDouble("speed"));
        assertSame(null, propertyManager.permanentStore.getBoolean("isTrue"));
        assertSame(null, propertyManager.permanentStore.getString("string"));
        
        assertSame(null, propertyManager.permanentStore.getDouble("weight"));
        assertSame(null, propertyManager.permanentStore.getBoolean("isFalse"));
        assertSame(null, propertyManager.permanentStore.getString("robotname"));
        
        assertSame(null, propertyManager.permanentStore.getDouble("height"));
        assertSame(null, propertyManager.permanentStore.getBoolean("isAwesome"));
        assertSame(null, propertyManager.permanentStore.getString("team"));
        
        assertEquals(0.5, dbl1.get(), 0.001);
        assertEquals(false,bool1.get());
        assertEquals("test2",str1.get());
        assertEquals(2.3,dbl2.get(), 0.001);
        assertEquals(true,bool2.get());
        assertEquals("xbot",str2.get());
        assertEquals(4.8,dbl3.get(), 0.001);
        assertEquals(true,bool3.get());
        assertEquals("488",str3.get());
        
        propertyManager.saveOutAllProperties();
        
        assertEquals(0.5, propertyManager.permanentStore.getDouble("speed").doubleValue(), 0.001);
        assertEquals(false,propertyManager.permanentStore.getBoolean("isTrue").booleanValue());
        assertEquals("test2",propertyManager.permanentStore.getString("string"));
        
        assertSame(null, propertyManager.permanentStore.getDouble("weight"));
        assertSame(null, propertyManager.permanentStore.getBoolean("isFalse"));
        assertSame(null, propertyManager.permanentStore.getString("robotname"));
        
        assertEquals(4.8, propertyManager.permanentStore.getDouble("height").doubleValue(), 0.001);
        assertEquals(true,propertyManager.permanentStore.getBoolean("isAwesome").booleanValue());
        assertEquals("488",propertyManager.permanentStore.getString("team"));
    }
    
    @Test
    public void testSortingValues() {
        PermanentStorageProxy permanentStore = propertyManager.permanentStore;
        permanentStore.setDouble("A", 0);
        permanentStore.setString("A1", "");
        permanentStore.setDouble("C", 0);
        permanentStore.setDouble("B", 0);
        
        assertEquals("double,A,0.0\nstring,A1,\ndouble,B,0.0\ndouble,C,0.0\n",
                permanentStore.serializePropertiesToString());
    }
    
    @Test
    public void testBadPropertyName() {
        DoubleProperty dbl = propertyManager.createProperty("commas are bad ,", 0.5);
        assertEquals("commas are bad ", dbl.key);


        DoubleProperty dbl2 = propertyManager.createProperty("new lines are bad too\n", 0.5);
        assertEquals("new lines are bad too", dbl2.key);
        
    }
	
    @Test
    public void testLoadingValue() {
    	((MockPermanentStorage)propertyManager.permanentStore).addTestDouble("speed", 0.5);
    	((MockPermanentStorage)propertyManager.permanentStore).addTestBoolean("isTrue", true);
    	((MockPermanentStorage)propertyManager.permanentStore).addTestString("string", "teststring");
    	
    	propertyManager.loadPropertiesFromStorage();
        
        DoubleProperty dbl = propertyManager.createProperty("speed", 1.0);
        BooleanProperty bool = propertyManager.createProperty("isTrue", false);
        StringProperty str = propertyManager.createProperty("string", "blahblah");
        
        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(true,bool.get());
        assertEquals("teststring",str.get());
    }
    
    @Test
    public void testLoadingValueAfterCreation() {
    	((MockPermanentStorage)propertyManager.permanentStore).addTestDouble("speed", 0.5);
    	((MockPermanentStorage)propertyManager.permanentStore).addTestBoolean("isTrue", true);
    	((MockPermanentStorage)propertyManager.permanentStore).addTestString("string", "teststring");
    	
        DoubleProperty dbl = propertyManager.createProperty("speed", 1.0);
        BooleanProperty bool = propertyManager.createProperty("isTrue", false);
        StringProperty str = propertyManager.createProperty("string", "blahblah");
    	
    	propertyManager.loadPropertiesFromStorage();

        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(true,bool.get());
        assertEquals("teststring",str.get());
    }
}
