package io.github.clamentos.gattoslabgateway.exchange;

///
import io.github.clamentos.gattoslabgateway.Application;
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.configuration.environments.Environment;

///..
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;

///..
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

///..
import lombok.extern.slf4j.Slf4j;

import com.sun.net.httpserver.HttpServer;
///..
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

///
@Slf4j

///
public final class RequestExchanger implements Closeable {

    ///
    private final Duration stopTimeout;

    ///..
    private final HttpServer httpServer;

    ///
    public RequestExchanger(final ApplicationProperties applicationProperties)
    throws CertificateException, IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {

        stopTimeout = applicationProperties.getServerStopTimeout();
        //final SSLContext sslContext = createSSLContext(applicationProperties.getSslKeystorePassword(), applicationProperties);

        httpServer = HttpServer.create(

            new InetSocketAddress(applicationProperties.getServerPort()),
            applicationProperties.getSocketQueueSize(),
            "/",
            new RequestExchangerHandler()
        );

        //httpServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));
        httpServer.start();
    }

    ///
    @Override
    public void close() throws IOException {

        httpServer.stop((int)stopTimeout.toSeconds());
    }

    ///.
    private SSLContext createSSLContext(final String password, final ApplicationProperties applicationProperties)
    throws CertificateException, IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {

        log.info("Loading SSL certificate start...");
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        keyManagerFactory.init(loadKeyStore(password, applicationProperties), password.toCharArray());

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        log.info("Loading SSL certificate end");
        return sslContext;
    }

    ///..
    private KeyStore loadKeyStore(final String password, final ApplicationProperties applicationProperties)
    throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {

        final Environment currentEnvironment = applicationProperties.getCurrentEnvironment();
        final String name = applicationProperties.getSslKeystoreName();

        final InputStream keyStream = currentEnvironment == Environment.PROD ?

            new FileInputStream("./" + name) :
            Application.class.getClassLoader().getResourceAsStream(name)
        ;

        try(keyStream) {

            log.info("Keystore file grabbed {}", keyStream != null);

            final KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            loadedKeystore.load(keyStream, password.toCharArray());

            return loadedKeystore;
        }
    }

    ///
}
