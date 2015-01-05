package com.forana.please;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forana.please.exceptions.HTTPException;
import com.forana.please.exceptions.HTTPRequestException;
import com.forana.please.exceptions.HTTPResponseException;

public class HTTPRequestTest {
    @Test
    public void testSendAndVerify() throws HTTPException {
        Please.get("https://httpbin.org/status/200").sendAndVerify();

        try {
            Please.get("https://httpbin.org/status/369").sendAndVerify();
            fail("Expected an exception");
        } catch (HTTPResponseException e) {
        }
    }

    @Test
    public void testCertificateVerification() throws HTTPException {
        // this should throw an exception
        try {
            Please.get("https://httpbin.herokuapp.com").send();
            fail("Expected a certificate failure");
        } catch (HTTPRequestException e) {
        }

        // this shouldn't throw an exception
        Please.get("https://httpbin.herokuapp.com")
                .setVerifyCertificates(false)
                .send();
    }
    
    @Test
    public void testURISyntaxCatch() throws HTTPException {
        try {
            Please.get("hey hello").send();
            fail("Expected a syntax exception");
        } catch (HTTPRequestException e) {
            assertTrue(e.getCause() instanceof URISyntaxException);
        }
    }

    @Test
    public void testParametersFromBuilderOnly() throws HTTPException {
        JsonNode args = Please.get("http://httpbin.org/get")
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
        JsonNode args = Please.get("http://httpbin.org/get?a=b&c=d")
                .sendAndVerify()
                .getJSON()
                .get("args");

        assertEquals("b", args.get("a").asText());
        assertEquals("d", args.get("c").asText());
    }

    @Test
    public void testParametersMixed() throws HTTPException {
        JsonNode args = Please.get("http://httpbin.org/get?a=b")
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
        Please.get("http://httpbin.org/get?a=4&42=")
                .setEncoding("ASCII")
                .sendAndVerify();
    }

    @Test
    public void testHeader() throws HTTPException {
        JsonNode headers = Please.get("http://httpbin.org/headers")
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
        String receivedBodyText = Please.post("http://httpbin.org/post")
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

    @Test
    public void testStringBody() throws HTTPException {
        String sentBody = "foxtrot uniform";
        String receivedBody = Please.post("http://httpbin.org/post")
                .body(sentBody)
                .sendAndVerify()
                .getJSON()
                .get("data")
                .asText();

        assertEquals(sentBody, receivedBody);
    }
    
    @Test
    public void testFormBody() throws HTTPException {
        JsonNode form = Please.post("http://httpbin.org/post")
                .body(new Form()
                        .addAll(new HashMap<String, String>() {
                            private static final long serialVersionUID = 1L;
                            {
                                put("time", "money");
                                put("daylight", "burning");
                        }})
                        .addAll("k", Arrays.asList("a", "b", "c"))
                        .add("6x9base7", 42))
                .sendAndVerify()
                .getJSON()
                .get("form");

        assertEquals("money", form.get("time").asText());
        assertEquals("burning", form.get("daylight").asText());
        assertEquals(42, form.get("6x9base7").asInt());
        assertEquals(3, form.get("k").size());
    }

    @Test
    public void testMultipart() throws HTTPException, IOException {
        // use a temp file
        File tempFile = File.createTempFile("test", "txt");
        String fileContents = "There and Back Again";
        FileWriter writer = new FileWriter(tempFile);
        writer.append(fileContents);
        writer.close();

        // byte[]
        String byteData = "The Talos Mistake";

        // stream tests commented out due to https://github.com/Runscope/httpbin/issues/102
        // the requests _look_ solid, but I have no way to test this at the moment
        /*
         * String streamText = "Nerevar, Moon and Star";
         * InputStream stream = new ByteArrayInputStream(streamText.getBytes());
         */

        JsonNode body = Please.post("http://httpbin.org/post")
                .body(new MultipartFormData()
                        .data("file", tempFile)
                        .data("bytes", byteData.getBytes())
                        // .data("stream", stream)
                        .field("x", "y")
                        .field("m1a", 1))
                .sendAndVerify()
                .getJSON();

        JsonNode form = body.get("form");
        JsonNode files = body.get("files");

        assertEquals(fileContents, files.get("file").asText());
        assertEquals(byteData, files.get("bytes").asText());
        // assertEquals(streamText, files.get("stream").asText());
        assertEquals("y", form.get("x").asText());
        assertEquals(1, form.get("m1a").asInt());
    }

}
