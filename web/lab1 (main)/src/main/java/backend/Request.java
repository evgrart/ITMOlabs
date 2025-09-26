package backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.fastcgi.FCGIInterface;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final FCGIInterface fcgi;
    private static final String HEADER_TEMPLATE = "Content-Type: application/json\r\n\r\n%s";
    private HitChecker hitChecker;
    private String errorMessage;
    
    public Request(FCGIInterface fcgi) {
        this.fcgi = fcgi;
        String request = null;
        try {
            request = fcgi.request.params.getProperty("QUERY_STRING");
            String method = fcgi.request.params.getProperty("REQUEST_METHOD");
            if ("POST".equalsIgnoreCase(method)) {
                errorMessage = "POST не доступен";
                sendUnsuccessResponse(errorMessage);
                return;
            }
        } catch (Exception e) {
            errorMessage = "Ошибка чтения запроса";
            sendUnsuccessResponse(errorMessage);
            return;
        }

        if (request == null || request.isEmpty()) {
            errorMessage = "Ничего не указано";
            return;
        }

        Map<String, String> params = parse(request);

        String apiKey = params.get("apiKey");
        if (apiKey == null || !apiKey.equals("aboba-123-bebrochka")) {
            errorMessage = "401 Unauthorized";
            return;
        }

        hitChecker = new HitChecker(params);
    }

    public void process() {
        if (errorMessage != null || !hitChecker.validate()) {
            sendUnsuccessResponse(errorMessage != null ? errorMessage : hitChecker.getErrorMessage());
            return;
        }

        boolean flag = hitChecker.checkHit();
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("x", hitChecker.getX());
        responseData.put("y", hitChecker.getY());
        responseData.put("r", hitChecker.getR());
        responseData.put("currentTime", currentTime);
        responseData.put("hit", flag);

        sendSuccessResponse(responseData);
    }

    private void sendSuccessResponse(Map<String, Object> data) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toPlainString());
            }
        });

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(data);
        String httpResponse = String.format(HEADER_TEMPLATE, json);
        System.out.print(httpResponse);
    }

    private void sendUnsuccessResponse(String message) {
        Map<String, String> errorMap = Map.of("error", message);
        String json = new Gson().toJson(errorMap);
        String httpResponse = String.format(HEADER_TEMPLATE, json);
        System.out.print(httpResponse);
    }

    private Map<String, String> parse(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key;
            String value;
            if (idx > 0) {
                key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            } else {
                key = URLDecoder.decode(pair, StandardCharsets.UTF_8);
                value = "";
            }
            params.put(key, value);
        }
        return params;
    }

}