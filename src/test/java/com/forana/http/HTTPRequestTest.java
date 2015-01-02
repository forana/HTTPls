package com.forana.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import com.forana.http.exceptions.HTTPException;
import com.forana.http.exceptions.HTTPRequestException;

public class HTTPRequestTest {
    @Test
    public void testCertificateVerification() throws HTTPException {
        // this should throw an exception
        try {
            HTTPls.get("https://httpbin.herokuapp.com").send();
            fail("Expected a certificate failure");
        } catch (HTTPRequestException e) {
        }

        // this shouldn't throw an exception
        HTTPls.get("https://httpbin.herokuapp.com")
                .setVerifyCertificates(false)
                .send();
    }
    
    @Test
    public void testParametersFromBuilderOnly() throws HTTPException {
        JsonNode args = HTTPls.get("http://httpbin.org/get")
                .parameter("hey", "listen")
                .parameter("pi", 3)
                .send()
                .verifyOk()
                .getJSON()
                .get("args");
        
        assertEquals("listen", args.get("hey").asText());
        assertEquals(3, args.get("pi").asInt());
    }

    @Test
    public void testParametersFromStringOnly() throws HTTPException {
        JsonNode args = HTTPls.get("http://httpbin.org/get?a=b&c=d")
                .send()
                .verifyOk()
                .getJSON()
                .get("args");

        assertEquals("b", args.get("a").asText());
        assertEquals("d", args.get("c").asText());
    }

    @Test
    public void testParametersMixed() throws HTTPException {
        JsonNode args = HTTPls.get("http://httpbin.org/get?a=b")
                .parameter("c", "d")
                .send()
                .verifyOk()
                .getJSON()
                .get("args");

        assertEquals("b", args.get("a").asText());
        assertEquals("d", args.get("c").asText());
    }

    @Test
    public void testSetEncoding() throws HTTPException {
        // TODO make this test meaningful
        HTTPls.get("http://httpbin.org/get?a=4&42=")
                .setEncoding("ASCII")
                .send()
                .verifyOk();
    }
}
