package com.example.lab4.service;

import com.example.lab4.dao.PointDAO;
import com.example.lab4.dao.UserDAO;
import com.example.lab4.dto.CheckResponseDto;
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

    @EJB
    private VerificationService verificationService; 

    public CheckResponseDto processPoint(PointDto dto, String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found");

        CheckResponseDto response = verificationService.verify(dto.getX(), dto.getY(), dto.getR());

        Point point = new Point();
        point.setX(dto.getX());
        point.setY(dto.getY());
        point.setR(dto.getR());
        point.setHit(response.isInside()); 
        point.setRequestTime(LocalDateTime.now());
        point.setUser(user);

        pointDAO.save(point);

        return response;
    }

    public List<Point> getUserPoints(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) return List.of();
        return pointDAO.findAllByUser(user);
    }
}