/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * When CommonTools is created, it needs a way to permanently store properties. This
 * functionality is abstracted into the PermanentStorageProxy, which is (for the purposes
 * of CommonTools) able to save and load values from a permanent store.
 * @author John
 */
public abstract class PermanentStorageProxy extends TableProxy {
    
    private static final Logger log = Logger
            .getLogger(PermanentStorageProxy.class);
    
    /**
     * Whether or not values have changed, and thus, a write is needed to save
     * these values.
     */
    protected boolean writeNeeded;
    /**
     * Where to store the values in the table.
     */
    protected String path;    
    /**
     * Permanent storage property storage delimiter
     */
    protected String propertyDelimiter = ",";
    /**
     * Permanent storage line seperator
     */
    protected String lineSeperator = "\n";
    /**
     *
     * @param name  Name of the permanentStore class
     * @param pathToPropertiesFile  Where to save the table
     */
    public PermanentStorageProxy(String pathToPropertiesFile)
    {
        super();
        writeNeeded = true;
        //PATH = "file:///Properties.txt";
        path = pathToPropertiesFile;
    }
    
    /**
     * Loads all properties from storage
     */
    public void loadFromDisk()
    {
        String propertyString = readFromFile();
        
        if (propertyString != null)
        {
            if (!propertyString.equals(""))
            {
                parsePropertyString(propertyString);
            }
            else
            {
                //Warning("PropertyString was empty when loading data! No properties loaded.");
            }
        }   
        else
        {
            //Warning("PropertyString was null when loading data! No properties loaded.");
        }
    }
    
    /**
     * Reads the table as a single String from storage
     */
    protected abstract String readFromFile();
    
    /**
     * Parses the property string and loads the values into the table.
     */
    protected void parsePropertyString(String propertyString)
    {
        // 1) split the string by line
        String[] propertyLines = propertyString.split(lineSeperator);
        
        // 2) for each propertyLine, split by delimeter and load it into memory
        for (int i = 0; i < propertyLines.length; i++)
        {
            String line = propertyLines[i];
            String[] keyValuePair = line.split(propertyDelimiter);
            String type = keyValuePair[0];
            String key = keyValuePair[1];
            String value = keyValuePair[2];
            
            try {
                if (type.equals("double")) {
                    this.setDouble(key, Double.parseDouble(value));
                }
                else if (type.equals("boolean")) {
                    this.setBoolean(key, parseBoolean(value));
                }
                else if (type.equals("string")) {
                    this.setString(key, value);
                }   
            }
            catch (Exception e) {
                //this.Warning("Unable to parse property " + key + " with value " + value);
                //this.Warning(e.getMessage());
            }
        }
    }
    
    
    public void setDouble(String key, double value)
    {
        super.setDouble("double" + propertyDelimiter + key, value);
        writeNeeded = true;
    }
    
    public void setBoolean(String key, boolean value)
    {
        super.setBoolean("boolean" + propertyDelimiter + key, value);
        writeNeeded = true;
    }
    
    public void setString(String key, String value)
    {
        super.setString("string" + propertyDelimiter + key, value);
        writeNeeded = true;
    }
    
    public Double getDouble(String key)
    {
        return super.getDouble("double" + propertyDelimiter + key);
        
    }
    
    public Boolean getBoolean(String key)
    {
        return super.getBoolean("boolean" + propertyDelimiter + key);
    }
    
    public String getString(String key)
    {
        return super.getString("string" + propertyDelimiter + key);
    }
    
    /**
     * Saves all properties to disk.
     */
    public void saveToDisk()
    {
        if (writeNeeded)
        {
            log.info("Saving properties to disk");
            writeNeeded = false;
            String serializedProperties = serializePropertiesToString();
            writeToFile(serializedProperties);
        }
        else
        {
            log.debug("Skipping saving properties to disc - write not needed.");
        }
    }    
    
    /**
     * Converts a boolean to string
     */
    public static boolean parseBoolean(String string)
    {
        if (string.equals("true")) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Serializes all the properties in the table into a single string (already
     * includes property and line delimeters).
     */
    protected String serializePropertiesToString()
    {
        //Info("Serializing Properties to String");
        // Iterate through the hashtable, make a long string.
        StringBuffer buf = new StringBuffer();
        
        int escape = 0;
        List<String> keys = Collections.list(table.keys());
        
        if (keys.size() == 0)
        {
            log.error("No properties to serialize!");
            return "";
        }
        
        // Sort by key name (ignoring type)
        keys.sort((String u1, String u2) -> 
                 u1.split(",")[1].compareTo(u2.split(",")[1]
             ));
        for(String key: keys)
        {
            buf
                .append(key)
                .append(propertyDelimiter)
                .append(table.get(key))
                .append(lineSeperator);
            
            escape++;
            if (escape > 500)
            {
                break;
            }
        }
        
        return buf.toString();
    }
    
    /**
     * Write the given string to storage in an implementation-specific way.
     */
    protected abstract void writeToFile(String serializedProperties);
}
