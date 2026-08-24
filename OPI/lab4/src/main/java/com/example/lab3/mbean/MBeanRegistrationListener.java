package com.example.lab3.mbean;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MBeanRegistrationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MonitoringRegistry.registerMBeans();
        sce.getServletContext().log("Lab4 monitoring MBeans registered");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MonitoringRegistry.unregisterMBeans();
        sce.getServletContext().log("Lab4 monitoring MBeans unregistered");
    }
}
