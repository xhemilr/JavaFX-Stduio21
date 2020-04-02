package sample.config;

import java.io.IOException;
import java.net.Socket;

public class ServerStatus {


    public boolean hostAvailabilityCheck(String SERVER_ADDRESS, int TCP_SERVER_PORT)
    {
        boolean available = true;
        try {
            new Socket(SERVER_ADDRESS, TCP_SERVER_PORT).close();
        } catch (NullPointerException | IOException e)
        { // unknown host
            available = false;
        }// io exception, service probably not running

        return available;
    }
}
