package xbot.common.networking;

import java.util.function.Consumer;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class MockCommunicationClient implements OffboardCommunicationClient {

    @AssistedInject
    public MockCommunicationClient(@Assisted("connectionString") String connectionString, @Assisted("topic") String topic) {}

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