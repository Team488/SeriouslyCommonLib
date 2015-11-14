/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;

/**
 *
 * @author Alex
 */
public class SmartDashboardTableWrapper implements ITableProxy {

    private static final Logger log = Logger.getLogger(SmartDashboardTableWrapper.class);

    public void setDouble(String key, double value) {
        SmartDashboard.putNumber(key, value);
    }

    public void clear() {
        // Do not clear the smart dashboard - the SmartDashboard doesn't really have a mechanism
        // to clear it, so instead we'll just put an angry warning message!
        log.warn("Somebody attempted to clear the SmartDashboard. This will never work - why are you doing this?!");
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