package com.forana.please;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.forana.please.exceptions.HTTPRequestException;
import com.forana.please.exceptions.HTTPResponseException;
import com.forana.please.util.ArbitraryMethodRequest;
import com.forana.please.util.ArbitraryMethodRequestWithBody;
import com.forana.please.util.NonValidatingClient;

/**
 * Object symbolizing an HTTP request. Supports builder-style population and chaining.
 * 
 * @author forana
 */
public class HTTPRequest {
    private String encoding = "UTF-8";

    private final String method;

    private final String url;

    private boolean verifyCertificates = true;

    private Map<String, String> headers;

    private List<NameValuePair> parameters;

    private HttpEntity entity;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new request with this specified method and URL.
     * 
     * This should not be called directly from client code - instances of this class should be
     * accessed from {@link com.forana.please.Please} or {@link com.forana.please.HTTPRequestFactory}.
     */
    protected HTTPRequest(String method, String url) {
        this.method = method;
        this.url = url;

        headers = new HashMap<>();
        parameters = new LinkedList<>();
    }

    /**
     * Add a header to this request.
     * 
     * Duplicate headers are not supported - subsequent calls with the same name will replace the
     * previous call's value. According to the HTTP spec, duplicate headers are allowed only if the
     * combined values are semantically identical to a comma-separated list of the values. If such a
     * thing is needed, you must combine the values manually.
     * 
     * @param name The name of the header (e.g. "Accept")
     * @param value The value for this header.
     * @return this
     */
    public HTTPRequest header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * Add a query string parameter to this request.
     * 
     * Duplicates are allowed and order will be preserved. The value will be toString()'d.
     * 
     * @param key
     * @param value
     * @return this
     */
    public HTTPRequest parameter(String key, Object value) {
        parameters.add(new BasicNameValuePair(key, value.toString()));
        return this;
    }

    /**
     * Add a body to this request as an {@link java.io.InputStream}.
     * 
     * Only one body can be added - if multiple are needed, see {@link #body(MultipartFormData)}.
     * 
     * @param stream
     * @return this
     */
    public HTTPRequest body(InputStream stream) {
        entity = new InputStreamEntity(stream);
        return this;
    }

    /**
     * Add a JSON body to this request as an {@link org.codehaus.jackson.JsonNode}.
     * 
     * Also sets the Content-Type header.
     * 
     * Only one body can be added - if multiple are needed, see {@link #body(MultipartFormData)}.
     * 
     * @param node
     * @return this
     * @throws JsonProcessingException If there's a problem serializing the JsonNode.
     * @throws HTTPRequestException If there's a general IOException in serialization.
     */
    public HTTPRequest body(JsonNode node) throws HTTPRequestException, JsonProcessingException {
        header("Content-Type", "application/json");
        try {
            StringWriter writer = new StringWriter();
            mapper.getJsonFactory()
                    .createJsonGenerator(writer)
                    .writeTree(node);
            entity = new StringEntity(writer.getBuffer().toString(), ContentType.APPLICATION_JSON);
            return this;
        } catch (IOException e) {
            throw new HTTPRequestException(e);
        }
    }

    /**
     * Add an object as a body to this request.
     * 
     * {@link java.lang.Object#toString()} will be called on the object, and the resulting String
     * sent over with the configured encoding.
     * 
     * Only one body can be added - if multiple are needed, see {@link #body(MultipartFormData)}.
     * 
     * @param obj
     * @return this
     */
    public HTTPRequest body(Object obj) {
        entity = new StringEntity(obj.toString(), encoding);
        return this;
    }

    /**
     * Add a multipart/form-data body to this request.
     * 
     * Only one body can be added - set multiple bodies inside of <code>formData</code>.
     * 
     * @param formData
     * @return this
     */
    public HTTPRequest body(MultipartFormData formData) {
        entity = formData.getEntity();
        return this;
    }

    /**
     * Set the encoding to use for serialization of both query string and body. This should be set
     * before the body if a body is needed. Defaults to UTF-8.
     * 
     * @param value
     * @return this
     */
    public HTTPRequest setEncoding(String value) {
        encoding = value;
        return this;
    }

    /**
     * Enable/disable certificate verification. Defaults to true (enabled).
     * 
     * This can be useful for development but shouldn't be used in production.
     * 
     * @param value
     * @return this
     */
    public HTTPRequest setVerifyCertificates(boolean value) {
        verifyCertificates = value;
        return this;
    }

    /**
     * Send the request and retrieve a response.
     * 
     * @return An {@link com.forana.please.HTTPResponse} object.
     * @throws HTTPRequestException If any IO-related exceptions occur while making this request.
     *             The thrown exception will wrap that exception.
     */
    public HTTPResponse send() throws HTTPRequestException {
        CloseableHttpClient client = null;
        try {
            client = createClient();
            URI uri = buildURI();
            HttpUriRequest request = buildRequest(uri);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            
            HTTPResponse response = new HTTPResponse(client.execute(request), client);
            client = null;
            return response;
        } catch (IOException e) {
            throw new HTTPRequestException(e);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    throw new HTTPRequestException(e);
                }
            }
        }
    }

    /**
     * Send the request and throw {@link com.forana.please.exceptions.HTTPResponseException} if
     * response.isOk() evaluates to <code>false</code>.
     * 
     * @return An {@link com.forana.please.HTTPResponse} object.
     * @throws HTTPRequestException If any IO-related exceptions occur while making this request.
     *             The thrown exception will wrap that exception.
     * @throws HTTPResponseException If the response status is not 20X.
     */
    public HTTPResponse sendAndVerify() throws HTTPRequestException, HTTPResponseException {
        HTTPResponse response = send();
        if (!response.isOk()) {
            throw new HTTPResponseException(String.format(
                    "Received unexpected status '%d' (%s)",
                    response.getStatus(), response.getStatusText()));
        }
        return response;
    }

    /**
     * Creates the client to be used for the request.
     * 
     * A subclass can override this to return a different type of client to be used, or customize
     * something about the one that would be returned.
     */
    protected CloseableHttpClient createClient() {
        return verifyCertificates
                ? HttpClients.createDefault()
                : NonValidatingClient.create();
    }

    /**
     * Builds the URI to be used for the request.
     * 
     * This takes into account query string parameters that might already be part of the
     * user-provided string, and merges those with any configured parameters.
     * 
     * @throws HTTPRequestException Wrapping the possible URISyntaxException.
     */
    protected URI buildURI() throws HTTPRequestException {
        try {
            URI baseURI = new URI(url);
            List<NameValuePair> mergedParams = new LinkedList<>();
            mergedParams.addAll(URLEncodedUtils.parse(baseURI, encoding));
            mergedParams.addAll(parameters);

            URI newURI = new URI(baseURI.getScheme(),
                    baseURI.getUserInfo(),
                    baseURI.getHost(),
                    baseURI.getPort(),
                    baseURI.getPath(),
                    URLEncodedUtils.format(mergedParams, encoding),
                    baseURI.getFragment());

            return newURI;
        } catch (URISyntaxException e) {
            throw new HTTPRequestException(e);
        }
    }

    /**
     * Provides the proper {@link HttpUriRequest} implementation for this request.
     * 
     * A subclass can override this to return a different type of request.
     * 
     * @param uri The URI that was built from {@link #buildURI()}.
     */
    protected HttpUriRequest buildRequest(URI uri) {
        HttpRequestBase request;
        if (entity == null) {
            request = new ArbitraryMethodRequest(method);
        } else {
            request = new ArbitraryMethodRequestWithBody(method, entity);
        }
        request.setURI(uri);
        return request;
    }
}
