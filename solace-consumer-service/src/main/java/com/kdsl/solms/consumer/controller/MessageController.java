package com.kdsl.solms.consumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kdsl.solms.consumer.service.ConsumerService;
import com.solacesystems.jcsmp.JCSMPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(path = "/api")
public class MessageController {

    private static final Logger LOGGER= LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private ConsumerService consumerService;


    @Autowired
    ObjectMapper objectMapper;

    @GetMapping(path = "/consume")
    public String startConsumer(@RequestParam(name = "queue",required = false) String queue) throws JCSMPException {
        consumerService.readMessage(queue);
        return "Done!";
    }
}
