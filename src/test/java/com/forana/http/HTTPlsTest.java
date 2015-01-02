package com.forana.http;

import org.junit.Test;

import com.forana.http.exceptions.HTTPException;

public class HTTPlsTest {
    @Test
    public void testGet() throws HTTPException {
        HTTPls.get("http://httpbin.org/get")
                .sendAndVerify();
    }

    @Test
    public void testPost() throws HTTPException {
        HTTPls.post("http://httpbin.org/post")
                .sendAndVerify();
    }

    @Test
    public void testPut() throws HTTPException {
        HTTPls.put("http://httpbin.org/put")
                .sendAndVerify();
    }

    @Test
    public void testDelete() throws HTTPException {
        HTTPls.delete("http://httpbin.org/delete")
                .sendAndVerify();
    }

    @Test
    public void testArbitrary() throws HTTPException {
        HTTPls.request("PATCH", "http://httpbin.org/patch")
                .sendAndVerify();
    }
}
