package com.forana.please;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.forana.please.exceptions.HTTPResponseException;

/**
 * Object symbolizing an HTTP response.
 * 
 * @author forana
 */
public class HTTPResponse {
    private final CloseableHttpResponse response;
    private final CloseableHttpClient client;

    /**
     * Should not be called directly.
     * 
     * @param response The response that this wraps.
     */
    protected HTTPResponse(CloseableHttpResponse response, CloseableHttpClient client) {
        this.response = response;
        this.client = client;
    }

    /**
     * Closes the response. Should be called if there is a body whose contents are being streamed.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        response.close();
        client.close();
    }

    /**
     * Retrieve the status code of the response.
     */
    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Retrieve the textual status (reason) for the response.
     */
    public String getStatusText() {
        return response.getStatusLine().getReasonPhrase();
    }

    /**
     * Convenience helper that checks if the response is within the 200 (acceptable) range.
     */
    public boolean isOk() {
        return getStatus() >= 200 && getStatus() <= 200;
    }

    /**
     * Retrieves the content-type of the response body (mime), or null if that header isn't set.
     */
    public String getContentType() {
        Header header = response.getEntity().getContentType();
        return header == null ? null : header.getValue();
    }

    /**
     * Convenience helper that checks if the response has a body.
     */
    public boolean hasBody() {
        return response.getEntity() != null;
    }

    /**
     * Retrieve the body as an @{link java.io.InputStream}. The user is then responsible for closing
     * the stream when finished.
     * 
     * @throws HTTPResponseException
     */
    public InputStream getBody() throws HTTPResponseException {
        try {
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new HTTPResponseException(e);
        }
    }

    /**
     * Retrieve the JSON body as a @{link org.codehaus.jackson.JsonNode}.
     * 
     * @throws HTTPResponseException If there's an error reading or parsing the body.
     */
    public JsonNode getJSON() throws HTTPResponseException {
        try {
            InputStream stream = response.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(mapper.getJsonFactory()
                    .createJsonParser(stream));
        } catch (IOException e) {
            throw new HTTPResponseException(e);
        }
    }

    /**
     * Retrieve the value of a header in the response if it's set, or <code>null</code> if it's not.
     * 
     * If there's more than one header with that name, the first will be returned.
     */
    public String getHeader(String name) {
        Header header = response.getFirstHeader(name);
        return header == null ? null : header.getValue();
    }

    /**
     * Retrieve all headers in the response.
     */
    public Collection<Header> getHeaders() {
        List<Header> headers = new LinkedList<>();
        for (Header header : response.getAllHeaders()) {
            headers.add(header);
        }
        return headers;
    }
}
