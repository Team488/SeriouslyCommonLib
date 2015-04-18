/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;


/**
 *
 * @author Alex
 */
public class SmartDashboardTableWrapper implements ITableProxy {

    public void setDouble(String key, double value) {
        SmartDashboard.putNumber(key, value);
    }

    public Double getDouble(String key) {
        try {
            return Double.valueOf(SmartDashboard.getNumber(key));
        } catch (TableKeyNotDefinedException e) {
            return null;
        }
    }
        
    public void setBoolean(String key, boolean value) {
        SmartDashboard.putBoolean(key, value);
    }

    public Boolean getBoolean(String key) {
        try {
            return Boolean.valueOf(SmartDashboard.getBoolean(key));
        } catch (TableKeyNotDefinedException e) {
            return null;
        }
    }
    
    public void setString(String key, String value) {
        SmartDashboard.putString(key, value);
    }

    public String getString(String key) {
        try {
            return SmartDashboard.getString(key);
        } catch (TableKeyNotDefinedException e) {
            return null;
        }
    }

}