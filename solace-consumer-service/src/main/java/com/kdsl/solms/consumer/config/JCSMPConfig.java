package com.kdsl.solms.consumer.config;

import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JCSMPConfig {

    private static final Logger LOGGER= LoggerFactory.getLogger(JCSMPConfig.class);

    @Value("${solms.host}")
    String host;
    @Value("${solms.vpn}")
    String vpn;
    @Value("${solms.username}")
    String username;
    @Value("${solms.password}")
    String password;

    @Bean
    public JCSMPProperties jcsmpProperties(){
        LOGGER.info("host :{}",host);
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, host);     // host:port
        properties.setProperty(JCSMPProperties.VPN_NAME, vpn); // message-vpn
        properties.setProperty(JCSMPProperties.USERNAME,  username); // client-username
        properties.setProperty(JCSMPProperties.PASSWORD, password); // client-password
        properties.setProperty(JCSMPProperties.SSL_VALIDATE_CERTIFICATE,true);
        return properties;
    }

    @Bean
    public JCSMPSession jcsmpSession(JCSMPProperties jcsmpProperties) throws JCSMPException {
        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(jcsmpProperties);
        session.connect();

        // Confirm the current session supports the capabilities required.
        if (    session.isCapable(CapabilityType.PUB_GUARANTEED) &&
                session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED) &&
                session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT) &&
                session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS)) {
            LOGGER.info("All required capabilities supported!");
        } else {
            LOGGER.error("Capabilities not met!");
            LOGGER.error("Capability - PUB_GUARANTEED: {} " , session.isCapable(CapabilityType.PUB_GUARANTEED));
            LOGGER.error("Capability - SUB_FLOW_GUARANTEED: {} " , session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED));
            LOGGER.error("Capability - ENDPOINT_MANAGEMENT: {} " , session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT));
            LOGGER.error("Capability - QUEUE_SUBSCRIPTIONS: {} " , session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS));
            System.exit(1);
        }
        return session;
    }
}
