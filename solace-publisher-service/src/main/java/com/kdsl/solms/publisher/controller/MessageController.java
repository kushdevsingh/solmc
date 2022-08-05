package com.kdsl.solms.publisher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kdsl.solms.publisher.dto.Messages;
import com.kdsl.solms.publisher.service.PublisherService;
import com.solacesystems.jcsmp.JCSMPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class MessageController {

    private static final Logger LOGGER= LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private PublisherService publisherService;

    @Autowired
    ObjectMapper objectMapper;


    @PostMapping(path = "/publish")
    public String publishMessage(@RequestBody List<Messages> allMessages) throws JCSMPException {

        Map<String, List<Messages>> messageByDestination =  allMessages.stream().collect(Collectors.groupingBy(Messages::getDestinationRoute));

        /**
         * Sequential Message Publish Block
        for (String eachDestination:messageByDestination.keySet()) {
            publisherService.publishMessage(messageByDestination.get(eachDestination),eachDestination);
        }
        */

        List<CompletableFuture<Boolean>> completableFuture = messageByDestination.keySet().stream().map(
                eachDestination -> CompletableFuture.supplyAsync(()->
                                sendMessages(messageByDestination.get(eachDestination),eachDestination)
                )).collect(Collectors.toList());
        return "Done!";
    }

    public boolean sendMessages(List<Messages> messageList,String destination)  {
        boolean returnValue=true;
        try {
            publisherService.publishMessage(messageList, destination);
        } catch (JCSMPException e) {
            e.printStackTrace();
            returnValue=false;
        }
        return returnValue;
    }
}
