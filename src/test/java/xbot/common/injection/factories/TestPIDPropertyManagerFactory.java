package xbot.common.injection.factories;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class TestPIDPropertyManagerFactory extends BaseWPITest {

    @Test
    public void makeOne() {
        PIDPropertyManagerFactory pf = injectorComponent.pidPropertyManagerFactory();
        
        pf.create("pid", 0, 0, 0, 0);
    }
}
