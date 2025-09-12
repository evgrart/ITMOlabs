package backend;

import java.util.Map;

public class HitChecker {
    private Double x, y, r;
    private String errorMessage;

    public HitChecker(Map<String, String> params) {
        try {
            x = Double.parseDouble(params.get("x"));
            y = Double.parseDouble(params.get("y"));
            r = Double.parseDouble(params.get("r"));
        } catch (NumberFormatException e) {
            errorMessage = "Введены некорректные данные";
        }
    }

    public boolean validate() {
        if (errorMessage != null) return false;
        if (x == null || y == null || r == null) {
            errorMessage = "Параметры не заданы";
            return false;
        }
        boolean flag = (x != null && (x == -3 || x == -2 || x == -1 || x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 5));
        if (!flag) {
            errorMessage = "X должен принадлежать множеству {-3, -2, -1, 0, 1, 2, 3, 4, 5}";
            return false;
        }
        if (y <= -5 || y >= 3) {
            errorMessage = "Y должен быть в диапазоне (-5, 3)";
            return false;
        }
        return true;
    }

    public boolean checkHit() {
        if (x >= -r/2 && x <= 0 && y >= 0 && y <= r) {
            return true;
        }
        if (x <= 0 && y <= 0 && y >= -x - r/2) {
            return true;
        }
        if (x >= 0 && y <= 0 && (x*x + y*y <= r*r)) {
            return true;
        }
        return false;
    }

    public Double getX() {
        return x; 
    }

    public Double getY() { 
        return y; 
    }

    public Double getR() { 
        return r; 
    }

    public String getErrorMessage() { 
        return errorMessage; 
    }
}