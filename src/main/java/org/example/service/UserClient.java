package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserClient {

    private final String BASE_URL = "https://userserviceapp-1.onrender.com/users";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                users = mapper.readValue(
                        response.toString(),
                        new TypeReference<List<User>>() {}
                );


        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public void deleteUser(Long id) {
        try {
            URL url = new URL(BASE_URL + "/delete/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            conn.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        try {
            URL url = new URL(BASE_URL + "/update");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            mapper.writeValue(conn.getOutputStream(), user);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed to update user, HTTP code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}