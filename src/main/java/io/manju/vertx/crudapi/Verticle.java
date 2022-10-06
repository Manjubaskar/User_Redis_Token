package io.manju.vertx.crudapi;


import java.util.HashSet;
import java.util.Set;


import io.manju.vertx.crudapi.entity.Signup;
import io.manju.vertx.crudapi.service.SignupService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class Verticle extends AbstractVerticle {

	  @Override
	  public void start(Future<Void> fut) {
	      Router router = Router.router(vertx); // <1>
	      // CORS support
	      Set<String> allowHeaders = new HashSet<>();
	      allowHeaders.add("x-requested-with");
	      allowHeaders.add("Access-Control-Allow-Origin");
	      allowHeaders.add("origin");
	      allowHeaders.add("Content-Type");
	      allowHeaders.add("accept");
	      allowHeaders.add("Authorization");
	      Set<HttpMethod> allowMethods = new HashSet<>();
	      allowMethods.add(HttpMethod.GET);
	      allowMethods.add(HttpMethod.POST);
	      allowMethods.add(HttpMethod.DELETE);
	      allowMethods.add(HttpMethod.PUT);

	      router.route().handler(CorsHandler.create("*") // <2>
	              .allowedHeaders(allowHeaders)
	              .allowedMethods(allowMethods));
	      router.route().handler(BodyHandler.create()); // <3>

	      // routes


	      router.post("/signup").handler(this::signup);
	      router.put("/signup").handler(this::passwordUpdate);

	      router.post("/login").handler(this::login);
	      

	      vertx.createHttpServer() // <4>
	              .requestHandler(router::accept)
	              .listen(8080, "0.0.0.0", result -> {
	                  if (result.succeeded())
	                      fut.complete();
	                  else
	                      fut.fail(result.cause());
	              });
	  }
	  
	  

	  SignupService signupService = new SignupService();


	  private void signup(RoutingContext context) {
		  	signupService.signup(context,Json.decodeValue(context.getBodyAsString(), Signup.class), ar -> {
		          if (ar.succeeded()) {
		              sendSuccess(context.response());
		          } else {
		              sendError(ar.cause().getMessage(), context.response());
		          }
		      });
		  }
	  

	  
	  private void login(RoutingContext context) {
		  	signupService.login(context,Json.decodeValue(context.getBodyAsString(), Signup.class), ar -> {
		          if (ar.succeeded()) {
		              sendSuccess(context.response());
		          } else {
		              sendError(ar.cause().getMessage(), context.response());
		          }
		      });
		  }
	  
	  private void passwordUpdate(RoutingContext context) {
		  signupService.passwordUpdate(context,Json.decodeValue(context.getBodyAsString(), Signup.class), ar -> {
	            if (ar.succeeded()) {
	                sendSuccess(context.response());
	                
	            } else {
	                sendError(ar.cause().getMessage(), context.response());
	            }
	        });
	    }
	    
	  private void sendError(String errorMessage, HttpServerResponse response) {
	      JsonObject jo = new JsonObject();
	      jo.put("errorMessage", errorMessage);

	      response
	              .setStatusCode(400)
	              .putHeader("content-type", "application/json; charset=utf-8")
	              .end(Json.encodePrettily(jo));
	  }

	  private void sendSuccess(HttpServerResponse response) {
	      response
	              .setStatusCode(200)
	              .putHeader("content-type", "application/json; charset=utf-8")
	              .end();
	  }
	}
