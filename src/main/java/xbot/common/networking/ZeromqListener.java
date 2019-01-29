package xbot.common.networking;

import java.util.StringTokenizer;
import java.util.function.Consumer;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;

public class ZeromqListener implements OffboardCommunicationClient {

    private static Logger log = Logger.getLogger(ZeromqListener.class);
    private Consumer<String> packetHandler;
    private ZeromqClient client;
    private final String connectionString;
    private final String topic;

    /**
     * 
     * @param connectionString Typically something like "tcp://localhost:5556"; read the ZeroMQ documentation.
     */
    @AssistedInject
    public ZeromqListener(@Assisted("connectionString") String connectionString, @Assisted("topic") String topic) {
        this.connectionString = connectionString;
        this.topic = topic;
    }

    @Override
    public void start() {
        if(client != null) {
            log.warn("Server already running; cannot start again.");
            return;
        }
        
        client = new ZeromqClient(connectionString, topic);
        client.setNewPacketHandler(this.packetHandler);
        client.startClient();
    }

    public void stop() {
        if (client != null) {
            client.stopClient();
        } else {
            log.warn("Tried to stop the client before starting it!");
        }
    }

    @Override
    public void setNewPacketHandler(Consumer<String> handler) {
        this.packetHandler = handler;
    }

    private static class ZeromqClient extends Thread {
        private static Logger log = Logger.getLogger(ZeromqClient.class);
        private volatile boolean isRunning = false;
        private volatile Consumer<String> packetHandler;
        private volatile boolean lastHandlerState = true;
        private String connectionString;
        private String topic;
        private ZMQ.Context context;
        private ZMQ.Socket socket;

        public ZeromqClient(String connectionString, String topic) {
            this.connectionString = connectionString;
            this.topic = topic;
        }

        public void setNewPacketHandler(Consumer<String> packetHandler) {
            this.packetHandler = packetHandler;
        }

        public void startClient() {
            if (this.isRunning) {
                log.warn("Client already started, will not start it again.");
                return;
            }

            if (packetHandler == null) {
                log.warn("No packet handler registered - you will not get any network messages!");
            }

            log.info("Creating ZMQ Context");
            context = ZMQ.context(1);
            log.info("Creating ZMQ Subscriber Socket");
            socket = context.socket(ZMQ.SUB);
            // SetConflate to True means we only get the most recent value. It will also be need to set server-side, I think.
            socket.setConflate(true);
            log.info("Connecting Socket with connection string: " + connectionString);
            boolean result = socket.connect(connectionString);
            if (result) {
                log.info("Connection succeeded.");
            } else {
                log.warn("Connection failed!");
            }
            log.info("Setting subscription filter to: " + topic);
            socket.subscribe(topic.getBytes());
            isRunning = true;
            this.start();
        }

        public void stopClient() {
            log.info("client stopping");
            isRunning = false;

            socket.close();
            context.term();
        }    

        @Override
        public void run() {
            while (isRunning) {
                String packet = socket.recvStr(0).trim();
                if (packetHandler != null) {
                    packetHandler.accept(packet);
                }
            }
        }
    }

}