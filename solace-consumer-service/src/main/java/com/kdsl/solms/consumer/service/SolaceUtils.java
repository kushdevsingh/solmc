package com.kdsl.solms.consumer.service;

import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolaceUtils {
    private static final Logger LOGGER= LoggerFactory.getLogger(SolaceUtils.class);

    @Autowired
    private JCSMPSession jcsmpSession;

    public Topic createTopic(String name){
        Topic topic = JCSMPFactory.onlyInstance().createTopic(name);
        return topic;
    }

    public Queue createQueue(String name) throws JCSMPException {
        Queue queue = JCSMPFactory.onlyInstance().createQueue(name);
        return queue;
    }
    public Queue provisionQueue(String name) throws JCSMPException {
        Queue queue = createQueue(name);
        jcsmpSession.provision(queue, endpointProvisionProperties(), JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
        return queue;
    }
    public EndpointProperties endpointProvisionProperties(){
        EndpointProperties endpointProvisionProperties = new EndpointProperties();
        endpointProvisionProperties.setPermission(EndpointProperties.PERMISSION_DELETE);
        endpointProvisionProperties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
        endpointProvisionProperties.setQuota(100);
        return endpointProvisionProperties;
    }

    public void addSubscription(Queue queue, Topic topic) throws JCSMPException {
        jcsmpSession.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);
    }




}
