package com.sambesnier.StudentLinkRestWs.resources;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sambesnier.db.dao.impl.Repository;
import com.sambesnier.db.models.AuthenticationToken;
import com.sambesnier.db.models.User;
import com.sambesnier.security.Authenticator;
import com.sambesnier.security.Encrypter;

@Path("users")
public class UsersResource {
	
	@SuppressWarnings("unchecked")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
		
		Repository repo = new Repository();
		List<User> users = (List<User>) repo.readAll(User.class);
		
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = (User) iterator.next();
			JSONObject jsonUser = new JSONObject();
			jsonUser.put("email", user.getEmail());
			jsonUser.put("username", user.getUsername());
			jsonUser.put("firstname", user.getFirstName());
			jsonUser.put("lastname", user.getLastName());
			array.add(jsonUser);
		}
		
		obj.put("users", array);
		
        return Response.ok(obj.toJSONString()).build();
    }
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newUser(String payload) {
		
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(payload);
			User user = new User();
			user.setEmail(json.get("email").toString());
			user.setPassword(json.get("password").toString());
			user.setUsername(json.get("username").toString());
			
			Repository repo = new Repository();
			repo.create(user);
			
			json.remove("password");
			
			return Response.ok(json.toJSONString()).build();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.BAD_REQUEST).build();
		
	}
	
	@Path("login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(String payload) {
		
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(payload);
			
			Repository repo = new Repository();
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("login", json.get("username").toString());
			Object obj = repo.queryObject("User.findByEmailOrUserName", prop);
			
			if (obj!=null && ((User)obj).getPassword().equals(json.get("password").toString())) {
		        
	        	User user =  (User)obj;
	        	
	        	Authenticator.removeExistingToken(user, repo);
	        	
	        	AuthenticationToken token = new AuthenticationToken();
	        	token.setUsername(user.getUsername());
//	        	Authenticator.isValidToken(token, repo);
	        	String value = Encrypter.sha1(UUID.randomUUID().toString());
	        	token.setToken(value);
	        	
	        	repo.create(token);
	        	
	        	JSONObject tokenValue = new JSONObject();
	        	tokenValue.put("username", user.getUsername());
	        	tokenValue.put("token", value);
	        	
	        	return Response.ok(tokenValue.toJSONString()).build();
	 
	        }
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(Status.UNAUTHORIZED).build();
		
	}
	
	@Path("tokens")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response verifyToken(String payload) {
		
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(payload);
			
			Repository repo = new Repository();
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("login", json.get("username").toString());
			prop.put("token", json.get("token").toString());
			Object obj = repo.queryObject("AuthenticationToken.findByUserNameAndToken", prop);
			
			if (obj!=null) {
		        
	        	AuthenticationToken token = (AuthenticationToken) obj;
	        	
	        	if (Authenticator.isValidToken(token, repo)) {
	        		repo.delete(token);
	        		AuthenticationToken newToken = new AuthenticationToken();
	        		newToken.setUsername(json.get("username").toString());
	        		newToken.setToken(Encrypter.sha1(UUID.randomUUID().toString()));
	        		repo.create(newToken);
	        		
	        		JSONObject tokenValue = new JSONObject();
		        	tokenValue.put("username", newToken.getUsername());
		        	tokenValue.put("token", newToken.getToken());
		        	
		        	return Response.ok(tokenValue.toJSONString()).build();
	        	}
	        }
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(Status.UNAUTHORIZED).build();
		
	}
	
	@Path("logout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(String payload) {
		
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(payload);
			
			Repository repo = new Repository();
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("login", json.get("username").toString());
			Object obj = repo.queryObject("User.findByEmailOrUserName", prop);
			
			if (obj!=null) {
		        
	        	User user = (User) obj;
	        	
	        	Authenticator.removeExistingToken(user, repo);
	        	
	        	JSONObject status = new JSONObject();
	        	status.put("status", "ok");
	        	
	        	return Response.ok(status.toJSONString()).build();
	        }
			
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		
		return Response.status(Status.UNAUTHORIZED).build();
		
	}

}
