package sample.windows.settings;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.config.CustomProperties;
import sample.config.ServerStatus;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsController {

    public Button btnSave;
    public Button btnClose;
    final CustomProperties customProperties = new CustomProperties();

    private ServerStatus serverStatus;

    public TextField txtIpAddress;
    public TextField txtPortNumber;
    public Button btnTestConnection;

    public void initialize(){
        serverStatus = new ServerStatus();
        txtPortNumber.textProperty().addListener((observedValue, oldValue, newValue)->{
            if (!newValue.matches("\\d{0,5}([\\.]\\d{0,4})?")) {
                txtPortNumber.setText(oldValue);
            }
        });

        btnSave.setOnAction(event-> saveSettings());

        btnClose.setOnAction(event -> {
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });

        btnTestConnection.setOnAction(event -> {
            if (validateIP(txtIpAddress.getText())){
                if (txtPortNumber.getText().length() > 1){
                    if (serverStatus.hostAvailabilityCheck(txtIpAddress.getText(), Integer.parseInt(txtPortNumber.getText()))){
                        JOptionPane.showMessageDialog(null, "Connection Successful\n");
                    }else{
                        JOptionPane.showMessageDialog(null, "Connection Refuse\nPlease check server configuration");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Please enter port number\n");
                }
            }else{
                JOptionPane.showMessageDialog(null, "Invalid ip address\n");
            }
        });
    }

    public void loadSettings(){
        if (!customProperties.getProperty(CustomProperties.SERVER_ADDRESS).isEmpty()){
            txtIpAddress.setText(customProperties.getProperty(CustomProperties.SERVER_ADDRESS));
        }
        if (!customProperties.getProperty(CustomProperties.PORT).isEmpty()){
            txtPortNumber.setText(customProperties.getProperty(CustomProperties.PORT));
        }

    }

    public void saveSettings(){
        if (validateIP(txtIpAddress.getText())){
            if (txtPortNumber.getText().length() > 1){
                customProperties.setProperty(CustomProperties.SERVER_ADDRESS, txtIpAddress.getText());
                customProperties.setProperty(CustomProperties.PORT, txtPortNumber.getText());
            }
        }
    }

    public boolean validateIP(final String ip) {
        Pattern pattern;
        Matcher matcher;
        String IPADDRESS_PATTERN
                = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }


}
