package com.example.edusence_studio.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class SupabaseConfig {

    @Bean
    public RestTemplate restTemplate() {
        try {
            // Create a trust manager that accepts all certificates (for hackathon demo)
            // WARNING: This disables SSL certificate validation - use only for development/demo
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            
            // Create a custom request factory that uses our SSL context
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) {
                    if (connection instanceof HttpsURLConnection) {
                        ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                        ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                    }
                }
            };
            
            factory.setConnectTimeout(30000); // 30 seconds
            factory.setReadTimeout(30000);
            
            return new RestTemplate(factory);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            // Fallback to default RestTemplate if SSL configuration fails
            e.printStackTrace();
            return new RestTemplate();
        }
    }
}
