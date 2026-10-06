package com.apierrorkit.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = ProblemExceptionHandlerIntegrationTest.TestConfig.class,
        properties = "api-error-kit.include-exception-message=false"
)
@AutoConfigureMockMvc
public class SecureFallbackExceptionHandlerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

        @Test
    void shouldNotExposeMessageForUnhandledException() throws Exception {

        mockMvc.perform(get("/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.title")
                        .value("Internal Server Error"))
                .andExpect(jsonPath("$.detail")
                        .value("An unexpected error occurred"))
                .andExpect(jsonPath("$.instance")
                        .value("/error"));
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        TestController testController() {
            return new TestController();
        }
    }
}
