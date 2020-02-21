package xbot.common.command;

import com.google.inject.Inject;

import xbot.common.properties.PropertyFactory;

public class MockWaitForMaintainerCommand extends BaseWaitForMaintainerCommand {

    @Inject
    public MockWaitForMaintainerCommand(MockSetpointSystem system, PropertyFactory pf) {
        super(system, pf, 1);
    }
}
