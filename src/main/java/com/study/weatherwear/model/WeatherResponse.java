package com.study.weatherwear.model;

import lombok.*;

import javax.xml.stream.Location;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private List<Weather> weather;
    private Coord coord;
    private Main main;
    private Wind wind;
    private Rain rain;
    private int timezone;
    private int visibility;
    private int id;
    private String name;
    private int cod;
}
