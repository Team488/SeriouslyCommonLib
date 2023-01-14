package xbot.common.networking;

import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class ZeromqTestServer implements Runnable {

    @Override
    public void run() {
        //  Prepare our context and publisher
        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket publisher = context.socket(SocketType.PUB);
        publisher.setConflate(true);
        publisher.bind("tcp://*:5556");
        publisher.bind("ipc://weather");

        //  Initialize random number generator
        Random srandom = new Random(System.currentTimeMillis());
        while (!Thread.currentThread ().isInterrupted ()) {
            //  Get values that will fool the boss
            int zipcode;
            int temperature;
            int relhumidity;
            zipcode = 10000 + srandom.nextInt(10000) ;
            temperature = srandom.nextInt(215) - 80 + 1;
            relhumidity = srandom.nextInt(50) + 10 + 1;

            //  Send message to all subscribers
            String update = String.format("%05d %d %d", zipcode, temperature, relhumidity);
            publisher.send(update, 0);
        }

        publisher.close ();
        context.term ();
    }

}