package ntlm4nav;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

/**
 *
 * @author sascha.kohlmann
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        // Trust all SSL connections. Don't use this in production
        final SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
    
        final CloseableHttpClient httpclient = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();

        // The important part
        credsProvider.setCredentials(AuthScope.ANY,
                                     new NTCredentials("USERNAME",  // User
                                                       "PASSWORD",  // Password
                                                       "ANY",       // Computer name (any)
                                                       "DOAMIN"));  // Domain

        final HttpHost target = new HttpHost("acheloos.navision.misterspex.yy", 28001, "https");

        // Make sure the same context is used to execute logically related requests
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        
        // Execute a cheap method first. This will trigger NTLM authentication
        final HttpGet httpget = new HttpGet("/DynamicsNAV/WS/Mister%20Spex%20GmbH/Codeunit/WMS_WebInterface?wsdl");
        System.out.println(httpget);
        try (final CloseableHttpResponse response1 = httpclient.execute(target, httpget, context)) {
            System.out.println(response1);
            HttpEntity entity1 = response1.getEntity(); 
            System.out.println(entity1);
            entity1.writeTo(System.out);
        }
    }    
}
