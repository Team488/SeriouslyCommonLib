package xbot.common.command;

import javax.inject.Inject;

import org.wpilib.tunable.Tunables;

import xbot.common.logging.assertion.RobotAssertionHandler;

public class RealTunableCommandPublisher implements TunableCommandPublisher {

    private final RobotAssertionHandler assertionHandler;

    @Inject
    public RealTunableCommandPublisher(RobotAssertionHandler assertionHandler) {
        this.assertionHandler = assertionHandler;
    }

    @Override
    public void publish(BaseCommand command) {
        String path = command.getName();
        if (!Tunables.publish(path, command)) {
            reportDuplicatePath(path);
        }
    }

    @Override
    public void publish(String label, BaseCommand command) {
        if (!Tunables.publish(label, command)) {
            reportDuplicatePath(label);
        }
    }

    private void reportDuplicatePath(String path) {
        assertionHandler.fail("Cannot publish command to Tunables path '" + path
                + "' because that path is already in use; the first command remains published.");
    }
}
