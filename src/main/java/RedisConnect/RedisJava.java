package RedisConnect;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
public class RedisJava  {
	 public static void main(String[] args) {
	 
//		  JedisPool jedisPool = new JedisPool("localhost", 6379);
//		  // Get the pool and use the database
//		  try (Jedis jedis = jedisPool.getResource()) {
//			  
//		  jedis.set("mykey", "Hello from Jedis");
//		  String value = jedis.get("mykey");
//		  System.out.println( value );
//		  }
//
//		  // close the connection pool
//		  jedisPool.close();
		 
		 
		  JedisPool jedisPool = new JedisPool("localhost", 6379);
		  // Get the pool and use the database
		  try (Jedis jedis = jedisPool.getResource()) {
			  
			  UUID uuid=UUID.randomUUID(); 
			  //Generates random UUID  
			  
			  String uuidAsString = uuid.toString();
			  System.out.println(uuidAsString);  

		  jedis.set("extoken", uuidAsString);
		  String value = jedis.get("extoken");
		  System.out.println( value );
		  }

		  // close the connection pool
		  jedisPool.close();		 
		 
		    
		  
	 }
	 
}