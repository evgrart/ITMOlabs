package com.example.lab4.service;

import com.example.lab4.dao.UserDAO;
import com.example.lab4.entity.User;
import com.example.lab4.util.JwtUtil;
import com.example.lab4.util.PasswordHasher;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class UserService {

    @EJB
    private UserDAO userDAO;

    public boolean register(String username, String password) {
        if (userDAO.exists(username)) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordHasher.hash(password)); 
        userDAO.save(user);
        return true;
    }

    public String login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null) return null;

        if (PasswordHasher.check(password, user.getPassword())) {
            return JwtUtil.generateToken(user.getUsername());
        }
        return null;
    }
}