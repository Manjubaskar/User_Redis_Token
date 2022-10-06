package io.manju.vertx.crudapi.service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.manju.vertx.crudapi.entity.Signup;
import io.manju.vertx.crudapi.repository.SignupDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SignupService {
	private SignupDao signupDao = SignupDao.getInstance();

	/**
	 *  This method used to save the signup data
	 * @param name
	 * @Interface handler
	 */
	public void signup(RoutingContext context,Signup newSignup, Handler<AsyncResult<Signup>> handler) {
		     Future<Signup> future = Future.future();
		     future.setHandler(handler);
		  try {
				if(newSignup.getName().isEmpty() || newSignup.getPassword().isEmpty()||newSignup.getEmail().isEmpty()) {
		     		 System.out.print("Please filled the manditatory Fields");
		             sendError("Please filled the manditatory Fields", context.response(),400);
		     	}
				else
				{
				  Signup user = signupDao.getByUsername(newSignup.getName());
				  if(user!=null) {
					  sendError("User exist", context.response(),400);}
				  else 
				  {
						  
				    	 String regex = "^(?=.*[0-9])"
			                     + "(?=.*[a-z])(?=.*[A-Z])"
			                     + "(?=.*[@#$%^&+=])"
			                     + "(?=\\S+$).{8,20}$";
				    	 
				    	 Pattern p = Pattern.compile(regex);
				    	 Matcher m = p.matcher(newSignup.getPassword());
				    	  if(m.matches()){	
				    		  
				    		  String regex1 = "^(.+)@(.+)$";  
				    		  Pattern pattern = Pattern.compile(regex1);
						    	 Matcher matcher = pattern.matcher(newSignup.getEmail());
						    	 if(matcher.matches()){ 
				    		  System.out.print("success");
				    		 signupDao.persist(newSignup);	}
						    	 else {
						    		 sendError("Please give a valid Email", context.response(),400);}
						    	 
						  }else{
							  sendError("Password must have length 8 characters,one Uppercase,"
							  		+ "one special character and one digit", context.response(),400);}
						  }
			  future.complete();
			 	 } }catch (Throwable ex) {
				            future.fail(ex);
				 }
	 }
	
	
	/**
	 *  This method used to check login data
	 * @param name
	 * @Interface handler
	 */
	public void login(RoutingContext context,Signup newSignup, Handler<AsyncResult<Signup>> handler) {
	     Future<Signup> future = Future.future();
	     future.setHandler(handler);
	     try {
	    	 
	     	/**
	     	 * this conditon is used check the user name and password is empty or wrong
	     	 */
	     	if(newSignup.getName().isEmpty() || newSignup.getPassword().isEmpty()) {
	     		 System.out.print("invalid");
	             sendError("Invalid username and password", context.response(),400);

	     	}
	     	else {
	     		
	     		
	     		 if(newSignup.getName().equals(signupDao.getByName(context,newSignup.getName()).getName())&&
	     				newSignup.getPassword().equals(signupDao.getByName(context,newSignup.getName()).getPassword()))
	             {	
	     		// sendSuccess("Login Success", context.response(),200);
			     		JedisPool jedisPool = new JedisPool("localhost", 6379);
			  		  // Get the pool and use the database
			  		  try (Jedis jedis = jedisPool.getResource()) {
			  			  
			  			  UUID uuid=UUID.randomUUID(); //Generates random UUID  
			  			  
			  			  String uuidAsString = uuid.toString();
			  			  System.out.println(uuidAsString);  
		
			  		  jedis.set("extoken", uuidAsString);
			  		  String value = jedis.get("extoken");
			  		  sendToken("Login Success",value, context.response(),200);
			  		  System.out.println( value );
			  		  }
		
			  		  // close the connection pool
			  		  jedisPool.close();	
			  		
			  	
	             }
	     		 else {
	     			sendError("Login failed", context.response(),400);
	     		 }

	     		  future.complete();}
		   } catch (Throwable ex) {
		            future.fail(ex);
		        }
	 		}

    public void passwordUpdate(RoutingContext context,Signup signup, Handler<AsyncResult<Signup>> handler) {
        Future<Signup> future = Future.future();
        future.setHandler(handler);

        try {
        	 Signup user = signupDao.getByEmail(signup.getEmail());
        	 
			  if(user!=null) {
				  String regex = "^(?=.*[0-9])"
		                     + "(?=.*[a-z])(?=.*[A-Z])"
		                     + "(?=.*[@#$%^&+=])"
		                     + "(?=\\S+$).{8,20}$";
			    	 
			    	 Pattern p = Pattern.compile(regex);
			    	 Matcher m = p.matcher(signup.getPassword());
			    	  if(m.matches()){	    		 
			    		  System.out.print("success");
				  
			signupDao.forgotPassword(context,signup.getEmail(), signup.getPassword());
            future.complete();
            sendSuccess("Password Updated", context.response(),200);
			    	  }else {
			    		  sendError("Password must have length 8 characters,one Uppercase,one special character and one digit", context.response(),400);} 
			    	  }
			 
			  else {
				  sendError("Email not Exist", context.response(),400);
			  }
        } catch (Throwable ex) {
        	 sendSuccess("fail ", context.response(),400);
            future.fail(ex);
      
        }
    }

     private static void sendError(String errorMessage, HttpServerResponse response,int code) {
         JsonObject jo = new JsonObject();
         jo.put("errorMessage", errorMessage);

         response
                 .setStatusCode(400)
                 .setStatusCode(code)
                 .putHeader("content-type", "application/json; charset=utf-8")
                 .end(Json.encodePrettily(jo));
     }

     private void sendSuccess(String successMessage, HttpServerResponse response,int code) {
         JsonObject jo = new JsonObject();
         jo.put("successMessage", successMessage);

         response
                 .setStatusCode(200)
                 .setStatusCode(code)
                 .putHeader("content-type", "application/json; charset=utf-8")
                 .end(Json.encodePrettily(jo));
     	}
     
     private void sendToken(String successMessage,String token, HttpServerResponse response,int code) {
         JsonObject jo = new JsonObject();
         jo.put("token", token);
         jo.put("successMessage", successMessage);
         response
                 .setStatusCode(200)
                 .setStatusCode(code)
                 .putHeader("content-type", "application/json; charset=utf-8")
                 .putHeader("Authorization", token)
                 .end(Json.encodePrettily(jo));
     	}
     
		}