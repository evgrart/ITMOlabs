package com.example.lab3.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class FlywayConfig {

    @Resource(lookup = "java:/jdbc/studs")
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        if (dataSource == null) {
            System.err.println("Flyway: DataSource not found!");
            return;
        }

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            System.out.println("Доп работает, гойда");
        } catch (Exception e) {
            System.err.println("Еррор: " + e.getMessage());
            e.printStackTrace();
        }
    }
}