package org.cloudjune.ragchatbotservice.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class ApplicationConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return Flyway::migrate; // keep repair off by default to avoid confusion when auth fails
    }

    @Bean
    public CommandLineRunner startupDiagnostics(Environment env, @Autowired(required = false) DataSource dataSource,
                                               @Autowired(required = false) Flyway flyway) {
        return args -> {
            String dbUrl = env.getProperty("spring.datasource.url");
            String dbUser = env.getProperty("spring.datasource.username");
            String flywayEnabled = env.getProperty("spring.flyway.enabled");
            String activeProfiles = String.join(",", env.getActiveProfiles());

            log.info("Startup diagnostics -> profiles={}, datasource.url={}, datasource.username={}, flyway.enabled={}",
                    activeProfiles, dbUrl, dbUser, flywayEnabled);

            // Try a simple connection to provide an early and clear message if authentication fails
            if (dataSource != null) {
                try (Connection c = dataSource.getConnection()) {
                    log.info("Database connection test: SUCCESS ({} -> {})", c.getMetaData().getUserName(), c.getMetaData().getURL());
                } catch (SQLException ex) {
                    String msg = "Database connection test FAILED: " + ex.getMessage() + " | If you are running with Docker Compose, try: 'docker compose down -v && docker compose up -d --build'. Also verify .env DB settings and that the app container sees them (compose exec app env). You can temporarily set FLYWAY_ENABLED=false to isolate connection issues.";
                    log.error(msg, ex);
                }
            } else {
                log.warn("No DataSource bean available to test connection.");
            }
        };
    }
}
