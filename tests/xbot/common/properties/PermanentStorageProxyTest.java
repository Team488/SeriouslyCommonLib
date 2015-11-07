package xbot.common.properties;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.OffRobotDatabaseStorage;

public class PermanentStorageProxyTest extends BaseWPITest {
    
    @Before
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testSaveAndLoad() {
    	
    	DatabaseStorageBase p = propertyManager.permanentStore;
    	
    	p.setDouble("fancyname", 1.23);
    	p.setBoolean("flag", true);
    	p.setString("phrase", "What time is it?");
        
        assertEquals(1.23, p.getDouble("fancyname"), 0.1);
        assertEquals(true, p.getBoolean("flag"));
        assertEquals("What time is it?", p.getString("phrase"));        
    }
    
    public void testClear() {
        DatabaseStorageBase p = propertyManager.permanentStore;
        
        p.setDouble("fancyname", 1.23);
        p.setBoolean("flag", true);
        p.setString("phrase", "What time is it?");
        // need to verify that information was loaded
        assertEquals(p.getDouble("fancyname"), 1.23, .01);
        
        p.clear();
        
        boolean result = p.getDouble("fancyname") == null;
        
        assertEquals("Should get null back when the table is clear!", result, true);
    }
    
    @After
    public void cleanUp()
    {
    	// We need a way to obliterate the database locally so tests don't leak. Can't delete the files themselves,
    	// because the database process still has a handle on some of them.
    	 assertEquals(true, ((OffRobotDatabaseStorage)propertyManager.permanentStore).obliterateStorage());
    }
}
