package com.kdsl.solms.consumer.service;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueFlowMessageListener implements XMLMessageListener {

    private static final Logger LOGGER= LoggerFactory.getLogger(QueueFlowMessageListener.class);

    @Override
    public void onReceive(BytesXMLMessage msg) {
        if (msg instanceof TextMessage) {
            LOGGER.info("TextMessage received: {}", ((TextMessage) msg).getText());
            msg.ackMessage();
        } else {
            LOGGER.info("Message received.");
        }
//        LOGGER.info("Message Dump:{}", msg.dump());
        if (msg.getRedelivered()) {  // useful check
            // this is the broker telling the consumer that this message has been sent and not ACKed before.
            // this can happen if an exception is thrown, or the broker restarts, or the netowrk disconnects
            // perhaps an error in processing? Should do extra checks to avoid duplicate processing
//            hasDetectedRedelivery = true;
        }
        // Messages are removed from the broker queue when the ACK is received.
        // Therefore, DO NOT ACK until all processing/storing of this message is complete.
        // NOTE that messages can be acknowledged from a different thread.
//        msg.ackMessage();  // ACKs are asynchronous
    }

    @Override
    public void onException(JCSMPException e) {
        LOGGER.error("Consumer received exception: {}", e);
//        latch.countDown(); // unblock main thread
    }
}
