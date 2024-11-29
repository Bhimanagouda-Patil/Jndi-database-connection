package com.unisys.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
/**
 * Configuration class to set up JNDI DataSource for Spring Boot application using embedded Tomcat.
 * <p>
 * This class defines two main beans for configuring JNDI-based DataSource in a Spring Boot application:
 * <ul>
 *   <li>{@link TomcatServletWebServerFactory} bean configures Tomcat to enable JNDI naming and adds the DataSource as a context resource.</li>
 *   <li>{@link DataSource} bean uses JNDI to provide database connection pooling, making it available for Spring's dependency injection.</li>
 * </ul>
 * The database connection properties such as driver, URL, username, and password are injected from the application properties
 * using the {@link Value} annotation.
 * </p>
 */
@Configuration
public class JndiConfig {

    /**
     * The fully qualified name of the JDBC driver class to use for the database connection.
     * Injected from the application properties file.
     */
    @Value("${jndi.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * The JDBC URL for the database. Injected from the application properties file.
     */
    @Value("${jndi.datasource.url}")
    private String dataSourceUrl;

    /**
     * The database username. Injected from the application properties file.
     */
    @Value("${jndi.datasource.username}")
    private String username;

    /**
     * The database password. Injected from the application properties file.
     */
    @Value("${jndi.datasource.password}")
    private String password;

    /**
     * Configures the Tomcat servlet web server to enable JNDI naming and define the DataSource as a resource.
     * <p>
     * This bean customizes the Tomcat server by enabling naming and setting up the database connection as a
     * JNDI resource in the Tomcat context.
     * </p>
     *
     * @return a customized {@link TomcatServletWebServerFactory} with JNDI enabled.
     */
    @Bean
    TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            /**
             * Enables JNDI naming within the Tomcat container.
             * @param tomcat the Tomcat instance being customized
             * @return the Tomcat web server
             */
            @Override
            protected TomcatWebServer getTomcatWebServer(org.apache.catalina.startup.Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            /**
             * Adds the DataSource as a resource to the Tomcat context.
             * This resource will be looked up via JNDI in the application.
             *
             * @param context the Tomcat context to post-process
             */
            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();
                resource.setName("jdbc/student"); // JNDI name for the resource
                resource.setType(DataSource.class.getName()); // The type of the resource
                resource.setProperty("driverClassName", driverClassName); // JDBC driver class name
                resource.setProperty("url", dataSourceUrl); // Database URL
                resource.setProperty("username", username); // Database username
                resource.setProperty("password", password); // Database password
                context.getNamingResources().addResource(resource); // Add resource to context
            }
        };
    }

    /**
     * Defines a {@link DataSource} bean that performs a JNDI lookup for the database connection.
     * <p>
     * This bean will enable Spring to access the configured DataSource for database operations.
     * The DataSource will be resolved from JNDI lookup and injected wherever required in the application.
     * </p>
     *
     * @return the DataSource resolved via JNDI.
     * @throws NamingException if there is a problem with JNDI lookup.
     * @throws IllegalArgumentException if the lookup fails or is misconfigured.
     */
    @Bean
    DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/student"); // JNDI name to look up the DataSource
        bean.setProxyInterface(DataSource.class); // Proxy interface for DataSource
        bean.setLookupOnStartup(false); // Avoid lookup on startup
        bean.afterPropertiesSet(); // Initialize the JNDI lookup
        return (DataSource) bean.getObject(); // Return the DataSource from JNDI lookup
    }
}
