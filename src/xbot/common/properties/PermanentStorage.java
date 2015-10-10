package xbot.common.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author John
 */
public class PermanentStorage extends PermanentStorageProxy {

    private static Logger log = Logger.getLogger(PermanentStorage.class);
    
    private String dbUrlPreFormat = "jdbc:derby:%1s;create=true";
    private String dbUrl = "";
    
    public PermanentStorage(String databaseDirectory) {
        super();
        
        dbUrl = String.format(dbUrlPreFormat, databaseDirectory);
    }
    
    public boolean obliterateStorage() {
    	try {
            Connection conn = DriverManager.getConnection(dbUrl);
            
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
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "";
    }

    protected void writeToFile(String data) {
                
        Connection conn = null;
        
        try {
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
