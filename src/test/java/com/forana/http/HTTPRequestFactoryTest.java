package com.forana.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import com.forana.http.exceptions.HTTPException;
import com.forana.http.exceptions.HTTPRequestException;

public class HTTPRequestFactoryTest {
    @Test
    public void testNormal() throws HTTPException {
        new HTTPRequestFactory()
                .get("http://httpbin.org/get")
                .sendAndVerify();
    }

    @Test
    public void testBaseURL() throws HTTPRequestException {
        HTTPResponse response = new HTTPRequestFactory("http://httpbin.org")
                .get("/status/418")
                .send();
        assertEquals(response.getStatus(), 418);
        assertTrue(response.getStatusText().contains("TEAPOT"));
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
