package xbot.common.command;

import com.google.inject.Inject;

public class MockSetpointCommand extends BaseSetpointCommand {

    @Inject
    public MockSetpointCommand(MockSetpointSystem system) {
        super(system);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
    }
}
