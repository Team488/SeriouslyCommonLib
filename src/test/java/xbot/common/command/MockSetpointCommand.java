package xbot.common.command;

import javax.inject.Inject;

public class MockSetpointCommand extends BaseSetpointCommand {

    @Inject
    public MockSetpointCommand(MockSetpointSubsystem system) {
        super(system);
    }
    
    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
    }
}
