package com.kdsl.solms.service;

import com.kdsl.solms.dto.MsgInfo;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublisherCallback  implements JCSMPStreamingPublishCorrelatingEventHandler {

    private static final Logger LOGGER= LoggerFactory.getLogger(PublisherCallback.class);

    @Override
    public void handleErrorEx(Object key, JCSMPException cause, long timestamp) {
        if (key instanceof MsgInfo) {
            MsgInfo i = (MsgInfo) key;
            i.acked = true;
            LOGGER.info("Message response (rejected) received for {}, error was {} \n", i, cause);
        }
//        latch.countDown();
    }

    @Override
    public void responseReceivedEx(Object key) {
        if (key instanceof MsgInfo) {
            MsgInfo i = (MsgInfo) key;
            i.acked = true;
            i.publishedSuccessfully = true;
            LOGGER.info("Message response (accepted) received for {} \n", i);
        }
//        latch.countDown();
    }
}
