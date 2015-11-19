/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author diwulechao
 */
public class JavaApplication1 {

    /**
     * @param args the command line arguments
     */
    
    public static long lasterrortime;
    
    public static void main(String[] args) {
        while (true) {
            if (System.currentTimeMillis() - lasterrortime < 5000) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            }

            String s = excutePost("http://hacktestd.cloudapp.net:1802", "iot1|no|1");
            if (s != null) {
                System.out.println(s);
            }
        }
    }

    public static String excutePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        lasterrortime = System.currentTimeMillis();
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setReadTimeout(300000);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response  
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            lasterrortime = 0;
            return response.toString();
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
