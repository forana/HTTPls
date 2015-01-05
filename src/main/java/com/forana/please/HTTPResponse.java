package com.forana.please;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    @Override
    protected final void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
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
     * @throws HTTPResponseException If there's a general I/O error.
     */
    public InputStream getBody() throws HTTPResponseException {
        try {
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new HTTPResponseException(e);
        }
    }

    /**
     * Retrieve the body as an <code>byte[]</code>. The stream will be closed automatically.
     * 
     * This method will not be spectacularly performant - if you're willing to add a third-party
     * dependency, look at commons-io's <code>IOUtils.toByteArray(InputStream)</code>.
     * 
     * @throws HTTPResponseException If there's a general I/O error.
     */
    public byte[] getBytes() throws HTTPResponseException {
        try {
            InputStream stream = getBody();
            List<byte[]> byteArrays = new LinkedList<>();
            final int CHUNK_SIZE = 1024;
            int lastRead = -1;
            int totalSize = 0;
            do {
                byte[] buffer = new byte[CHUNK_SIZE];
                lastRead = stream.read(buffer);
                if (lastRead != -1) {
                    totalSize += lastRead;
                    byteArrays.add(Arrays.copyOf(buffer, lastRead));
                }
            } while (lastRead > 0);
            stream.close();

            byte[] result = new byte[totalSize];
            int i = 0;
            for (byte[] array : byteArrays) {
                for (byte b : array) {
                    result[i] = b;
                    i++;
                }
            }
            return result;
        } catch (IOException e) {
            throw new HTTPResponseException(e);
        }
    }

    /**
     * Convenience method to return the body as a string.
     * 
     * @throws HTTPResponseException
     */
    public String getBodyText() throws HTTPResponseException {
        byte[] bytes = getBytes();
        return new String(bytes);
    }

    /**
     * Retrieve the JSON body as a @{link org.codehaus.jackson.JsonNode}.
     * 
     * @throws HTTPResponseException If there's an error reading or parsing the body.
     */
    public JsonNode getJSON() throws HTTPResponseException {
        try {
            InputStream stream = getBody();
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
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new TreeMap<>(); // so that entrySet() is ordered
        for (Header header : response.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }
    
    /**
     * Print the status, reason, and headers to System.out. Useful for debugging.
     * 
     * @return this
     */
    public HTTPResponse dump() {
        return dump(System.out);
    }

    /**
     * Print the status, reason, and headers. Useful for debugging.
     * 
     * @return this
     */
    public HTTPResponse dump(PrintStream out) {
        out.println(String.format("HTTP %d: %s", getStatus(), getStatusText()));
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            out.println(String.format("%s: %s", entry.getKey(), entry.getValue()));
        }
        return this;
    }
}
