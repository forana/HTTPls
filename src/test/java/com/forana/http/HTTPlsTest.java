package com.forana.http;

import org.junit.Test;
import static org.junit.Assert.*;

import com.forana.http.exceptions.HTTPRequestException;

public class HTTPlsTest {
    @Test
    public void testGet() throws HTTPRequestException {
        HTTPResponse response = HTTPls.get("http://httpbin.org/get")
                .send();
        assertTrue(response.isOk());
    }

    @Test
    public void testPost() throws HTTPRequestException {
        HTTPResponse response = HTTPls.post("http://httpbin.org/post")
                .send();
        assertTrue(response.isOk());
    }

    @Test
    public void testPut() throws HTTPRequestException {
        HTTPResponse response = HTTPls.put("http://httpbin.org/put")
                .send();
        assertTrue(response.isOk());
    }

    @Test
    public void testDelete() throws HTTPRequestException {
        HTTPResponse response = HTTPls.delete("http://httpbin.org/delete")
                .send();
        assertTrue(response.isOk());
    }

    @Test
    public void testArbitrary() throws HTTPRequestException {
        HTTPResponse response = HTTPls.request("PATCH", "http://httpbin.org/patch")
                .send();
        assertTrue(response.isOk());
    }
}
