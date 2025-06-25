package com.example.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class IPLocation {
    public static JsonObject getCityFromIP(String ip) throws IOException, MalformedURLException {
        //String apiUrl = "http://ip-api.com/json/" + ip;
        String apiUrl = "http://ip-api.com/json/" + ip;
        URL url = new URL(apiUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        if (json.get("status").getAsString().equals("success")) {
            log.info(json.toString());
            return json;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
//        String publicIP = IPUtils.getPublicIP();
//        System.out.println("Public IP: " + publicIP);
//        String city = getCityFromIP(publicIP);
//        System.out.println("City: " + city);
    }
}