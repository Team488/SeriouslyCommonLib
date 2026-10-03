package xbot.common.command;

import javax.inject.Inject;

public class MockTunableCommandPublisher implements TunableCommandPublisher {

    @Inject
    public MockTunableCommandPublisher() {}

    @Override
    public void publish(BaseCommand command) {
        // Tunables publication is intentionally disabled in unit tests.
    }

    @Override
    public void publish(String label, BaseCommand command) {
        // Tunables publication is intentionally disabled in unit tests.
    }
}