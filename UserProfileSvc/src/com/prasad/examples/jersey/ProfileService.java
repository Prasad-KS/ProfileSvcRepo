package com.prasad.examples.jersey;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import sun.misc.BASE64Decoder;

@Path("/ProfileService")
public class ProfileService 
{
	private static final int FIRST_NAME_MAX_LENGTH = 50;
	private static final int LAST_NAME_MAX_LENGTH = 50;
	private static final int EMAIL_MAX_LENGTH = 50;
	private static final int PHONE_MAX_LENGTH = 50;
	
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
		
		//validations
		if(p.getFirst_name() == null || 
				p.getFirst_name().length()>FIRST_NAME_MAX_LENGTH)
		{
			return Response.status(
					Response.Status.NOT_ACCEPTABLE).entity("First name is null or "
							+ "exceeds max allowed value!!").build();
		}
		
		//TODO other validations..
		
		int i = getEmpNumber();		
		profileMap.put(i, p);
		System.out.println("In POST Profiles.."+profileMap.toString());
		
		Connection c = null;
		try 
		{
			c = new DBConnection().getConnection();
		} 
		catch (Exception e) 
		{
			return Response.status(
					Response.Status.SERVICE_UNAVAILABLE).entity("Internal Error").build();
		}
		PreparedStatement ps = null;
		try 
		{
			ps = c.prepareStatement("insert into userprofile (first_name, last_name, email, phone)"
					+ "values (?,?,?,?)");
			ps.setString(1, p.getFirst_name());
			ps.setString(2, p.getLast_name());
			ps.setString(3, p.getEmail());
			ps.setString(4, p.getPhone());
			System.out.println(ps.executeUpdate());
			
		} 
		catch (SQLException e) 
		{
			return Response.status(
					Response.Status.SERVICE_UNAVAILABLE).entity("Internal Error").build();
		}
		finally
		{
			try
			{
				ps.close();
				c.close();
			}
			catch(Exception e)
			{
				System.out.println("Exception closing the resources..");
			}
		}
		
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
	public Response getProfiles(@PathParam("id") int id, @HeaderParam("authorization") String authString) 
	{	
		if(!isUserAuthenticated(authString))
		{
            return Response.status(
					Response.Status.FORBIDDEN).entity("You are not authorized!!").build();
        }
		System.out.println("In get Profiles by id.."+profileMap.toString());
		return Response.status(Response.Status.OK).entity(profileMap.get(id)).build();		
	}
	
	@GET @Path("/search")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response search(@Context UriInfo  uriInfo) 
	{
		
		//TODO - bad code of searching all values in the map!!
		//currently, only searches on first name
		String fName = uriInfo.getQueryParameters().getFirst("first_name");
		for (Entry<Integer, Profile> entry : profileMap.entrySet()) {
	        Profile p = entry.getValue();
	        if (p.getFirst_name()!=null && p.getFirst_name().equals(fName))
	        {
	            System.out.println("got you!!" + entry.getKey());
	        }
	    }

		return null;
		
	}
	
	@DELETE
	@Path("/profiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProfiles()
	{
		return Response.status(
				Response.Status.METHOD_NOT_ALLOWED).entity("Delete all profiles "
						+ "is not supported!!").build();
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
