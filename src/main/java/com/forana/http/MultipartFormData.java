package com.forana.http;

import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * Wrapper for a multipart/form-data request.
 * 
 * @author forana
 */
public class MultipartFormData {
    private final MultipartEntityBuilder builder;

    public MultipartFormData() {
        builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    /**
     * Builds the entity for this request. This should not be called by client code.
     */
    protected HttpEntity getEntity() {
        return builder.build();
    }

    /**
     * Adds a data section under a specified name.
     * 
     * Sets the content-type to <code>application/octet-stream</code> and the filename to the same
     * name as the form field.
     * 
     * @see org.apache.http.entity.mime.MultipartEntityBuilder#addBinaryBody(String, byte[])
     * 
     * @return this
     */
    public MultipartFormData data(String name, byte[] bytes) {
        return data(name, bytes, ContentType.APPLICATION_OCTET_STREAM, name);
    }

    /**
     * Adds a data section under a specified name.
     * 
     * @see org.apache.http.entity.mime.MultipartEntityBuilder#addBinaryBody(String, byte[],
     *      ContentType, String)
     * 
     * @return this
     */
    public MultipartFormData data(String name, byte[] bytes, ContentType type, String filename) {
        builder.addBinaryBody(name, bytes, type, filename);
        return this;
    }

    /**
     * Adds a data section under a specified name.
     * 
     * @see org.apache.http.entity.mime.MultipartEntityBuilder#addBinaryBody(String, File)
     * 
     * @return this
     */
    public MultipartFormData data(String name, File file) {
        builder.addBinaryBody(name, file);
        return this;
    }

    /**
     * Adds a data section under a specified name.
     * 
     * @see org.apache.http.entity.mime.MultipartEntityBuilder#addBinaryBody(String, File,
     *      ContentType, String)
     * 
     * @return this
     */
    public MultipartFormData data(String name, File file, ContentType type, String filename) {
        builder.addBinaryBody(name, file, type, filename);
        return this;
    }

    /**
     * Adds a data section under a specified name.
     * 
     * Sets the content-type to <code>application/octet-stream</code> and the filename to the same
     * name as the form field.
     * 
     * @see org.apache.http.entity.mime.MultipartEntityBuilder#addBinaryBody(String, InputStream)
     * 
     * @return this
     */
    public MultipartFormData data(String name, InputStream stream) {
        return data(name, stream, ContentType.APPLICATION_OCTET_STREAM, name);
    }

    /**
     * Adds a data section under a specified name.
     * 
     * @see org.apache.http.entity.mime.MultipartEntityBuilder#addBinaryBody(String, InputStream,
     *      ContentType, String)
     * 
     * @return this
     */
    public MultipartFormData data(String name, InputStream stream, ContentType type, String filename) {
        builder.addBinaryBody(name, stream, type, filename);
        return this;
    }

    /**
     * Adds a field to the form.
     * 
     * Duplicates are allowed and order will be maintained. The value will be toString()'d.
     * 
     * @param name
     * @param value
     * @return this
     */
    public MultipartFormData field(String name, Object value) {
        builder.addTextBody(name, value.toString());
        return this;
    }
}
