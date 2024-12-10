package com.unisys.config;



import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.unisys.controller.JndiTestController;
import com.unisys.controller.MessagePublisher;
import com.unisys.controller.UserResource;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(UserResource.class); // Register Jersey REST Controller
        register(JndiTestController.class);
        register(MessagePublisher.class);
        register(SecurityExceptionMapper.class);
    }
}