package com.example.lab3.mbean;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.concurrent.atomic.AtomicLong;

public class PointStatistics extends NotificationBroadcasterSupport implements PointStatisticsMBean {

    public static final String TENTH_POINT_NOTIFICATION = "com.example.lab4.point.totalMultipleOfTen";

    private final MonitoringState state;
    private final AtomicLong sequence = new AtomicLong();

    PointStatistics(MonitoringState state) {
        this.state = state;
    }

    void registerPoint(boolean hit, double radius) {
        MonitoringState.Snapshot snapshot = state.registerPoint(hit, radius);
        if (snapshot.tenthPoint()) {
            Notification notification = new Notification(
                    TENTH_POINT_NOTIFICATION,
                    this,
                    sequence.incrementAndGet(),
                    System.currentTimeMillis(),
                    "Total number of user points became multiple of 10: " + snapshot.totalPoints()
            );
            notification.setUserData("total=" + snapshot.totalPoints()
                    + ", hits=" + (snapshot.totalPoints() - snapshot.missPoints())
                    + ", misses=" + snapshot.missPoints());
            sendNotification(notification);
        }
    }

    @Override
    public long getTotalPoints() {
        return state.snapshot().totalPoints();
    }

    @Override
    public long getMissPoints() {
        return state.snapshot().missPoints();
    }

    @Override
    public long getHitPoints() {
        MonitoringState.Snapshot snapshot = state.snapshot();
        return snapshot.totalPoints() - snapshot.missPoints();
    }

    @Override
    public void reset() {
        state.reset();
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = {TENTH_POINT_NOTIFICATION};
        String name = Notification.class.getName();
        String description = "Notification sent when total user point count is multiple of 10";
        return new MBeanNotificationInfo[]{new MBeanNotificationInfo(types, name, description)};
    }
}
