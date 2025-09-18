package backend;

import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.fastcgi.FCGIInterface;

public class Request {
    private final FCGIInterface fcgi;
    private static final String HEADER_TEMPLATE = "Content-Type: application/json\r\n\r\n%s";  
    HitChecker hitChecker;    
    private String errorMessage;     

    public Request(FCGIInterface fcgi) {
        this.fcgi = fcgi;
        String request = null;
        try {
            request = fcgi.request.params.getProperty("QUERY_STRING");
        } catch (Exception e) {
        }

        if (request == null || request.isEmpty()) {
            errorMessage = "Параметры не заданы";
            return;
        }
        Map<String, String> params = parse(request);
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
        String json = new Gson().toJson(data);
        String httpResponse = String.format(HEADER_TEMPLATE, json);
        System.out.print(httpResponse); 
    }

    private void sendUnsuccessResponse(String message) {
        Map<String, String> errorMap = Map.of("error", message);
        String json = new Gson().toJson(errorMap);
        String httpResponse = String.format(HEADER_TEMPLATE, json);
        System.out.print(httpResponse);
    }

    private Map<String, String> parse(String query) { // query = "x=1&y=2&r=3"
        Map<String, String> params = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                params.put(pair[0], pair[1]);
            } else {
                params.put(pair[0], "");
            }
        }
        return params;
    }
}
