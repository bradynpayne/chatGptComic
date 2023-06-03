import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ChatTestMain {

    static String des;
    static String caption;



    public static void main(String[] args) throws IOException {
        String apiKey = "APIKEY";
        String endpointUrl = "https://api.openai.com/v1/chat/completions";

        try {
            // Create the URL object with the endpoint URL
            URL url = new URL(endpointUrl);

            // Create the HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP method to POST
            connection.setRequestMethod("POST");

            // Set the API key as an HTTP request header
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Enable input and output streams
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Create JSON payload
            String jsonPayload = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a funny creative writer\"}, {\"role\": \"user\", \"content\": \"Write the words Caption: then write a caption for a funny one panel comic, then write the word Description:, then write a simple short specific one sentence description of what's the image of the comic looks like without any explanation.\"}]}";

            // Set the Content-Type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Convert the payload to bytes and write it to the output stream
            byte[] postData = jsonPayload.getBytes(StandardCharsets.UTF_8);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData);
                outputStream.flush();
            }

            // Send the request
            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            // Read the response line by line
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            String test= response.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(test);

            JSONArray choicesArray = (JSONArray) jsonObject.get("choices");
            JSONObject thing = (JSONObject) choicesArray.get(0);
            JSONObject message = (JSONObject) thing.get("message");
            String content = (String) message.get("content");


            System.out.println(content);

            caption = content.substring(content.indexOf("Caption:") + 8, content.indexOf("Description:") - 2);
//            System.out.println("CAP" + caption);

             des = content.substring(content.indexOf("Description:") + 12);
            while(des.indexOf("\"") != -1) {
                int spot = des.indexOf("\"");
                String last = des.substring(0, spot);
                System.out.println(des);
                des = last + des.substring(spot + 1);
            }

//            System.out.println("DES:" + description);


            // Print the response
           // System.out.println(response.toString());

            // Close the connection
            connection.disconnect();

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String image_url;
        try {
            String theUrl = "https://api.openai.com/v1/images/generations";
            // Create the URL object with the endpoint URL
            URL url2 = new URL(theUrl);

            // Create the HttpURLConnection object
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();

            // Set the HTTP method to POST
            connection2.setRequestMethod("POST");

            // Set the API key as an HTTP request header
            connection2.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Enable input and output streams
            connection2.setDoInput(true);
            connection2.setDoOutput(true);

            // Create JSON payload
            String jsonPayload2 = "{\"prompt\": \"An illustrated comic without text: " + des +  "\", \"n\":1, \"size\": \"1024x1024\" }";

//          // Set the Content-Type header
            connection2.setRequestProperty("Content-Type", "application/json");

            // Convert the payload to bytes and write it to the output stream
            byte[] postData = jsonPayload2.getBytes(StandardCharsets.UTF_8);
            try (OutputStream outputStream = connection2.getOutputStream()) {
                outputStream.write(postData);
                outputStream.flush();
            }

            // Send the request
            int responseCode2 = connection2.getResponseCode();

            // Read the response
            BufferedReader reader2;
            if (responseCode2 == HttpURLConnection.HTTP_OK) {
                reader2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
            } else {
                reader2 = new BufferedReader(new InputStreamReader(connection2.getErrorStream()));
            }

            // Read the response line by line
            StringBuilder response2 = new StringBuilder();
            String line2;
            while ((line2 = reader2.readLine()) != null) {
                response2.append(line2);
            }
            reader2.close();
            String test = response2.toString();
            System.out.println(test);

            JSONParser parser2 = new JSONParser();
            JSONObject jsonObject2 = (JSONObject) parser2.parse(test);
            JSONArray data = (JSONArray) jsonObject2.get("data");
            JSONObject urlstuff = (JSONObject) data.get(0);
            image_url = (String) urlstuff.get("url");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        String path = image_url;
//        JLabel label2 = new JLabel("test");
//        label2.setText(caption);
        URL urlIMAGE = new URL(path);
        BufferedImage image = ImageIO.read(urlIMAGE);
        JLabel label = new JLabel(new ImageIcon(image));
        JFrame f = new JFrame();
        f.setLayout(new BorderLayout());
        f.add(new JLabel(caption), BorderLayout.PAGE_END);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        f.setLocation(200, 200);
        f.setVisible(true);
    }
}
