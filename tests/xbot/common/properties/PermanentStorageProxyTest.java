package xbot.common.properties;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class PermanentStorageProxyTest extends BaseWPITest {

    private String testFolder = "testo";
    
    @Before
    public void setUp() {
        super.setUp();
        
        // clean out database
        File d = new File(testFolder);
        try {
            FileUtils.deleteDirectory(d);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSaveAndLoad() {
        
        PermanentStorageProxy p = new PermanentStorage();
        p.writeToFile("double,fancyname,1.23\nboolean,flag,true\nstring,phrase,What time is it?");
        
        p.loadFromDisk();
        
        assertEquals(1.23, p.getDouble("fancyname"), 0.1);
        assertEquals(true, p.getBoolean("flag"));
        assertEquals("What time is it?", p.getString("phrase"));        
    }
    
    @Test
    public void testClear() {
    	PermanentStorageProxy p = new PermanentStorage();
        p.writeToFile("double,fancyname,1.23\nboolean,flag,true\nstring,phrase,What time is it?");
        
        p.loadFromDisk();
        // need to verify that information was loaded
        assertEquals(p.getDouble("fancyname"), 1.23, .01);
        
        p.clear();
        
        boolean result = p.getDouble("fancyname") == null;
        
        assertEquals("Should get null back when the table is clear!", result, true);
    }
    
    @After
    public void cleanUp()
    {
        File d = new File(testFolder);
        try {
            FileUtils.deleteDirectory(d);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
