package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "ControllerServlet", value = "/controller")
public class ControllerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String r = request.getParameter("r");
        
        if (x != null && y != null && r != null && 
            !x.isEmpty() && !y.isEmpty() && !r.isEmpty()) {
            request.getRequestDispatcher("/area-check").forward(request, response);
        } else {
            request.getRequestDispatcher("/game.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
}