package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet(name = "AreaCheckServlet", value = "/area-check")
public class AreaCheckServlet extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        if (context.getAttribute("results") == null) {
            context.setAttribute("results", new CopyOnWriteArrayList<Map<String, Object>>());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String xStr = request.getParameter("x").trim().replace(',', '.');
            String yStr = request.getParameter("y").trim().replace(',', '.');
            String rStr = request.getParameter("r").trim().replace(',', '.');
            
            BigDecimal x = new BigDecimal(xStr);
            BigDecimal y = new BigDecimal(yStr);
            BigDecimal r = new BigDecimal(rStr);
            
            if (!validateParameters(x, y, r)) {
                request.getRequestDispatcher("/game.jsp").forward(request, response);
                return;
            }
            
            boolean hit = checkHit(x, y, r);
            
            Map<String, Object> result = new HashMap<>();
            result.put("x", x);
            result.put("y", y);
            result.put("r", r);
            result.put("hit", hit);
            result.put("currentTime", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")));
            
            ServletContext context = getServletContext();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = 
                (List<Map<String, Object>>) context.getAttribute("results");
            results.add(0, result);
            
            if (results.size() > 30) {
                results.remove(results.size() - 1);
            }
            
            request.setAttribute("result", result);
            request.getRequestDispatcher("/result.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка обработки данных: " + e.getMessage());
            request.getRequestDispatcher("/game.jsp").forward(request, response);
        }
    }
    
    private boolean validateParameters(BigDecimal x, BigDecimal y, BigDecimal r) {
        Set<BigDecimal> validX = Set.of(
            new BigDecimal("-2"), new BigDecimal("-1.5"), new BigDecimal("-1"),
            new BigDecimal("-0.5"), BigDecimal.ZERO, new BigDecimal("0.5"),
            new BigDecimal("1"), new BigDecimal("1.5"), new BigDecimal("2")
        );
        
        if (!validX.contains(x)) return false;
        
        if (y.compareTo(new BigDecimal("-5")) <= 0 || y.compareTo(new BigDecimal("3")) >= 0) {
            return false;
        }
        
        Set<BigDecimal> validR = Set.of(
            new BigDecimal("1"), new BigDecimal("1.5"), new BigDecimal("2"),
            new BigDecimal("2.5"), new BigDecimal("3")
        );
        
        return validR.contains(r);
    }
    
    private boolean checkHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        
     
        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal rHalf = r.divide(new BigDecimal("2"));
            BigDecimal xHalf = x.divide(new BigDecimal("2"));
            return y.compareTo(xHalf.add(rHalf)) <= 0;
        }

        if (x.compareTo(BigDecimal.ZERO) <= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            return x.pow(2).add(y.pow(2)).compareTo(r.pow(2)) <= 0;
        }

        if (x.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal rHalf = r.divide(new BigDecimal("2"));
            return x.compareTo(r) <= 0 && y.compareTo(rHalf.negate()) >= 0;
        }
        
        return false;
    }
}