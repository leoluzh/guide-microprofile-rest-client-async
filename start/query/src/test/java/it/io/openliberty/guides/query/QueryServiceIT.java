package it.io.openliberty.guides.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import io.openliberty.guides.query.QueryResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@SuppressWarnings("serial")
public class QueryServiceIT implements Serializable {

	@RestClient
	public static QueryResource queryResource;
	
	private static String testHost1 = 
		"{" + 
			"\"hostname\" : \"testHost1\" ," +
			"\"systemLoad\" : 1.23" +
		"}" ;

	private static String testHost2 = 
			"{" + 
				"\"hostname\" : \"testHost2\" ," +
				"\"systemLoad\" : 3.21" +
			"}" ;
	
	private static String testHost3 = 
			"{" + 
				"\"hostname\" : \"testHost3\" ," +
				"\"systemLoad\" : 2.13" +
			"}" ;
	
	@BeforeAll
	public static void setup() throws InterruptedException {
		
		AppContainerConfig.mockClient.when(
				HttpRequest
					.request()
						.withMethod("GET")
						.withPath("/inventory/systems/testHost1"))
					.respond(HttpResponse.response()
						.withStatusCode(200)
						.withBody(testHost1)
						.withHeader("Content-Type","application/json"));

		AppContainerConfig.mockClient.when(
				HttpRequest
					.request()
						.withMethod("GET")
						.withPath("/inventory/systems/testHost2"))
					.respond(HttpResponse.response()
						.withStatusCode(200)
						.withBody(testHost2)
						.withHeader("Content-Type","application/json"));

		AppContainerConfig.mockClient.when(
				HttpRequest
					.request()
						.withMethod("GET")
						.withPath("/inventory/systems/testHost3"))
					.respond(HttpResponse.response()
						.withStatusCode(200)
						.withBody(testHost3)
						.withHeader("Content-Type","application/json"));
		
		
	}
	
	@Test
	public void testLoads() {
		
		Map<String,Properties> response = queryResource.systemLoad();
		
		assertEquals( 
				"testHost2" , 
				response.get("highest").get("hostname") , 
				"Returned highest system load incorrect." );

		assertEquals( 
				"testHost1" , 
				response.get("lowest").get("hostname") , 
				"Returned lowest system load incorrect." );
		
		
	}
	
}
