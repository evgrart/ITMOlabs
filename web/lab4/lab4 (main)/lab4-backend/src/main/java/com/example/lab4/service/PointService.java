package com.example.lab4.service;

import com.example.lab4.dao.PointDAO;
import com.example.lab4.dao.UserDAO;
import com.example.lab4.dto.PointDto;
import com.example.lab4.entity.Point;
import com.example.lab4.entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class PointService {

    @EJB
    private PointDAO pointDAO;

    @EJB
    private UserDAO userDAO;

    public Point addPoint(PointDto dto, String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found");

        boolean hit = checkArea(dto.getX(), dto.getY(), dto.getR());

        Point point = new Point();
        point.setX(dto.getX());
        point.setY(dto.getY());
        point.setR(dto.getR());
        point.setHit(hit);
        point.setRequestTime(LocalDateTime.now());
        point.setUser(user);

        pointDAO.save(point);
        return point;
    }

    public List<Point> getUserPoints(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) return List.of();
        return pointDAO.findAllByUser(user);
    }

    private boolean checkArea(double x, double y, double r) {
        if (r <= 0) return false;

        if (x <= 0 && y >= 0) {
            return (x * x + y * y <= r * r);
        }

        if (x <= 0 && y <= 0) {
            return (x >= -r && y >= -r / 2.0);
        }

        if (x >= 0 && y <= 0) {
            return (y >= x - r / 2.0);
        }

        return false;
    }
}