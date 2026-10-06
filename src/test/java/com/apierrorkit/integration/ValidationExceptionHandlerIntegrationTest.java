package com.apierrorkit.integration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = ValidationExceptionHandlerIntegrationTest.TestConfig.class
)
@AutoConfigureMockMvc
class ValidationExceptionHandlerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnValidationErrors() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "",
                                    "email": "invalid-email"
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.title")
                        .value("Validation Failed"))
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.instance")
                        .value("/users"))
                .andExpect(jsonPath("$.errors")
                        .isArray())
                .andExpect(jsonPath("$.errors.length()")
                        .value(2))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]")
                        .exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]")
                        .exists());
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        TestUserController testUserController() {
            return new TestUserController();
        }
    }

    @RestController
    static class TestUserController {

        @PostMapping("/users")
        String createUser(
                @Valid @RequestBody CreateUserRequest request) {

            return "created";
        }
    }

    static class CreateUserRequest {

        @NotBlank
        private String name;

        @Email
        @NotBlank
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}