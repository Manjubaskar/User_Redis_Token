package io.manju.vertx.crudapi.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import io.manju.vertx.crudapi.entity.Signup;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 *This class handles the signup data transactions
 */
public class SignupDao {
    private static SignupDao instance;
    protected EntityManager entityManager;

    public static SignupDao getInstance()
    	{
        if (instance == null){
            instance = new SignupDao();
        }
        return instance;
    	}

    private SignupDao()
    	{
        entityManager = getEntityManager();
    	}

    private EntityManager getEntityManager()
    	{
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("crudHibernatePU");
        if (entityManager == null) {
            entityManager = factory.createEntityManager();
        }
        return entityManager;
    	}
	 
    /**
	   *  This method should return user info for given user name
	   * @param name
	   * @return Signup object
	   */
	 public Signup getByUsername(String name) {
		 Signup user = null;
	    	try {
	    		List<Signup> users = entityManager.createQuery(
			    		"FROM Signup WHERE user_name = :name", Signup.class)
		          .setParameter("name", name)
		          .getResultList();
	    		user=users.get(0);
	    	}
	    	catch(Exception ex) {
	    	ex.printStackTrace();
	    	}
	    	return user;
	    	}
	 
	 /**
	   *  This method check the given user name same as signup table exsiting user name 
	   *  Invalid Username throw Login failed 
	   * @param name
	   * @return Signup object
	   */
     public Signup getByName(RoutingContext context,String name)
	  {
	      try{Object result = entityManager.createQuery( "SELECT s FROM Signup s WHERE s.name LIKE :user_name")
	    	        .setParameter("user_name", name)
	    	        .getSingleResult();

	      if (result != null) {
	          return (Signup) result;
	      }
	      }
	      catch(NoResultException nre){

	    	  sendError("Login failed", context.response(),400);
	      }
	     return null;
	  }


	  /**
	   *  This method handles the post call transaction for sigup data
	   *  @param Signup
	   */
      public void persist(Signup signup)
    	{
	        try {
	            entityManager.getTransaction().begin();
	            entityManager.persist(signup);
	            entityManager.getTransaction().commit();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            entityManager.getTransaction().rollback();
	        }
	    }
      
      /**
	   *  This method should return user info for given user email
	   * @param email
	   * @return Signup object
	   */
      public Signup getByEmail(String email) {
 		 Signup user = null;
 	    	try {
 	    		List<Signup> users = entityManager.createQuery(
 			    		"FROM Signup WHERE email = :email", Signup.class)
 		          .setParameter("email", email)
 		          .getResultList();
 	    		user=users.get(0);
 	    	}
 	    	catch(Exception ex) {
 	    	ex.printStackTrace();
 	    	}
 	    	return user;
 	    	}
      
      public void forgotPassword(RoutingContext context,String email,String password)
  		{   
    	  try {
    		  
	            entityManager.getTransaction().begin();
	            Query signup = entityManager.createQuery("UPDATE Signup set password='"+password+"'  WHERE email='"+email+"'");
	            signup.executeUpdate();
        		entityManager.getTransaction().commit();
        		sendSuccess(" Password Updated", context.response(),200);   		 
    	  }  catch (Exception ex) {
		            ex.printStackTrace();
		            entityManager.getTransaction().rollback();
		            
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
      private void sendSuccess(String successMessage,HttpServerResponse response,int code) {
    	  JsonObject jo = new JsonObject();
          jo.put("successMessage", successMessage);
    	  response
	              .setStatusCode(200)
	              .setStatusCode(code)
	              .putHeader("content-type", "application/json; charset=utf-8")
	              .end(Json.encodePrettily(jo));
	  }
}

