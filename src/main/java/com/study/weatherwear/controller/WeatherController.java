package com.study.weatherwear.controller;

import com.study.weatherwear.model.WeatherData;
import com.study.weatherwear.service.AdviceService;
import com.study.weatherwear.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class WeatherController {

    private final WeatherService weatherService;
    private final AdviceService adviceService;

    public WeatherController(WeatherService weatherService, AdviceService adviceService) {
        this.weatherService = weatherService;
        this.adviceService = adviceService;
    }


    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("city", "");
        model.addAttribute("weather", null);
        model.addAttribute("error", null);
        return "weather";
    }



    @PostMapping("/weather")
    public String getWeather(@RequestParam String city, Model model) {
        try {
            WeatherData weatherData = weatherService.getWeather(city);
            if (weatherData == null) {
                model.addAttribute("error", "Город не найден: " + city);
                return "weather";
            }

            String advice = adviceService.generateAdvice(weatherData);
            weatherData.setAdvice(advice);

            List<String> adviceList = adviceService.generateAdviceList(weatherData);
            model.addAttribute("adviceList", adviceList);

            model.addAttribute("weather", weatherData);
            model.addAttribute("city", city);
            model.addAttribute("error", null);

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "weather";
    }


    @PostMapping("/weather-by-coords")
    public ResponseEntity<Map<String, String>> getCityByCoordinates(@RequestBody Map<String, Double> coords) {
        Double lat = coords.get("lat");
        Double lon = coords.get("lon");

        if (lat == null || lon == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Координаты 'lat' и 'lon' обязательны"));
        }

        String city = weatherService.getCityByCoordinates(lat, lon);

        if (city == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Город не найден по координатам"));
        }

        return ResponseEntity.ok(Map.of("city", city));
    }

}