package com.forana.http.util;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * HttpRequestBase subclass that allows an arbitrary HTTP method.
 * 
 * @author forana
 */
public class ArbitraryMethodRequest extends HttpRequestBase {
    
    protected String method;
    
    public ArbitraryMethodRequest(String method) {
        super();
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }

}
