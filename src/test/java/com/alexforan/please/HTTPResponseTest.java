package com.alexforan.please;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.alexforan.please.HTTPResponse;
import com.alexforan.please.Please;
import com.alexforan.please.exceptions.HTTPException;

public class HTTPResponseTest {
    @Test
    public void testStatus() throws HTTPException {
        HTTPResponse response = Please.get("http://httpbin.org/status/418").send();
        assertEquals(response.getStatus(), 418);
        assertTrue(response.getStatusText().contains("TEAPOT"));
    }

    @Test
    public void testIsOk() throws HTTPException {
        HTTPResponse response = Please.get("http://httpbin.org/status/418").send();
        assertFalse(response.isOk());
        for (int status : new int[] { 200, 201, 202, 204 }) {
            response = Please.get("http://httpbin.org/status/" + status).send();
            assertEquals(status, response.getStatus());
            assertTrue(response.isOk());
        }
    }

    @Test
    public void testGetHeader() throws HTTPException {
        HTTPResponse response = Please.get("http://httpbin.org/get").sendAndVerify();
        assertEquals("*", response.getHeader("Access-Control-Allow-Origin"));
        assertNull(response.getHeader("definitely-don't-have-this"));
    }
    
    @Test
    public void testHeaders() throws HTTPException {
        Map<String, String> headers = Please.get("http://httpbin.org/get").sendAndVerify()
                .getHeaders();
        assertTrue(headers.containsKey("Access-Control-Allow-Origin"));
        assertEquals("*", headers.get("Access-Control-Allow-Origin"));
        assertFalse(headers.containsKey("definitely-don't-have-this"));
    }

    @Test
    public void testGetContentType() throws HTTPException {
        String type = Please.get("http://httpbin.org/get").sendAndVerify().getContentType();
        assertEquals("application/json", type);
    }

    // this is where a testGetJSON() would be if it wasn't used by a number of other tests already

    @Test
    public void testGetBytes() throws HTTPException {
        HTTPResponse response = Please.get("http://httpbin.org/stream-bytes/1234").sendAndVerify();
        assertTrue(response.hasBody());
        assertEquals(1234, response.getBytes().length);
    }

    @Test
    public void testDump() throws HTTPException {
        // this really just tests if a NullPointerException occurs
        Please.get("http://httpbin.org/get").sendAndVerify().dump();
    }

    @Test
    public void testFinalizeBeforeRead() throws Throwable {
        Please.get("http://httpbin.org/stream-bytes/42").sendAndVerify().finalize();
    }

    @Test
    public void testFinalizeAfterRead() throws Throwable {
        HTTPResponse response = Please.get("http://httpbin.org/stream-bytes/42").sendAndVerify();
        byte[] bytes = response.getBytes();
        assertEquals(42, bytes.length);
        response.finalize();
    }
}
