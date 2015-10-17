package xbot.common.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.MockPermanentStorage;
import xbot.common.injection.PersonalComputerDatabaseStorage;

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
    	propertyManager.permanentStore.setDouble("speed", 0.5);
    	propertyManager.permanentStore.setBoolean("isTrue", true);
    	propertyManager.permanentStore.setString("string", "teststring");
    	
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
    	   	
    	propertyManager.createProperty("speed", 0.5);
        propertyManager.createProperty("isTrue", true);
        propertyManager.createProperty("string", "teststring");
    	
    	propertyManager.saveOutAllProperties();
    	
        DoubleProperty dbl = propertyManager.createProperty("speed", 1.0);
        BooleanProperty bool = propertyManager.createProperty("isTrue", false);
        StringProperty str = propertyManager.createProperty("string", "blahblah");
    	
    	propertyManager.loadPropertiesFromStorage();

        assertEquals(0.5, dbl.get(), 0.001);
        assertEquals(true,bool.get());
        assertEquals("teststring",str.get());
    }
    
    @After
    public void cleanUp()
    {
    	// We need a way to obliterate the database locally so tests don't leak. Can't delete the files themselves,
    	// because the database process still has a handle on some of them.
    	assertEquals(true, ((PersonalComputerDatabaseStorage)propertyManager.permanentStore).obliterateStorage());
    }
}
