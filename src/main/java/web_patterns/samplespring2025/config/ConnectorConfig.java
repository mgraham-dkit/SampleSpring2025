package web_patterns.samplespring2025.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import web_patterns.samplespring2025.persistence.Connector;
import web_patterns.samplespring2025.persistence.MySqlConnector;

import java.io.IOException;

// Create a way to build a Connector with a specific properties file
// Properties file path is included in application.properties - Spring boot's properties file
@Configuration
public class ConnectorConfig {
    private Environment env;

    public ConnectorConfig(Environment env){
        this.env = env;
    }

    @Bean
    public Connector connector() throws IOException {
        String path = env.getProperty("connector.properties.path");
        return new MySqlConnector(path);
    }
}