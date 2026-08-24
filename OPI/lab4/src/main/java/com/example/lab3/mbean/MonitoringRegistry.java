package com.example.lab3.mbean;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public final class MonitoringRegistry {

    public static final String POINT_STATISTICS_OBJECT_NAME = "com.example.lab4:type=PointStatistics";
    public static final String AREA_OBJECT_NAME = "com.example.lab4:type=Area";

    private static final MonitoringState STATE = new MonitoringState();
    private static final PointStatistics POINT_STATISTICS = new PointStatistics(STATE);
    private static final Area AREA = new Area(STATE);

    private MonitoringRegistry() {
    }

    public static synchronized void registerMBeans() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            register(server, new ObjectName(POINT_STATISTICS_OBJECT_NAME), POINT_STATISTICS);
            register(server, new ObjectName(AREA_OBJECT_NAME), AREA);
        } catch (JMException e) {
            throw new IllegalStateException("Unable to register lab4 MBeans", e);
        }
    }

    public static synchronized void unregisterMBeans() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        unregister(server, POINT_STATISTICS_OBJECT_NAME);
        unregister(server, AREA_OBJECT_NAME);
    }

    public static void registerPoint(boolean hit, double radius) {
        POINT_STATISTICS.registerPoint(hit, radius);
    }

    public static void updateRadius(double radius) {
        STATE.updateRadius(radius);
    }

    private static void register(MBeanServer server, ObjectName objectName, Object mbean) throws JMException {
        if (server.isRegistered(objectName)) {
            server.unregisterMBean(objectName);
        }
        server.registerMBean(mbean, objectName);
    }

    private static void unregister(MBeanServer server, String objectName) {
        try {
            ObjectName name = new ObjectName(objectName);
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
            }
        } catch (JMException ignored) {
            // Application shutdown must not be blocked by JMX cleanup.
        }
    }
}
