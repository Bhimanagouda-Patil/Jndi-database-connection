package com.unisys.controller;

import com.unisys.model.SystemMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.annotation.PostConstruct;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Properties;

@Path("/publish")
@Component
public class MessagePublisher {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${jms.connection.factory.name}")
    private String connectionFactoryName;

    @Value("${jms.queue.name}")
    private String queueName;

    private ConnectionFactory connectionFactory;
    private Queue queue;

    @PostConstruct
    public void init() {
        try {
            // JNDI Properties Setup
            Properties jndiProps = new Properties();
            jndiProps.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            jndiProps.setProperty(Context.PROVIDER_URL, brokerUrl);
            jndiProps.setProperty("connectionFactoryNames", connectionFactoryName);
            jndiProps.setProperty("queue." + queueName, queueName);

            // Lookup resources
            Context context = new InitialContext(jndiProps);
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryName);
            queue = (Queue) context.lookup(queueName);
        } catch (NamingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize MessagePublisher", e);
        }
    }

    @POST
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishMessage(SystemMessage systemMessage) {
        try (Connection connection = connectionFactory.createConnection(username, password)) {
            connection.start();

            // Create session and producer
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);

            // Create and send ObjectMessage
            ObjectMessage message = session.createObjectMessage(systemMessage);
            producer.send(message);

            return Response.ok("Message sent successfully.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to send message: " + e.getMessage())
                    .build();
        }
    }
}
