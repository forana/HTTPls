package com.forana.http;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.*;

import com.forana.http.exceptions.HTTPRequestException;
import com.forana.http.exceptions.HTTPResponseException;

public class HTTPRequestFactoryTest {
    @Test
    public void testBaseURL() throws HTTPRequestException {
        HTTPRequestFactory factory = new HTTPRequestFactory("http://httpbin.org");
        HTTPResponse response = factory.get("/status/418").send();
        assertEquals(response.getStatus(), 418);
    }
    
    @Test
    public void testDefaultHeaders() throws HTTPRequestException, HTTPResponseException {
        HTTPRequestFactory factory = new HTTPRequestFactory()
            .addDefaultHeader("X-Test", "test");
        HTTPResponse response = factory.get("http://httpbin.org/headers").send();
        assertTrue(response.is20X());
        ObjectNode obj = (ObjectNode)response.getJSON();
        assertEquals("test", obj.get("headers").get("X-Test").asText());
    }
}
