package com.study.weatherwear.service;


import com.study.weatherwear.model.UvIndexResponse;
import com.study.weatherwear.model.WeatherData;
import com.study.weatherwear.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${config.apiKey}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData getWeather(String city) {
        try {
            WeatherResponse rawData = restTemplate.getForObject(buildWeatherUrl(city), WeatherResponse.class);

            if (rawData == null) {
                return null;
            }

            double uvIndex = getUvIndex(rawData.getCoord().getLat(), rawData.getCoord().getLon());

            WeatherData cleanData = convertToWeatherData(rawData);
            cleanData.setUv(uvIndex);

            return cleanData;

        } catch (Exception e) {
            System.out.println("Ошибка при получении погоды: " + e.getMessage());
            return null;
        }
    }

    private double getUvIndex(double lat, double lon) {
        try {
            String uvUrl = buildUvUrl(lat, lon);
            UvIndexResponse uvResponse = restTemplate.getForObject(uvUrl, UvIndexResponse.class);

            if (uvResponse != null) {
                return uvResponse.getValue();
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении UV индекса: " + e.getMessage());
        }

        return -1;
    }

    private WeatherData convertToWeatherData(WeatherResponse rawData) {
        if (rawData == null || rawData.getMain() == null || rawData.getWeather() == null || rawData.getWeather().isEmpty()) {
            return null;
        }

        WeatherData data = new WeatherData();
        data.setCity(rawData.getName());
        data.setTemperature(rawData.getMain().getTemp());
        data.setFeelsLike(rawData.getMain().getFeels_like());
        data.setHumidity((int) rawData.getMain().getHumidity());
        data.setWeatherDescription(rawData.getWeather().get(0).getDescription());
        data.setPressure(rawData.getMain().getPressure());

        data.setWindSpeed(rawData.getWind().getSpeed());
        data.setUv(-1);

        return data;
    }


    public String getCityByCoordinates(double lat, double lon) {
        try {
            String url = "http://api.openweathermap.org/geo/1.0/reverse" +
                    "?lat=" + lat +
                    "&lon=" + lon +
                    "&limit=1&appid=" + apiKey;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

            if (response != null && !response.isEmpty()) {
                Map<String, Object> location = response.get(0);

                String city = (String) location.get("name");
                if (isNotBlank(city)) return city;

                city = (String) location.get("locality");
                if (isNotBlank(city)) return city;

                city = (String) location.get("county");
                if (isNotBlank(city)) return city;

                return "Неизвестный город";
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка при геокодировании координат (" + lat + ", " + lon + "): " + e.getMessage());
            return null;
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }


    private String buildWeatherUrl(String city) {
        return "https://api.openweathermap.org/data/2.5/weather" +
                "?q=" + city +
                "&appid=" + apiKey +
                "&units=metric&lang=ru";
    }

    private String buildUvUrl(double lat, double lon) {
        return "https://api.openweathermap.org/data/2.5/uvi" +
                "?lat=" + lat +
                "&lon=" + lon +
                "&appid=" + apiKey;
    }


}
