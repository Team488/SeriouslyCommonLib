package xbot.common.subsystems.compressor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.properties.TunableFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompressorSubsystemTest extends BaseCommonLibTest  {

    private CompressorSubsystem compressorSubsystem;

    @BeforeEach
    public void setup() {
        TunableFactory tunableFactory = getInjectorComponent().tunableFactory();
        compressorSubsystem = new CompressorSubsystem(getInjectorComponent().compressorFactory(), tunableFactory);
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
