package com.example.lab3.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Named
@ApplicationScoped
public class LlmService implements Serializable {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llama3";
    
    private final HttpClient httpClient;
    private final Gson gson;

    public LlmService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(100))
                .build();
        this.gson = new Gson();
    }

    public String getExplanation(double x, double y, double r, boolean isHit) {
        String promptText = String.format(
            "Тебе нужно будет пояснить кратко (1-2 предложения), почему точка (%.2f, %.2f) %s область радиусом %.2f. Если пользователь попал (называй пользователя Астартес), то скажи, что пользователь (то есть астартес) - бездарь, а если точка попала в область, то похвали пользователя (пользователь это Астартес)" +
            "Область: четверть круга во 2-й четверти (радиус круга = радиус области пополам (R/2)), прямоугольник в 4-й четверти с длиной R и шириной R/2 (то есть противоположные вершины имеют координаты (0, 0) и (R, -R/2)), треугольник в 3-й четверти с координатами вершин (0, 0), (-R, 0), (0, -R/2)). Отвечай на русском языке",
            x, y, (isHit ? "попала" : "не попала"), r
        );

        System.out.println("[LLM-LOG] Request: " + promptText);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", MODEL_NAME);
        requestBody.addProperty("prompt", promptText);
        requestBody.addProperty("stream", false); 

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(100)) 
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                String explanation = jsonResponse.get("response").getAsString();
                
                System.out.println("[LLM-LOG] Response: " + explanation);
                return explanation;
            } else {
                System.err.println("[LLM-ERROR] Status: " + response.statusCode());
                return "Не удалось получить ответ";
            }

        } catch (Exception e) {
            System.err.println("[LLM-ERROR] " + e.getMessage());
            return "Малкадор спит на троне";
        }
    }
}

// разобраться бы в этом говне
