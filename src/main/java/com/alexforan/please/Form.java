package com.alexforan.please;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

/**
 * Represents a application/x-www-form-urlencoded form. Supports builder-style chainable calls.
 * 
 * @author forana
 */
public class Form {
    private List<NameValuePair> fields = new LinkedList<>();

    public Form() {
    }

    /**
     * Add all values from map to this form under their respective keys.
     * 
     * The values will be toString()'d if not null.
     * 
     * @param map
     * @return this
     */
    public Form addAll(Map<String, ?> map) {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }

        return this;
    }

    /**
     * Add a number of values under a single name.
     * 
     * For example, <code>form.addAll("k", Arrays.asList("a", "b", "c"))</code> would result in a
     * body of <code>k=a&amp;k=b&amp;k=c</code>.
     * 
     * The values will be toString()'d if not null.
     * 
     * @return this
     */
    public Form addAll(String name, Iterable<?> values) {
        for (Object value : values) {
            add(name, value);
        }
        
        return this;
    }

    /**
     * Add a single field to the form.
     * 
     * The value will be toString()'d if not null.
     * 
     * @return this;
     */
    public Form add(String name, Object value) {
        fields.add(new BasicNameValuePair(name,
                value == null ? null : value.toString()));
        return this;
    }

    /**
     * Builds the HttpEntity from this form.
     */
    protected HttpEntity getEntity(String charset) {
        String body = URLEncodedUtils.format(fields, charset);
        return new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED);
    }
}
