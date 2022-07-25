package com.kdsl.solms.service;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimplePrintingMessageListener implements XMLMessageListener {

    private static final Logger LOGGER= LoggerFactory.getLogger(SimplePrintingMessageListener.class);

    @Override
    public void onReceive(BytesXMLMessage msg) {
        if (msg instanceof TextMessage) {
            LOGGER.info("TextMessage received: {}", ((TextMessage) msg).getText());
        } else {
            LOGGER.info("Message received.");
        }
        LOGGER.info("Message Dump:{}", msg.dump());

//        latch.countDown(); // unblock main thread
    }

    @Override
    public void onException(JCSMPException e) {
        LOGGER.error("Consumer received exception: {}", e);
//        latch.countDown(); // unblock main thread
    }
}
