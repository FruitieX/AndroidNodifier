package fruitiex.androidnodifier;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by rasse on 3/6/15.
 */
public class DoHttpPost {
    public void execute(final String method, final JSONObject obj) {
        final String TAG = "nodifier";
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    // open client certificate file
                    File dir = Environment.getExternalStorageDirectory();
                    FileInputStream keyStream = new FileInputStream(dir + "/nodifier-httpbridge.p12");
                    FileInputStream certStream = new FileInputStream(dir + "/nodifier-httpbridge-cert.pem");

                    // create key store
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    keyStore.load(keyStream, "".toCharArray());
                    keyStream.close();

                    // create a key manager for the SSL context
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                    kmf.init(keyStore, "".toCharArray());
                    KeyManager[] keyManagers = kmf.getKeyManagers();

                    // create trust store containing the CA cert
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate) certificateFactory
                            .generateCertificate(certStream);
                    String alias = cert.getSubjectX500Principal().getName();
                    certStream.close();

                    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null);
                    trustStore.setCertificateEntry(alias, cert);

                    // create a trust manager for the SSL context
                    TrustManagerFactory tmf =
                            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(keyStore);

                    // create the SSL context
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(keyManagers, tmf.getTrustManagers(), new SecureRandom());

                    SSLSocketFactory factory = new SSLSocketFactory(keyStore, "", trustStore);

                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    ClientConnectionManager manager = httpClient.getConnectionManager();
                    manager.getSchemeRegistry().register(new Scheme("https", factory, 5678));

                    HttpPost post = new HttpPost("https://fruitiex.org:5678/?method=" + method);
                    post.setEntity(new ByteArrayEntity(
                            obj.toString().getBytes("UTF8")));
                    post.setHeader("Content-Type", "application/json");

                    httpClient.execute(post);

                    return true;

                    //Scheme scheme = new Scheme("https", sc., 443);
                    //socket = IO.socket("https://fruitiex.org:9000", opts);
                } catch (NoSuchAlgorithmException e) {
                    System.out.println(e);
                } catch (KeyStoreException e) {
                    System.out.println(e);
                } catch (CertificateException e) {
                    System.out.println(e);
                } catch (UnrecoverableKeyException e) {
                    System.out.println(e);
                } catch (KeyManagementException e) {
                    System.out.println(e);
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }

                return false;
            }
            @Override
            protected void onPostExecute(Boolean status) {
                Log.i(TAG, "HTTP request finished with status: " + status);
            }
        }.execute();
    }
}
