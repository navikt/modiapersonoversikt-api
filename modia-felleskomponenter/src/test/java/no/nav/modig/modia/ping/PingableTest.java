package no.nav.modig.modia.ping;

import org.junit.Test;

import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static org.junit.Assert.assertEquals;

public class PingableTest {
    
    @Test
    public void testPing() throws Exception {
        PingableComponent pingable = new PingableComponent();

        PingResult result = pingable.ping();

        assertEquals(SERVICE_OK, result.getServiceStatus());
        assertEquals(1000, result.getElapsedTime());
    }
    
    
    private class PingableComponent implements Pingable {
        @Override
        public PingResult ping() {
            return new PingResult(SERVICE_OK, 1000);
        }

        @Override
        public String name() {
            return "exampleService";
        }

        @Override
        public String method() {
            return "ping";
        }

        @Override
        public String endpoint() {
            return "endpoint";
        }
    }
}
