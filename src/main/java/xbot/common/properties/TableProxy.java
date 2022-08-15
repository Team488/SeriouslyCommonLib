/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import java.util.Hashtable;

import javax.inject.Inject;

/**
 * A simple implementation of ITableProxy. Uses a HashTable as the table to store and save properties.
 * 
 * @author Alex
 */
public class TableProxy implements ITableProxy {

    public Hashtable<String, String> table;

    @Inject
    public TableProxy() {
        clear();
    }

    public void clear() {
        this.table = new Hashtable<String, String>();
    }

    public void setDouble(String key, double value) {
        table.put(key, Double.toString(value));
    }

    public Double getDouble(String key) {
        if (table.get(key) == null) {
            return null;
        } else {
            return Double.valueOf(table.get(key).toString());
        }
    }

    public void setBoolean(String key, boolean value) {
        table.put(key, Boolean.toString(value));
    }

    public Boolean getBoolean(String key) {
        if (table.get(key) == null) {
            return null;
        } else {
            return parseBoolean((String) table.get(key));
        }
    }

    public void setString(String key, String value) {
        table.put(key, value);
    }

    public String getString(String key) {
        return (String) table.get(key);
    }

    private static boolean parseBoolean(String string) {
        if (string.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setFastMode(boolean on) {
    }

    public void remove(String key) {
        table.remove(key);
    }
}
