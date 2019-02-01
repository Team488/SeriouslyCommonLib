package xbot.common.networking;

import java.util.function.Consumer;

import com.google.inject.Singleton;

@Singleton
public class MockCommunicationClient implements OffboardCommunicationClient {

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