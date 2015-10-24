package xbot.common.properties;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 *
 * @author John
 */
public abstract class DatabaseStorageBase implements ITableProxy {

    private static Logger log = Logger.getLogger(DatabaseStorageBase.class);

    private String dbUrlPreFormat = "jdbc:derby:%1s;create=true";
    private final String dbUrl;
    private Connection conn;

    public DatabaseStorageBase(String databaseDirectory) {
        
        dbUrl = String.format(dbUrlPreFormat, databaseDirectory);
        
        try {
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            log.error("Could not open a connection to the database! No properties will be loaded or persisted!!");
            log.error(e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Could not open a connection to the database! No properties will be loaded or persisted!!");
            log.error(e.toString());
            e.printStackTrace();
        }        
        
        // create properties if not exists
        if (propertiesTableExists() == false)
        {
            createPropertiesTable();
        }
    }
    
    public void setDouble(String key, double value){
        saveProperty("double", key, Double.toString(value));
    }
    
    public void setBoolean(String key, boolean value){
        saveProperty("boolean", key, Boolean.toString(value));
    }
    
    public void setString(String key, String value){
        saveProperty("string", key, value);
    }
    
    public Double getDouble(String key){
        String value = loadProperty(key);
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
    
    public Boolean getBoolean (String key){
        String value = loadProperty(key);
        return parseBoolean(value);
    }
    
    private Boolean parseBoolean(String value)
    {
        if (value.toLowerCase().equals("true"))
        {
            return true;
        }
        
        if (value.toLowerCase().equals("false"))
        {
            return false;
        }
        
        return null;
    }
    
    public String getString (String key){
        String value = loadProperty(key);
        if (value.length() == 0)
        {
            return null;
        }
        return value;
    }
    
    public void clear(){
        obliterateStorage();
    }
    
    private void saveProperty(String type, String name, String value) {
       
        String payload = "UPDATE PROPERTIES SET TYPE='" + name + "', VALUE='" + type + "' WHERE NAME = '" + value + "'";
        Statement sta;
        try {
            sta = conn.createStatement();
            int count = sta.executeUpdate(payload);

            if (count == 0) {
                // Looks like this isn't currently in the database. We need to add it instead.
                payload = "INSERT INTO PROPERTIES VALUES ('" + name + "', '" + type + "', '" + value + "')";
                Statement insert = conn.createStatement();
                count = insert.executeUpdate(payload);
            }
        } catch (SQLException e) {
            log.warn("Unable to save property " + name + "!");
            log.warn(e.toString());
            e.printStackTrace();
        }
    }
    
    private String loadProperty(String name)
    {
        try {
            Statement sta = conn.createStatement();
            String payload = "SELECT * FROM PROPERTIES WHERE NAME = '" + name + "'";
            ResultSet rs = sta.executeQuery(payload);
            
            if (rs.next())
            {
                String value = rs.getString("Value");
                rs.close();
                return value;
            }
            rs.close();
        } catch (SQLException e) {
            log.warn("Unable to load property " + name + "!");
            log.warn(e.toString());
            e.printStackTrace();
        }
        return "";        
    }

    /**
     * @return True if the PROPERTIES table is present in the Database; False otherwise
     */
    private boolean propertiesTableExists() {
        try {
            DatabaseMetaData md = conn.getMetaData();
            // The following method returns tables that match a given pattern. Since we don't care about
            // Catalog, Schema, or Types (we only care about the TableNamePattern), we leave those
            // fields null.
            ResultSet tables = md.getTables(null, null, "PROPERTIES", null);

            if (tables.next()) {
                tables.close();
                return true;
            }
            tables.close();
            return false;

        } catch (SQLException e) {
            log.warn("Ran into problems when checking to see if the PROPERTIES table existed.");
            log.warn(e.toString());
            e.printStackTrace();
        }
        return false;
    }

    private boolean createPropertiesTable() {
        
        Statement sta;
        try {
            sta = conn.createStatement();
            String payload = "CREATE TABLE PROPERTIES (Name VARCHAR(100), Type VARCHAR(20), Value VARCHAR(50))";
            int count = sta.executeUpdate(payload);
            return true;
        } catch (SQLException e) {
            log.warn("Could not create the properties table! Properties will not be saved!!");
            log.warn(e.toString());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean obliterateStorage() {
        try {
            
            if (propertiesTableExists()) {
                Statement sta = conn.createStatement();
                String payload = "DROP TABLE PROPERTIES";

                int response = sta.executeUpdate(payload);

                if (response == 0) {
                    return true;
                }
                // something went wrong
                return false;
            } else {
                // table does not exist, nothing to obliterate
                return true;
            }
        } catch (SQLException e) {
            log.warn("Something went wrong when attempting to drop the PROPERTIES table!");
            log.warn(e.toString());
            e.printStackTrace();
            return false;
        }

    }
}
