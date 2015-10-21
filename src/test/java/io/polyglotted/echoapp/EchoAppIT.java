package io.polyglotted.echoapp;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class EchoAppIT {

    @Test
    public void testEcho() throws Exception {
        String host = System.getProperty("app.host");
        int port = Integer.parseInt(System.getProperty("app.port", "-1"));

        assertNotNull(host);
        assertTrue(port > 0);
    }
}
