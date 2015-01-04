package com.forana.please;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import com.forana.please.exceptions.HTTPException;

public class ExampleTest {
    private static final String EXAMPLE_URL = "http://alexforan.com/hello.txt";
    private static final String EXPECTED_TEXT = "hello world\n";
    
    @Test
    public void testPleaseExample() throws HTTPException {
        String text = Please
                .get(EXAMPLE_URL)
                .send()
                .getBodyText();

        assertEquals(EXPECTED_TEXT, text);
    }

    @Test
    public void testHttpClientExample() throws URISyntaxException, ClientProtocolException, IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        URI uri = new URI(EXAMPLE_URL);
        HttpRequestBase request = new HttpGet(uri);
        CloseableHttpResponse response = client.execute(request);
        InputStream stream = response.getEntity().getContent();
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String body = "";
        while (reader.ready()) {
            body += reader.readLine() + "\n";
        }

        assertEquals(EXPECTED_TEXT, body);
    }
}
