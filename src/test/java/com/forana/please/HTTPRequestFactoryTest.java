package com.forana.please;

import static org.junit.Assert.assertEquals;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import com.forana.please.exceptions.HTTPException;

public class HTTPRequestFactoryTest {
    @Test
    public void testNormal() throws HTTPException {
        new HTTPRequestFactory()
                .get("http://httpbin.org/get")
                .sendAndVerify();
    }

    @Test
    public void testBaseURL() throws HTTPException {
        new HTTPRequestFactory("http://httpbin.org")
                .get("/get")
                .sendAndVerify();
    }
    
    @Test
    public void testDefaultHeaders() throws HTTPException {
        JsonNode headers = new HTTPRequestFactory()
                .addDefaultHeader("X-Test", "test")
                .get("http://httpbin.org/headers")
                .header("X-Test-Again", "still")
                .sendAndVerify()
                .getJSON()
                .get("headers");
        assertEquals("test", headers.get("X-Test").asText());
        assertEquals("still", headers.get("X-Test-Again").asText());
    }
}
