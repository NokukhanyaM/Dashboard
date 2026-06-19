package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;

public class AuthClient {

    private static final Logger log = LogManager.getLogger(AuthClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    public User getLoggedInUser(String token) {
        try {
            URL url = new URL("https://authserviceapp.onrender.com/api/auth/me");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

//            log.info("Connection status is: {}", conn);


                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                User user = objectMapper.readValue(reader, User.class);
//                log.info("Fetched users details: -> {}",user.getUsername());
//                 System.out.println("Fetched users details: -> "+ user.getUsername());
                return user;

        } catch (Exception e) {
            log.error("Exception has occurred: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
