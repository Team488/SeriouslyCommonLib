package xbot.common.networking;

import java.util.function.Consumer;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class MockZeromqListener implements XZeromqListener {

    @AssistedFactory
    public abstract static class MockZeromqListenerFactory implements XZeromqListenerFactory {
        public abstract MockZeromqListener create(
                @Assisted("connectionString") String connectionString,
                @Assisted("topic") String topic);
    }

    @AssistedInject
    public MockZeromqListener(@Assisted("connectionString") String connectionString, @Assisted("topic") String topic) {
    }

    Consumer<String> handler;

    @Override
    public void start() {

    }

    @Override
    public void setNewPacketHandler(Consumer<String> handler) {
        this.handler = handler;
    }

    public void sendPacket(String packet) {
        handler.accept(packet);
    }

}