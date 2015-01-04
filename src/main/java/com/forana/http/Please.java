package com.forana.http;

/**
 * A simple facade for making requests without instantiating a factory.
 * 
 * @see com.forana.http.HTTPRequestFactory
 * 
 * @author forana
 */
public class Please {
    private static final HTTPRequestFactory factory = new HTTPRequestFactory();

    private Please() {
    }

    /**
     * Start building a request to a URL with an arbitrary HTTP method, such as PATCH or HEAD.
     * 
     * Note that unsupported methods (e.g. BREW) may cause an exception in subsequent calls to
     * <code>HTTPRequest.send()</code>.
     */
    public static HTTPRequest request(String method, String url) {
        return factory.request(method, url);
    }

    /**
     * Start building a GET request to a URL.
     */
    public static HTTPRequest get(String url) {
        return factory.get(url);
    }

    /**
     * Start building a POST request to a URL.
     */
    public static HTTPRequest post(String url) {
        return factory.post(url);
    }

    /**
     * Start building a PUT request to a URL.
     */
    public static HTTPRequest put(String url) {
        return factory.put(url);
    }

    /**
     * Start building a DELETE request to a URL.
     */
    public static HTTPRequest delete(String url) {
        return factory.delete(url);
    }
}
