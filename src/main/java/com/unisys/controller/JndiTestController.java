package com.unisys.controller;



import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test")
public class JndiTestController {
	
	@GET
    @Produces(MediaType.TEXT_PLAIN)
	public String getDatasourceFromJndi() throws NamingException {
		return "DataSource retrieved from JNDI: " +
				new InitialContext().lookup("java:comp/env/jdbc/student");
	}
}