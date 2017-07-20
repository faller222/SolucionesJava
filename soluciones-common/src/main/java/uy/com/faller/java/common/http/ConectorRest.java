package uy.com.faller.java.common.http;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class ConectorRest {

    public static final int HTTP_OK = 200;


    public static void invocar(List<BasicNameValuePair> urlParameters, String restURL) throws AuditoriaException {
        HttpPost post = new HttpPost(restURL);
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            int codigo = response.getStatusLine().getStatusCode();
            if (codigo != HTTP_OK) {
                throw new RuntimException("ERROR  - Codigo devuelto :" + codigo);
            }
        } catch (Exception e) {
            if (e instanceof AuditoriaException) {
                throw (AuditoriaException) e;
            }
            throw new RuntimException("ERROR ", e);
        } finally {
            post.releaseConnection();
        }
    }
}
