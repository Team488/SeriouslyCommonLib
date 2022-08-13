package xbot.common.properties;

import xbot.common.injection.BaseWPITest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyFactoryTest extends BaseWPITest {
    
    @Test
    public void testNoDoubleSlashes() {
        PropertyFactory factory = injectorComponent.propertyFactory();
        assertEquals(-1, factory.createFullKey("my//mykey").indexOf("//"));

        factory.setPrefix("prefix");
        assertEquals(-1, factory.createFullKey("/mykey").indexOf("//"));
    } 
}