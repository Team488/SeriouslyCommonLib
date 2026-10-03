package xbot.common.command;

public interface TunableCommandPublisher {
    void publish(BaseCommand command);

    void publish(String label, BaseCommand command);
}