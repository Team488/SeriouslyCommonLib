package xbot.common.command;

import javax.inject.Inject;

import xbot.common.properties.TunableFactory;

public class MockWaitForMaintainerCommand extends BaseWaitForMaintainerCommand {

    @Inject
    public MockWaitForMaintainerCommand(MockSetpointSubsystem system, TunableFactory tunableFactory) {
        super(system, tunableFactory, 1);
    }
}
