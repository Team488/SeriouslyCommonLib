package xbot.common.properties;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyFactoryTest extends BaseCommonLibTest {
    
    @Test
    public void testNoDoubleSlashes() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        assertEquals(-1, factory.createFullKey("my//mykey").indexOf("//"));

        factory.setPrefix("prefix");
        assertEquals(-1, factory.createFullKey("/mykey").indexOf("//"));
    } 
}