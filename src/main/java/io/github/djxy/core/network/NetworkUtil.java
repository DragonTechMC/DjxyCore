package io.github.djxy.core.network;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Samuel on 2016-05-01.
 */
public class NetworkUtil {

    public static String requestHttp(String httpUrl){
        return requestHttp(httpUrl, 1024);
    }

    public static String requestHttp(String httpUrl, int size){
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            if(con.getResponseCode() != 200)
                return null;

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer buffer = new StringBuffer(size);
            String line;

            while ((line = br.readLine()) != null)
                buffer.append(line+"\n");
            br.close();

            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static String requestHttps(String httpsUrl){
        return requestHttps(httpsUrl, 1024);
    }

    public static String requestHttps(String httpsUrl, int size){
        try {
            URL url = new URL(httpsUrl);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            if(con.getResponseCode() != 200)
                return null;

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer buffer = new StringBuffer(size);
            String line;

            while ((line = br.readLine()) != null)
                buffer.append(line+"\n");
            br.close();

            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
