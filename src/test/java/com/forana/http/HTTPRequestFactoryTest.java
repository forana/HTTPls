package com.forana.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import com.forana.http.exceptions.HTTPException;
import com.forana.http.exceptions.HTTPRequestException;

public class HTTPRequestFactoryTest {
    @Test
    public void testNormal() throws HTTPException {
        HTTPResponse response = new HTTPRequestFactory().get("http://httpbin.org/get").send();
        assertTrue(response.isOk());
    }

    @Test
    public void testBaseURL() throws HTTPRequestException {
        HTTPRequestFactory factory = new HTTPRequestFactory("http://httpbin.org");
        HTTPResponse response = factory.get("/status/418").send();
        assertEquals(response.getStatus(), 418);
        assertTrue(response.getStatusText().contains("TEAPOT"));
    }
    
    @Test
    public void testDefaultHeaders() throws HTTPException {
        HTTPRequestFactory factory = new HTTPRequestFactory()
            .addDefaultHeader("X-Test", "test");
        HTTPResponse response = factory.get("http://httpbin.org/headers").send();
        assertTrue(response.isOk());
        ObjectNode obj = (ObjectNode)response.getJSON();
        assertEquals("test", obj.get("headers").get("X-Test").asText());
    }
}
