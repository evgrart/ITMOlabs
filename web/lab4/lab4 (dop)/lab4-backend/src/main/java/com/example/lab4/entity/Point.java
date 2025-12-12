package com.example.lab4.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "points")
public class Point implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private double x;
    private double y;
    private double r;
    private boolean hit;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @ManyToOne 
    @JoinColumn(name = "user_id")
    private User user;

    public Point() {}

    public String getFormattedTime() {
        if (this.requestTime == null) return "";
        return this.requestTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getR() { return r; }
    public void setR(double r) { this.r = r; }
    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}