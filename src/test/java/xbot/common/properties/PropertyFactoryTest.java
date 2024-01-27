package xbot.common.properties;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyFactoryTest extends BaseCommonLibTest {
    
    @Test
    public void testNoDoubleSlashes() {
        PropertyFactory factory = getInjectorComponent().propertyFactory();
        factory.setPrefix("my//myPrefixWithDoubleSlashes");

        assertEquals(-1, factory.getCleanPrefix().indexOf("//"));

        factory.setPrefix("simplePrefixWithNoSlashes");
        assertEquals(-1, factory.getCleanPrefix().indexOf("//"));
    } 
}