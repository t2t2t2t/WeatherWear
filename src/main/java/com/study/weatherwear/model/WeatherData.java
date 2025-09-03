package com.study.weatherwear.model;

import lombok.*;
import org.springframework.core.SpringVersion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WeatherData {
    private String city;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private String weatherDescription;
    private double windSpeed;
    private double uv;
    private double pressure;
    private String advice;
}
