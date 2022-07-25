package com.kdsl.solms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdsl.solms.dto.Messages;
import com.kdsl.solms.dto.MsgInfo;
import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PublisherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherService.class);

    final LinkedList<MsgInfo> msgList = new LinkedList<MsgInfo>();

    @Autowired
    private JCSMPSession jcsmpSession;

    @Autowired
    private SolaceUtils solaceUtils;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishMessage(List<Messages> messagesObjList, String destination) throws JCSMPException {
        Topic topic = solaceUtils.createTopic(destination);
        final XMLMessageProducer messageProducer = jcsmpSession.getMessageProducer(new PublisherCallback());

        // Publish-only session is now hooked up and running!
        LOGGER.info("Thread {}: Connected. About to publish {} messages to topic {}", Thread.currentThread().getName(), messagesObjList.size(), topic.getName());

        publishToTopic(topic, messageProducer, messagesObjList);


        /**
        //You can also just run a for-each and manually add each
        //feature to a list
        List<CompletableFuture<Void>> futures =
                (List<CompletableFuture<Void>>) messagesObjList.stream().map(eachMessage -> CompletableFuture.runAsync(() -> {
                    publishToTopic(topic, messageProducer, eachMessage);
                }));
         **/
    }

    public void publishToTopic(Topic topic, XMLMessageProducer messageProducer, List<Messages> messagesObjList) {
        messagesObjList.stream().parallel().forEach(
                (eachMessage -> {
                    LOGGER.info("Thread {}: Publishing:{}", Thread.currentThread().getName(), eachMessage.getDistributionId());
                    TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                    try {
                        String jsonString = objectMapper.writeValueAsString(eachMessage);
                        msg.setDeliveryMode(DeliveryMode.PERSISTENT);
                        msg.setText(jsonString);
                        final MsgInfo msgCorrelationInfo = new MsgInfo(eachMessage.getDistributionId());
                        msgCorrelationInfo.sessionIndependentMessage = msg;
                        msgList.add(msgCorrelationInfo);

                        msg.setCorrelationKey(msgCorrelationInfo);
                        messageProducer.send(msg, topic);

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (JCSMPException e) {
                        e.printStackTrace();
                    }
                }
                )
        );
    }
}
