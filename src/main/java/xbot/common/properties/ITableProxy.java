package xbot.common.properties;

/**
 * This interface defines a simple table interface, where you can set and retrieve
 * values. All methods are strongly typed (so we have SetDouble instead of Set(object))
 * @author Alex
 */
public interface ITableProxy {

    /**
     * Writes a numeric property value.
     * @param key The name of the property.
     * @param value The value to write.
     */
    public void setDouble(String key, double value);

    /**
     * Writes a boolean property value.
     * @param key The name of the property.
     * @param value The value to write.
     */
    public void setBoolean(String key, boolean value);

    /**
     * Writes a string property value.
     * @param key The name of the property.
     * @param value The value to write.
     */
    public void setString(String key, String value);

    /**
     * Reads a numeric property value.
     * @param key The name of the property.
     * @return The value of the property, or null if it does not exist.
     */
    public Double getDouble(String key);

    /**
     * Reads a boolean property value.
     * @param key The name of the property.
     * @return The value of the property, or null if it does not exist.
     */
    public Boolean getBoolean (String key);

    /**
     * Reads a string property value.
     * @param key The name of the property.
     * @return The value of the property, or null if it does not exist.
     */
    public String getString (String key);

    /**
     * Clears all values from the table.
     */
    public void clear();

    /**
     * Sets the table to fast mode. This is a hint to the underlying implementation
     * that we are going to be doing a lot of reads and writes in a short period of time,
     * and that it should optimize for that case.
     * @param on True to turn fast mode on, false to turn it off.
     */
    public void setFastMode(boolean on);

    /**
     * Removes a key from the table.
     * @param key The name of the property to remove.
     */
    public void remove(String key);
}