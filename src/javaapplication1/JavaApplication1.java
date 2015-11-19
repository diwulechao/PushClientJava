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
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

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
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
        pin.setShutdownOptions(true, PinState.LOW);

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
                try {
                    switch (s) {
                        case "on":
                            pin.high();
                            break;
                        case "off":
                            pin.low();
                            break;
                        case "toggle":
                            pin.toggle();
                            break;
                        default:
                    }
                } catch (Exception e) {}
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

//    public static void main(String[] args) throws InterruptedException {
//
//        System.out.println("<--Pi4J--> GPIO Control Example ... started.");
//
//        // create gpio controller
//        final GpioController gpio = GpioFactory.getInstance();
//
//        // provision gpio pin #01 as an output pin and turn on
//        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
//
//        // set shutdown state for this pin
//        pin.setShutdownOptions(true, PinState.LOW);
//
//        System.out.println("--> GPIO state should be: ON");
//
//        Thread.sleep(5000);
//
//        // turn off gpio pin #01
//        pin.low();
//        System.out.println("--> GPIO state should be: OFF");
//
//        Thread.sleep(5000);
//
//        // toggle the current state of gpio pin #01 (should turn on)
//        pin.toggle();
//        System.out.println("--> GPIO state should be: ON");
//
//        Thread.sleep(5000);
//
//        // toggle the current state of gpio pin #01  (should turn off)
//        pin.toggle();
//        System.out.println("--> GPIO state should be: OFF");
//
//        Thread.sleep(5000);
//
//        // turn on gpio pin #01 for 1 second and then off
//        System.out.println("--> GPIO state should be: ON for only 1 second");
//        pin.pulse(1000, true); // set second argument to 'true' use a blocking call
//
//        // stop all GPIO activity/threads by shutting down the GPIO controller
//        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
//        gpio.shutdown();
//    }
}
