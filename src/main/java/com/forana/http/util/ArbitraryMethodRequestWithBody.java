package com.forana.http.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * HttpRequestBase subclass that allows an arbitrary request method and a body.
 * 
 * @author forana
 */
public class ArbitraryMethodRequestWithBody extends HttpEntityEnclosingRequestBase {

    protected String method;

    public ArbitraryMethodRequestWithBody(String method, HttpEntity entity) {
        super();
        this.method = method;
        setEntity(entity);
    }

    @Override
    public String getMethod() {
        return method;
    }

}
