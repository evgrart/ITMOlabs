package com.example.lab4.dao;

import com.example.lab4.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :n", User.class)
                    .setParameter("n", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean exists(String username) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :n", Long.class)
                .setParameter("n", username)
                .getSingleResult();
        return count > 0;
    }
}