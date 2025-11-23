package com.example.lab3.beans;

import com.example.lab3.model.CheckResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Named("areaCheckBean")
@SessionScoped
public class AreaCheckBean implements Serializable {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    private Double x = 0.0;
    private Double y = 0.0;
    private Double r = 1.5; 

    private List<CheckResult> results;

    private final transient Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    @PostConstruct
    public void init() {
        loadResults();
    }

    @Transactional
    public void check() {
        processCheck(this.x, this.y);
    }

    @Transactional
    public void checkFromCanvas() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        try {
            double canvasX = Double.parseDouble(params.get("x"));
            double canvasY = Double.parseDouble(params.get("y"));
            processCheck(canvasX, canvasY);
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("Error parsing canvas coordinates: " + e.getMessage());
        }
    }

    private void processCheck(double x, double y) {
        CheckResult result = new CheckResult();
        result.setX(x);
        result.setY(y);
        result.setR(this.r);
        result.setHit(isHit(x, y, this.r));
        result.setRequestTime(LocalDateTime.now());

        entityManager.persist(result);
        if (results != null) {
            results.add(0, result);
        }
    }

    private void loadResults() {
        try {
            this.results = entityManager.createQuery("SELECT r FROM CheckResult r ORDER BY r.requestTime DESC", CheckResult.class)
                                        .getResultList();
        } catch (Exception e) {
            this.results = Collections.emptyList();
            System.err.println("Error loading results from DB: " + e.getMessage());
        }
    }

    private boolean isHit(double x, double y, double r) {
        // Прямоугольник
        if (x >= 0 && y <= 0 && x <= r && y >=  -r / 2) {
            return true;
        }
        // Четверть круга
        if (x <= 0 && y >= 0 && (x * x + y * y <= (r * r / 4))) {
            return true;
        }
        // Треугольник
        if (x <= 0 && y <= 0 && y >= (-x / 2 - r / 2)) {
            return true;
        }
        return false;
    }

    public String getResultsAsJson() {
        if (results == null) {
            return "[]";
        }
        return gson.toJson(results);
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }
    public List<CheckResult> getResults() { return results; }
    public void setResults(List<CheckResult> results) { this.results = results; }
}