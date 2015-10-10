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
public class PermanentStorageBase extends PermanentStorageProxy {

    private static Logger log = Logger.getLogger(PermanentStorageBase.class);
    
    private String dbUrlPreFormat = "jdbc:derby:%1s;create=true";
    private String dbUrl = "";
    
    public PermanentStorageBase(String databaseDirectory) {
        super();
        
        dbUrl = String.format(dbUrlPreFormat, databaseDirectory);
    }
    
    private boolean propertiesTableExists(Connection conn)
    {
		try {
			DatabaseMetaData md = conn.getMetaData();
            ResultSet tables = md.getTables(null, null, "PROPERTIES", null);
            
            if (tables.next())
            {
            	return true;
            }
            return false;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
    
    public boolean obliterateStorage() {
    	try {
            Connection conn = DriverManager.getConnection(dbUrl);
            
            if (propertiesTableExists(conn))
            {
            	Statement sta = conn.createStatement();
                String payload = "DROP TABLE PROPERTIES";
                
                int response = sta.executeUpdate(payload);
                
                if (response == 0)
                {
                	return true;
                }
                // something went wrong
                return false;
            }
            else
            {
            	// table does not exist, nothing to obliterate
            	return true;
            }
    	}
    	catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    	
    }

    protected String readFromFile() {
        // Need to read out the database into a string
        
        try {
            Connection conn = DriverManager.getConnection(dbUrl);
            
            if (propertiesTableExists(conn))
            {
	            Statement sta = conn.createStatement();
	            String payload = "SELECT * FROM PROPERTIES";
	
	            ResultSet rs = sta.executeQuery(payload);
	            
	            StringBuffer sb = new StringBuffer();
	            
	            while ( rs.next() ) {
	                sb.append(rs.getString("Type"));
	                sb.append(propertyDelimiter);
	                sb.append(rs.getString("Name"));
	                sb.append(propertyDelimiter);
	                sb.append(rs.getString("Value"));
	                sb.append(lineSeperator);
	            }
	            
	            return sb.toString();
            }
            else
            {
            	// could not load properties table.
            	log.error("Could not find the properties table in the database when loading properties!");
            	log.error("All properties will be at their default levels!");
            }
        } 
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "";
    }

    protected void writeToFile(String data) {
                
        Connection conn = null;
        
        try {
        	// we need to be more resilient here, and only create if table doesn't exist.
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            // create table if it doesn't exist
            Statement sta = conn.createStatement();
            String payload = "CREATE TABLE PROPERTIES (Name VARCHAR(100), Type VARCHAR(20), Value VARCHAR(50))";
            int count = sta.executeUpdate(payload);
            sta.close();
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        
        // split up that string
        String[] splitData = data.split(lineSeperator);
        
        // each row actually needs to be split again;
        for (String line : splitData)
        {
            String[] values = line.split(propertyDelimiter);
            // values is currently the order of Type, Name, Value
            
            // try to update. If update doens't work or updates no rows, we'll try and insert directly.
            String payload = "UPDATE PROPERTIES SET TYPE='" + values[0] + "', VALUE='" + values[2] + "' WHERE NAME = '" + values[1] + "'";
            Statement sta;
            try {
                sta = conn.createStatement();
                int count = sta.executeUpdate(payload);
                
                if (count == 0)
                {
                    // Looks like this isn't currently in the database. We need to add it instead.
                    payload = "INSERT INTO PROPERTIES VALUES ('" + values[1] + "', '"+ values[0] + "', '" + values[2] + "')";
                    Statement insert = conn.createStatement();
                    count = insert.executeUpdate(payload);
                }
            } 
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
