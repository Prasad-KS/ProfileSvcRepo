package com.prasad.examples.jersey;


import java.util.HashMap;

import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/RegistrationService")
public class RegistrationService 
{
	static Map<String, String> registrationMap = new HashMap<String, String>();
	
		
	@POST
	@Path("/registration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postRegistration(RegistrationInfo regInfo) throws Exception 
	{
		if(registrationMap.containsKey(regInfo.getEmailId()))
		{
			return Response.status(
					Response.Status.CONFLICT).entity("Already exists!!").build();
		}
		if(regInfo.getPassword() == null || regInfo.getPassword() == "")
		{
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Password criteria not met!").build();
		}
		registrationMap.put(regInfo.getEmailId(), regInfo.getPassword());
		System.out.println("In POST Registration.."+registrationMap.toString());
		return Response.status(Response.Status.OK).entity("Success").build();
	}	
}
