package com.study.weatherwear.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.weatherwear.config.AdviceRuleConfig;
import com.study.weatherwear.model.WeatherData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdviceService {

    private final List<AdviceRuleConfig> rules;

    public AdviceService(ObjectMapper objectMapper) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("advice-rules.json");
        if (inputStream == null) {
            throw new IllegalStateException("Файл advice-rules.json не найден");
        }

        this.rules = objectMapper.readValue(inputStream,
                new TypeReference<List<AdviceRuleConfig>>() {});
    }

    public List<String> generateAdviceList(WeatherData weatherData) {
        return rules.stream()
                .filter(rule -> matches(rule.getCondition(), weatherData))
                .map(AdviceRuleConfig::getAdvice)
                .collect(Collectors.toList());
    }


    public String generateAdvice(WeatherData weatherData) {
        return rules.stream()
                .filter(rule -> matches(rule.getCondition(), weatherData))
                .map(AdviceRuleConfig::getAdvice)
                .collect(Collectors.joining(" "));
    }

    private boolean matches(AdviceRuleConfig.Condition condition, WeatherData data) {
        String field = condition.getField();
        String op = condition.getOperator();
        Object value = condition.getValue();

        return switch (field.toLowerCase()) {
            case "uv" -> compareDouble(data.getUv(), value, op);
            case "temperature" -> compareDouble(data.getTemperature(), value, op);
            case "windspeed" -> compareDouble(data.getWindSpeed(), value, op);
            case "humidity" -> compareDouble((double) data.getHumidity(), value, op);
            case "weatherdescription" -> compareString(data.getWeatherDescription(), value, op);
            default -> false;
        };
    }

    private boolean compareDouble(Double actual, Object expected, String op) {
        if (actual == null || !(expected instanceof Number)) return false;
        double val = ((Number) expected).doubleValue();
        return switch (op) {
            case ">=" -> actual >= val;
            case ">" -> actual > val;
            case "<=" -> actual <= val;
            case "<" -> actual < val;
            case "==" -> actual.equals(val);
            default -> false;
        };
    }

    private boolean compareString(String actual, Object expected, String op) {
        if (actual == null || !(expected instanceof String)) return false;
        String val = ((String) expected).toLowerCase();
        return switch (op) {
            case "contains" -> actual.toLowerCase().contains(val);
            case "equals" -> actual.equalsIgnoreCase(val);
            default -> false;
        };
    }
}
