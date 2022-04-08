package io.github.fabiojose.snip.support;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class ByPassTLSValidation {

    private ByPassTLSValidation() {}

    private static TrustManager[] trusAllCerts() {
        return new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                    X509Certificate[] certs,
                    String authType
                ) {}

                public void checkServerTrusted(
                    X509Certificate[] certs,
                    String authType
                ) {}
            },
        };
    }

    private static HostnameVerifier trustAllHosts() {
        return new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    public static void setup() {
        try {
            final var context = SSLContext.getInstance("SSL");
            context.init(null, trusAllCerts(), new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.getSocketFactory()
            );

            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHosts());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {}
    }
}
