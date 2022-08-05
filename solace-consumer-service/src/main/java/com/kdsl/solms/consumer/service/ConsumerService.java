package com.kdsl.solms.consumer.service;

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

    private static FlowReceiver flowQueueReceiver;

    public void readMessage(String queueName) throws JCSMPException {
        Queue queue= solaceUtils.createQueue(queueName);
        ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
        flow_prop.setEndpoint(queue);
        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);  // best practice
        flow_prop.setActiveFlowIndication(true);  // Flow events will advise when

        LOGGER.info("Attempting to bind to queue {} on the broker", queue);

       try{
           flowQueueReceiver = jcsmpSession.createFlow(new QueueFlowMessageListener(), flow_prop,null,new FlowEventHandler() {
            @Override
            public void handleEvent(Object source, FlowEventArgs event) {
                // Flow events are usually: active, reconnecting (i.e. unbound), reconnected, active
                LOGGER.info("### Received a Flow event: " + event);
                // try disabling and re-enabling the queue to see in action
            }
        });
    } catch (OperationNotSupportedException e) {  // not allowed to do this
        throw e;
    } catch (JCSMPErrorResponseException e) {  // something else went wrong: queue not exist, queue shutdown, etc.
        LOGGER.error(e.getMessage());
        LOGGER.error("\n*** Could not establish a connection to queue {}: {}", queue, e.getMessage());
        LOGGER.error("Create queue using PubSub+ Manager WebGUI, and add subscription ");
        LOGGER.error("  or see the SEMP CURL scripts inside the 'semp-rest-api' directory.");
        // could also try to retry, loop and retry until successfully able to connect to the queue
        LOGGER.error("NOTE: see QueueProvision sample for how to construct queue with consumer app.");
        LOGGER.error("Exiting.");
        return;
    }
        flowQueueReceiver.start();

//        try {
////            latch.await(); // block here until message received, and latch will flip
//        } catch (InterruptedException e) {
//            LOGGER.info("I was awoken while waiting");
//        }
        LOGGER.info("Finished consuming expected messages.");


        // Close consumer
//        cons.close();
    }
}
