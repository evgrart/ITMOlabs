package backend;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HitChecker {
    private BigDecimal x, y, r;
    private String errorMessage;

    private static final BigDecimal min_y = new BigDecimal("-5");
    private static final BigDecimal max_y = new BigDecimal("3");
    private static final Set<BigDecimal> x_val = new HashSet<>(Arrays.asList(
            new BigDecimal("-3"), new BigDecimal("-2"), new BigDecimal("-1"),
            new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("2"),
            new BigDecimal("3"), new BigDecimal("4"), new BigDecimal("5")
    ));

    public HitChecker(Map<String, String> params) {
        try {
            x = new BigDecimal(params.get("x").trim().replace(',', '.'));
            y = new BigDecimal(params.get("y").trim().replace(',', '.'));
            r = new BigDecimal(params.get("r").trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException e) {
            errorMessage = "Введены некорректные или отсутствующие числовые параметры.";
        }
    }

    public boolean validate() {
        if (errorMessage != null) return false;

        if (x == null || y == null || r == null) {
            errorMessage = "Задайте все три параметра";
            return false;
        }

        if (!x_val.contains(x)) {
            errorMessage = "X должен принадлежать множеству {-3, -2, -1, 0, 1, 2, 3, 4, 5}";
            return false;
        }

        if (y.compareTo(min_y) <= 0 || y.compareTo(max_y) >= 0) {
            errorMessage = "Y должен быть в диапазоне (-5, 3)";
            return false;
        }

        if (r.compareTo(BigDecimal.ZERO) <= 0) {
            errorMessage = "R должен быть положительным числом.";
            return false;
        }

        return true;
    }

    public boolean checkHit() {
        if (x.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal xSquared = x.multiply(x);
            BigDecimal ySquared = y.multiply(y);
            BigDecimal rSquared = r.multiply(r);
            BigDecimal rSquaredDiv4 = rSquared.divide(new BigDecimal("4"), 100, RoundingMode.HALF_UP);

            if (xSquared.add(ySquared).compareTo(rSquaredDiv4) <= 0) {
                return true;
            }
        }

        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            if (x.compareTo(r.negate()) >= 0 && y.compareTo(r) <= 0) {
                return true;
            }
        }

        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            if (x.add(y).compareTo(r.negate()) >= 0) {
                return true;
            }
        }

        return false;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getR() {
        return r;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
