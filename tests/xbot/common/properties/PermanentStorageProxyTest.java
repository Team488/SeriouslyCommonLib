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

    private String testFolder = ".\\TestProperties";
    
    @Before
    public void setUp() {
        super.setUp();
        File d = new File(testFolder);
        d.mkdirs();
    }
    
    @Test
    public void saveBackups() {
        
        PermanentStorageProxy p = new PermanentStorage(testFolder + "\\Properties.txt");
        p.writeToFile("asdf,1.23");
        p.writeToFile("aaaaabcd, 1.23");
        
        File d = new File(testFolder);
        File[] listOfFiles = d.listFiles();
        
        // Check that the directory has 2 files: the original, and the backup
        assertEquals(2, listOfFiles.length);
    }
    
    @Test
    public void checkEmptyData()
    {
        PermanentStorageProxy p = new PermanentStorage(testFolder + "\\Properties.txt");
        p.writeToFile("asdf,1.23");
        p.writeToFile("");
        
        File d = new File(testFolder);
        File[] listOfFiles = d.listFiles();
        
        // Check that the directory has only 1 file (because no backup created, due to abort)
        assertEquals(1, listOfFiles.length);
    }
    
    @Test
    public void checkBadWrite()
    {
        PermanentStorageProxy p = new PermanentStorage(testFolder + "\\Properties.txt");
        p.writeToFile("asdf,1.23");
        p.writeToFile("a,1");
        
        File d = new File(testFolder);
        File[] listOfFiles = d.listFiles();
        
        // Check that the directory has only 1 file (because no backup created, due to abort)
        assertEquals(1, listOfFiles.length);
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
