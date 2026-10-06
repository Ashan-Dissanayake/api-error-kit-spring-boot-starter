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
        classes = ProblemExceptionHandlerIntegrationTest.TestConfig.class
)
@AutoConfigureMockMvc
class ProblemExceptionHandlerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnRfc9457ProblemDetail() throws Exception {

        mockMvc.perform(get("/users/123"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.type")
                        .value(
                                "https://example.com/problems/user-not-found"
                        ))
                .andExpect(jsonPath("$.title")
                        .value("User Not Found"))
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.detail")
                        .value("User 123 was not found"))
                .andExpect(jsonPath("$.instance")
                        .value("/users/123"));
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