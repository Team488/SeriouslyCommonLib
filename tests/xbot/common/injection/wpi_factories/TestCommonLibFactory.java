package xbot.common.injection.wpi_factories;

import org.junit.Test;

import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.injection.BaseWPITest;

public class TestCommonLibFactory extends BaseWPITest {

    @Test
    public void testPDP() {
        CommonLibFactory clf = injector.getInstance(CommonLibFactory.class);
        
        XPowerDistributionPanel xpdp = clf.createPowerDistributionPanel();
    }
}
