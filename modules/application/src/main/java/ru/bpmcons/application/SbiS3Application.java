package ru.bpmcons.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SbiS3Application {
    public static void main(String[] args) {
        SpringApplication.run(SbiS3Application.class, args);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
