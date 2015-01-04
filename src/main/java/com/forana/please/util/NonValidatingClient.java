package com.forana.please.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * CloseableHttpClient implementation that skips certificate validation.
 * 
 * @author forana
 */
public class NonValidatingClient {
    private static final SSLContext nonValidatingContext;
    
    static {
        try {
            nonValidatingContext = SSLContext.getInstance("SSL");
            
            nonValidatingContext.init(null, (new X509TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        }
        
                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        }
        
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            }), new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            // this should never actually get hit, but if it does we want everything to blow up
            // because something is VERY wrong
            throw new RuntimeException(e);
        }
    }
    
    private NonValidatingClient() {
    }
    
    public static CloseableHttpClient create() {
        return HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(
                nonValidatingContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
                ))
                .build();
    }
}
