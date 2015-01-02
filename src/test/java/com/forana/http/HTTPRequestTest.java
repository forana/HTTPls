package com.forana.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
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
                .sendAndVerify()
                .getJSON()
                .get("args");
        
        assertEquals("listen", args.get("hey").asText());
        assertEquals(3, args.get("pi").asInt());
    }

    @Test
    public void testParametersFromStringOnly() throws HTTPException {
        JsonNode args = HTTPls.get("http://httpbin.org/get?a=b&c=d")
                .sendAndVerify()
                .getJSON()
                .get("args");

        assertEquals("b", args.get("a").asText());
        assertEquals("d", args.get("c").asText());
    }

    @Test
    public void testParametersMixed() throws HTTPException {
        JsonNode args = HTTPls.get("http://httpbin.org/get?a=b")
                .parameter("c", "d")
                .sendAndVerify()
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
                .sendAndVerify();
    }

    @Test
    public void testHeader() throws HTTPException {
        JsonNode headers = HTTPls.get("http://httpbin.org/headers")
                .header("X-Thing", "thing")
                .sendAndVerify()
                .getJSON()
                .get("headers");
        assertEquals("thing", headers.get("X-Thing").asText());
    }

    @Test
    public void testJSONBody() throws HTTPException, IOException {
        ObjectNode sentBody = new ObjectNode(JsonNodeFactory.instance);
        sentBody.put("6x9", "42");
        String receivedBodyText = HTTPls.post("http://httpbin.org/post")
                .header("Accept", "application/json")
                .body(sentBody)
                .sendAndVerify()
                .getJSON()
                .get("data")
                .asText();

        JsonNode receivedBody = new ObjectMapper()
                .readTree(receivedBodyText);

        assertEquals(sentBody.get("6x9").asText(), receivedBody.get("6x9").asText());
    }
}
