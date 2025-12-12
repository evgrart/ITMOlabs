package com.example.lab4.dao;

import com.example.lab4.entity.Point;
import com.example.lab4.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PointDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void save(Point point) {
        em.persist(point);
    }

    public List<Point> findAllByUser(User user) {
        return em.createQuery("SELECT p FROM Point p WHERE p.user = :u ORDER BY p.requestTime DESC", Point.class)
                .setParameter("u", user)
                .getResultList();
    }
}