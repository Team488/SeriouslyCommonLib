package xbot.common.subsystems.compressor;

import org.junit.Before;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.properties.PropertyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CompressorSubsystemTest extends BaseCommonLibTest  {

    private CompressorSubsystem compressorSubsystem;

    @Before
    public void setup() {
        PropertyFactory propertyFactory = getInjectorComponent().propertyFactory();
        compressorSubsystem = new CompressorSubsystem(getInjectorComponent().compressorFactory(), propertyFactory);
    }

    @Test
    public void testEnable() {
        compressorSubsystem.enable();
        assertTrue(compressorSubsystem.isEnabled());
    }

    @Test
    public void testDisable() {
        compressorSubsystem.disable();
        assertFalse(compressorSubsystem.isEnabled());
    }

    @Test
    public void testGetCompressorCurrent() {
        assertEquals(0.0, compressorSubsystem.getCompressorCurrent(), 0.001);
    }

    @Test
    public void testGetEnableCommand() {
        assertNotNull(compressorSubsystem.getEnableCommand());
    }

    @Test
    public void testGetDisableCommand() {
        assertNotNull(compressorSubsystem.getDisableCommand());
    }

    @Test
    public void testPeriodic() {
        compressorSubsystem.enable();
        compressorSubsystem.periodic();
        assertTrue(compressorSubsystem.isEnabled());

        compressorSubsystem.disable();
        compressorSubsystem.periodic();
        assertFalse(compressorSubsystem.isEnabled());
    }
}