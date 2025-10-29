package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.tribuo.Example;
import org.tribuo.clustering.ClusterID;
import org.tribuo.clustering.kmeans.KMeansModel;
import org.tribuo.impl.ArrayExample;

@WebServlet(name = "AreaCheckServlet", value = "/area-check")
public class AreaCheckServlet extends HttpServlet {

    private KMeansModel kMeansModel;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        if (context.getAttribute("results") == null) {
            context.setAttribute("results", new CopyOnWriteArrayList<Map<String, Object>>());
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("kmeans-tribuo.model");
             ObjectInputStream ois = new ObjectInputStream(is)) {
            this.kMeansModel = (KMeansModel) ois.readObject();
            System.out.println("Tribuo K-Means модель успешно загружена.");
        } catch (Exception e) {
            throw new ServletException("Ошибка загрузки Tribuo K-Means модели", e);
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

            int clusterId = -1; 
            if (kMeansModel != null) {
                try {
                    String[] featureNames = {"x", "y"};
                    double[] values = {x.doubleValue(), y.doubleValue()};
                    Example<ClusterID> example = new ArrayExample<>(new ClusterID(ClusterID.UNASSIGNED), featureNames, values);

                    ClusterID prediction = kMeansModel.predict(example).getOutput();
                    clusterId = prediction.getID();

                } catch (Exception e) {
                    System.err.println("Ошибка предсказания кластера Tribuo: " + e.getMessage());
                    clusterId = -1;
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("x", x);
            result.put("y", y);
            result.put("r", r);
            result.put("hit", hit);
            result.put("currentTime", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")));
            
            result.put("cluster", clusterId); 

            ServletContext context = getServletContext();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) context.getAttribute("results");
            results.add(0, result);

            if (results.size() > 30) {
                results.remove(results.size() - 1);
            }
            HttpSession session = request.getSession();
            session.setAttribute("result", result);
            response.sendRedirect(request.getContextPath() + "/result.jsp");


        } catch (Exception e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/result.jsp");
            //request.getRequestDispatcher("/game.jsp").forward(request, response);
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
            return y.compareTo(x.divide(new BigDecimal("2")).add(rHalf)) <= 0;
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

