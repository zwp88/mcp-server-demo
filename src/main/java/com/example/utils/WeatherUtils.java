package com.example.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.zip.GZIPInputStream;

@Slf4j
public class WeatherUtils {

    Logger logger = LoggerFactory.getLogger(WeatherUtils.class);
    public static String decompressGzip(byte[] compressedData) throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8))) {

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    public static JsonObject getCityData(String city) throws Exception {
        {
            //String apiUrl = "http://ip-api.com/json/" + ip;
            String apiUrl = "https://hu3dn53293.re.qweatherapi.com/geo/v2/city/lookup?number=1&location=" + city;
            log.info(apiUrl);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Authorization", "Bearer "+JwtUtil.getJwtToken("""
                -----BEGIN PRIVATE KEY-----
                MC4CAQAwBQYDK2VwBCIEIK9K0NSzrutZI8pp+JxWRMd2wip0Q0ajWTBq9xXt3dNU
                -----END PRIVATE KEY-----
                """))
                    .GET() // 或.POST(HttpRequest.BodyPublishers.ofString("request body"))
                    .build();


            HttpResponse<byte[]> byteResponse = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray());
            String responseBody = null;
            try {
                responseBody = WeatherUtils.decompressGzip(byteResponse.body());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info(responseBody);

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            if (json.get("code").getAsString().equals("200")) {
                //System.out.println(json);
                log.info(json.toString());
                return json;
            } else {
                return null;
            }
        }
    }

    public static JsonObject getCurrWeatherByLocation(String location) throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException, IOException, InterruptedException {
        //String apiUrl = "http://ip-api.com/json/" + ip;
        String apiUrl = "https://hu3dn53293.re.qweatherapi.com/v7/weather/now?location=" + location;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Authorization", "Bearer "+JwtUtil.getJwtToken("""
                -----BEGIN PRIVATE KEY-----
                MC4CAQAwBQYDK2VwBCIEIK9K0NSzrutZI8pp+JxWRMd2wip0Q0ajWTBq9xXt3dNU
                -----END PRIVATE KEY-----
                """))
                .GET() // 或.POST(HttpRequest.BodyPublishers.ofString("request body"))
                .build();


        HttpResponse<byte[]> byteResponse = client.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray());
        String responseBody = null;
        try {
            responseBody = WeatherUtils.decompressGzip(byteResponse.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        if (json.get("code").getAsString().equals("200")) {
            return json;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(WeatherUtils.getCurrWeatherByLocation(ToolsDemo1.ToolsContext.currLatAndLon()));
//        JsonArray location = WeatherUtils.getCityData(CustomerTool.currLocation()).get("location").getAsJsonArray();
//        System.out.println(location.get(0).getAsJsonObject().get("id").toString().replace("\"",""));
//        System.out.println(Objects.requireNonNull(WeatherUtils.getCurrWeatherByLocation(location.get(0).getAsJsonObject().get("id").toString().replaceAll("\"", ""))).get("now"));

        //        String publicIP = IPUtils.getPublicIP();
//        System.out.println("Public IP: " + publicIP);
//        String city = getCityFromIP(publicIP);
//        System.out.println("City: " + city);

    }

    }
