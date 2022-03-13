/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

/**
 * This interface defines a simple table interface, where you can set and retrieve
 * values. All methods are strongly typed (so we have SetDouble instead of Set(object))
 * @author Alex
 */
public interface ITableProxy {
        
    public void setDouble(String key, double value);
    
    public void setBoolean(String key, boolean value);
    
    public void setString(String key, String value);
    
    public Double getDouble(String key);
    
    public Boolean getBoolean (String key);
    
    public String getString (String key);
    
    public void clear();
    
    public void setFastMode(boolean on);

    public void remove(String key);
}