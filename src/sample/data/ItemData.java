package sample.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sample.config.CustomProperties;
import sample.model.Item;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

public class ItemData {

    final CustomProperties customProperties = new CustomProperties();

    private static CloseableHttpClient client;

    private static String authHeader;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static String URL_STRING = null;

    public ItemData(){
       URL_STRING = customProperties.getServerUrl();
        String auth = "besim" + ":" + "besim123";
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        authHeader = "Basic " + new String(encodedAuth);
    }

    public void getJsonData(ObservableList<Item> items){
        items.clear();
        client = HttpClients.createDefault();
        if (URL_STRING == null){
            JOptionPane.showMessageDialog(null, "Could not load server address\n");
            return;
        }
        HttpGet request = new HttpGet(URL_STRING);
        request.setHeader("Content-Type", "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        try (CloseableHttpResponse response = client.execute(request)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    Item[] newItem = mapper.readValue(result, Item[].class);
                    Collections.addAll(items, newItem);
                }
            }else {
                JOptionPane.showMessageDialog(null, "Could not load items\n" +
                        "Error Code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not load items. Please check server!\n" +
                            "Error: Connection refused",
                    "Error loading items ", JOptionPane.ERROR_MESSAGE);
        }finally {
            try {
                client.close();
            }catch (IOException e){
                JOptionPane.showMessageDialog(null, "Something went wrong!\n Shutting down application",
                        "Unexpected Error", JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            }
        }
    }

    private static void insertItem(Item item){
        client = HttpClients.createDefault();
        if (URL_STRING == null){
            JOptionPane.showMessageDialog(null, "Could not load server address\n");
            return;
        }
        HttpPost httpPost = new HttpPost(URL_STRING);

        httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        try {
            String jsonString = mapper.writeValueAsString(item);
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");
            client.execute(httpPost);

        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(null, "Bad data. Please check API",
                    "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Query could not be executed. Please check connection",
                    "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                client.close();
            }catch (IOException e){
                JOptionPane.showMessageDialog(null, "Something went wrong!\n Shutting down application",
                        "Unexpected Error", JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            }
        }

    }

    public void deleteItem(int id){
        client = HttpClients.createDefault();
        if (URL_STRING == null){
            JOptionPane.showMessageDialog(null, "Could not load server address\n");
            return;
        }
        HttpDelete httpDelete = new HttpDelete(URL_STRING + "/" + id);
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        try {
            client.execute(httpDelete);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Query could not be executed. Please check connection",
                    "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                client.close();
            }catch (IOException e){
                JOptionPane.showMessageDialog(null, "Something went wrong!\n Shutting down application",
                        "Unexpected Error", JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            }
        }
    }

    public static void saveItem(Item item){
        insertItem(item);
    }
}
