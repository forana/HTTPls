package com.forana.please;

import org.junit.Test;

import com.forana.please.Please;
import com.forana.please.exceptions.HTTPException;

public class PleaseTest {
    @Test
    public void testGet() throws HTTPException {
        Please.get("http://httpbin.org/get")
                .sendAndVerify();
    }

    @Test
    public void testPost() throws HTTPException {
        Please.post("http://httpbin.org/post")
                .sendAndVerify();
    }

    @Test
    public void testPut() throws HTTPException {
        Please.put("http://httpbin.org/put")
                .sendAndVerify();
    }

    @Test
    public void testDelete() throws HTTPException {
        Please.delete("http://httpbin.org/delete")
                .sendAndVerify();
    }

    @Test
    public void testArbitrary() throws HTTPException {
        Please.request("PATCH", "http://httpbin.org/patch")
                .sendAndVerify();
    }
}
