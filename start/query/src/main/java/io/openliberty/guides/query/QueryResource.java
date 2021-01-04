package io.openliberty.guides.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.HashMap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.openliberty.guides.query.client.InventoryClient;

@ApplicationScoped
@Path("/query")
public class QueryResource {
    
    @Inject
    @RestClient
    private InventoryClient inventoryClient;

    @GET
    @Path("/systemLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Properties> systemLoad() {
    	
        List<String> systems = inventoryClient.getSystems();
        CountDownLatch remainingSystems = new CountDownLatch(systems.size());
        Holder systemLoads = new Holder();

        for (String system : systems) {
        	
            inventoryClient.getSystem(system)
            	.thenAcceptAsync( p -> {
            		if( Objects.nonNull( p ) ) {
            			systemLoads.updateValues( p );
            		}
            		remainingSystems.countDown();
            	})
            	.exceptionally( ex -> {
            		ex.printStackTrace();
            		remainingSystems.countDown();
            		return null;
            	});
            
        }
        
        //Wait for all remaining systems to be checked
        try {
        	remainingSystems.await( 30 , TimeUnit.SECONDS );
        }catch( InterruptedException ex ) {
        	ex.printStackTrace();
        }

        return systemLoads.getValues();
    }

    private class Holder {
        private Map<String, Properties> values;

        public Holder() {
            this.values = new HashMap<String, Properties>();
            init();
        }

        public Map<String, Properties> getValues() {
            return this.values;
        }

        public void updateValues(Properties p) {
            final BigDecimal load = (BigDecimal) p.get("systemLoad");

            this.values.computeIfPresent("lowest", (key, curr_val) -> {
                BigDecimal lowest = (BigDecimal) curr_val.get("systemLoad");
                return load.compareTo(lowest) < 0 ? p : curr_val;
            });
            this.values.computeIfPresent("highest", (key, curr_val) -> {
                BigDecimal highest = (BigDecimal) curr_val.get("systemLoad");
                return load.compareTo(highest) > 0 ? p : curr_val;
            });
        }

        private void init() {
            // Initialize highest and lowest values
            this.values.put("highest", new Properties());
            this.values.put("lowest", new Properties());
            this.values.get("highest").put("hostname", "temp_max");
            this.values.get("lowest").put("hostname", "temp_min");
            this.values.get("highest").put("systemLoad", new BigDecimal(Double.MIN_VALUE));
            this.values.get("lowest").put("systemLoad", new BigDecimal(Double.MAX_VALUE));
        }
    }
}
