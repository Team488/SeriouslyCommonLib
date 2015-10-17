package xbot.common.injection;

import xbot.common.properties.DatabaseStorageBase;
import xbot.common.properties.PermanentStorageProxy;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MockPermanentStorage extends DatabaseStorageBase {
	
    private StringBuffer buf;
	
    /**
     * Permanent storage property storage delimiter
     */
    protected String propertyDelimiter = ",";
    /**
     * Permanent storage line seperator
     */
    protected String lineSeperator = "\n";
	
	@Inject
	public MockPermanentStorage() {
		super ();
        buf = new StringBuffer();
	}
	
    public void addTestDouble(String key, double value)
    {
        buf.append("double")
           .append(propertyDelimiter)
           .append(key)
           .append(propertyDelimiter)
           .append(value)
           .append(lineSeperator);
    }
    
    public void addTestBoolean(String key, boolean value)
    {
        buf.append("boolean")
           .append(propertyDelimiter)
           .append(key)
           .append(propertyDelimiter)
           .append(value)
           .append(lineSeperator);
    }
        
    public void addTestString(String key, String value)
    {
        buf.append("string")
           .append(propertyDelimiter)
           .append(key)
           .append(propertyDelimiter)
           .append(value)
           .append(lineSeperator);
    }
	
    protected String readFromFile() {   	
        return buf.toString();
    }

    protected void writeToFile(String data) {
    	StringBuffer sbdata = new StringBuffer (data);
    	buf = sbdata;
    }
    
}
