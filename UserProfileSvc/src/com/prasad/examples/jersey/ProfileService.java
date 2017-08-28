package com.prasad.examples.jersey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import sun.misc.BASE64Decoder;

@Path("/ProfileService")
public class ProfileService 
{
	static Map<Integer, Profile> profileMap = new HashMap<Integer, Profile>();	
	private static AtomicInteger empNumber = new AtomicInteger();
	
	public int getEmpNumber()
	{
		return empNumber.getAndIncrement(); 
	}
	
	@POST
	@Path("/profiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postProfiles(Profile p, @HeaderParam("authorization") String authString) 
	{
		if(!isUserAuthenticated(authString))
		{
			return Response.status(
					Response.Status.FORBIDDEN).entity("You are not authorized!!").build();
        }
		int i = getEmpNumber();		
		profileMap.put(i, p);
		System.out.println("In POST Profiles.."+profileMap.toString());
		return Response.status(Response.Status.OK).entity(String.valueOf(i)).build();
	}
	
	@GET
	@Path("/profiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfiles(@HeaderParam("authorization") String authString) 
	{	
		if(!isUserAuthenticated(authString))
		{
            return Response.status(
					Response.Status.FORBIDDEN).entity("You are not authorized!!").build();
        }
		System.out.println("In get Profiles.."+profileMap.toString());
		return Response.status(Response.Status.OK).entity(profileMap.values()).build();		
	}
	
	@GET
	@Path("/profiles/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Profile getProfiles(@PathParam("id") int id) 
	{	
		System.out.println("In get Profiles by id.."+profileMap.toString());
		return profileMap.get(id);		
	}
	
	private boolean isUserAuthenticated(String authString){
        
        String decodedAuth = "";
        // Header is in the format "Basic 5tyc0uiDat4"
        // We need to extract data before decoding it back to original string
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        // Decode the data back to original string
        byte[] bytes = null;
        try 
        {
            bytes = new BASE64Decoder().decodeBuffer(authInfo);
        } catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        decodedAuth = new String(bytes);
        System.out.println(decodedAuth);
         
        /**
         * here you include your logic to validate user authentication.
         * it can be using ldap, or token exchange mechanism or your 
         * custom authentication mechanism.
         */
        // your validation code goes here....
        String[] decodedAuthParts = decodedAuth.split(":");
        
        String s = RegistrationService.registrationMap.get(decodedAuthParts[0]);
         
        if((s!=null) && s.equals(decodedAuthParts[1]))
        {
        	return true;
        }
        return false;
	}
}