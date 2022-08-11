package xbot.common.injection.factories;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class TestPIDFactory extends BaseWPITest {

    @Test
    public void makeOne() {
        PIDFactory pf = injectorComponent.pidFactory();
        
        pf.create("pid");
    }
}
