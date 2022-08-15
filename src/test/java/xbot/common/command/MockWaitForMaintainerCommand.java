package xbot.common.command;

import javax.inject.Inject;

import xbot.common.properties.PropertyFactory;

public class MockWaitForMaintainerCommand extends BaseWaitForMaintainerCommand {

    @Inject
    public MockWaitForMaintainerCommand(MockSetpointSubsystem system, PropertyFactory pf) {
        super(system, pf, 1);
    }
}
