package sample.config;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class CustomProperties {

    private static Properties properties;

    public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
    public static final String PORT = "PORT";
    private static final String SERVICE_API = "service";

    public CustomProperties() {
        properties = new Properties();
        loadProperties();
    }

    public void loadProperties() {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("application.properties"));
            properties.load(reader);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not load properties\n" +
                    "Error Code: " + e.getMessage());
        }finally {
            if (reader!=null){
                try {
                    reader.close();
                }catch (IOException e){
                    JOptionPane.showMessageDialog(null, "Could not close the reader\n" +
                            "Error Code: " + e.getMessage());
                }
            }

        }
    }

    public void setProperty(String key, String value) {
        FileOutputStream out = null;
        boolean created = false;
        File f = new File("application.properties");
        if (!f.exists()){
            try {
                 created = f.createNewFile();
            }catch (IOException e){
                JOptionPane.showMessageDialog(null, "Could not close the reader\n" +
                        "Error Code: " + e.getMessage());
            }
        }else{
            created = true;
        }
        if (created){
            try {
                out = new FileOutputStream("application.properties");
                properties.setProperty(key, value);
                properties.store(out, null);
                out.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not load properties\n" +
                        "Error Code: " + e.getMessage());
            }finally {
                if (out!=null){
                    try {
                        out.close();
                    }catch (IOException e){
                        JOptionPane.showMessageDialog(null, "Could not close writer\n" +
                                "Error Code: " + e.getMessage());
                    }
                }
            }
        }
    }

    public String getProperty(String key) {
        if (properties.containsKey(key)){
            return properties.getProperty(key);
        }
        return "";
    }

    public String getServerUrl() {
        StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(getProperty(SERVER_ADDRESS));
        url.append(":");
        url.append(getProperty(PORT));
        url.append("/");
        url.append(SERVICE_API);
        return url.toString();
    }
}
