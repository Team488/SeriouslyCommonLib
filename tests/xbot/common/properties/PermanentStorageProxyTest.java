package xbot.common.properties;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.MockPermanentStorageWithDatabase;

public class PermanentStorageProxyTest extends BaseWPITest {

    private String testFolder = "./488Database";
    
    @Before
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testSaveAndLoad() {
    	
    	PermanentStorageProxy p = propertyManager.permanentStore;
        
        p.writeToFile("double,fancyname,1.23\nboolean,flag,true\nstring,phrase,What time is it?");
        
        p.loadFromDisk();
        
        assertEquals(1.23, p.getDouble("fancyname"), 0.1);
        assertEquals(true, p.getBoolean("flag"));
        assertEquals("What time is it?", p.getString("phrase"));        
    }
    
    @Test
    public void loadNothing()
    {
    	// No exceptions should be thrown.
    	propertyManager.permanentStore.loadFromDisk();
    }
    
    @After
    public void cleanUp()
    {
    	// We need a way to obliterate the database locally so tests don't leak. Can't delete the files themselves,
    	// because the database process still has a handle on some of them.
    	 assertEquals(true, ((MockPermanentStorageWithDatabase)propertyManager.permanentStore).obliterateStorage());
    }
}
