package com.kdsl.solms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private static final Logger LOGGER= LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    private JCSMPSession jcsmpSession;

    @Autowired
    private SolaceUtils solaceUtils;

    @Autowired
    private ObjectMapper objectMapper;

    public void readMessage(String queueName) throws JCSMPException {
        Queue queue= solaceUtils.createQueue(queueName);
        ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
        flow_prop.setEndpoint(queue);
        Consumer cons = jcsmpSession.createFlow(new SimplePrintingMessageListener(), flow_prop);
        cons.start();

//        try {
////            latch.await(); // block here until message received, and latch will flip
//        } catch (InterruptedException e) {
//            LOGGER.info("I was awoken while waiting");
//        }
        LOGGER.info("Finished consuming expected messages.");


        // Close consumer
        cons.close();
    }
}
