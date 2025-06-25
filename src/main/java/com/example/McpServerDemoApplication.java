package com.example;

import com.example.tools.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * @author Administrator
 */
@SpringBootApplication
public class McpServerDemoApplication {

    public static void main(String[] args) {
        System.setProperty("traceId", "req-"+ UUID.randomUUID().toString());
        System.setProperty("spring.application.name","mcp-server-demo");
        SpringApplication.run(McpServerDemoApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }
}
