package xbot.common.networking;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class ZeromqListenerTest extends BaseCommonLibTest {

    int packets = 0;

    @Test
    public void pubsub() throws InterruptedException {
        ZeromqTestServer server = new ZeromqTestServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

        ZeromqListener client = new ZeromqListener("tcp://localhost:5556", "10001 ");
        client.setNewPacketHandler(packet -> handlePacket(packet));

       
        client.start();

        while (true) {
            if (packets > 10) {
                break;
            }
            System.out.println("Packet count at : " + packets);
            Thread.sleep(100);
        }
    }

    @Test
    public void startWithNoHandler() {
        ZeromqTestServer server = new ZeromqTestServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

        ZeromqListener client = new ZeromqListener("tcp://localhost:5556", "10001 ");
        client.start();        
    }

    @Test
    public void stopBeforeStart() {
        ZeromqListener client = new ZeromqListener("tcp://localhost:5556", "10001 ");
        client.stop();   
    }

    private void handlePacket(String packet) {
        System.out.println(packet);
        packets++;
    }
}