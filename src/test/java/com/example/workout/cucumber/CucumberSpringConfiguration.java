package com.example.workout.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberSpringConfiguration.Config.class)
public class CucumberSpringConfiguration {
    @Configuration
    static class Config {}
}
