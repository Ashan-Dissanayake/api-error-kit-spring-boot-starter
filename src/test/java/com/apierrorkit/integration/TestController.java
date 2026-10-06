package com.apierrorkit.integration;

import com.apierrorkit.annotation.ApiError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestController {

    @GetMapping("/users/123")
    String getUser() {
        throw new UserNotFoundException(
                "User 123 was not found"
        );
    }

    @ApiError(
            status = 404,
            title = "User Not Found",
            type = "https://example.com/problems/user-not-found"
    )
    static class UserNotFoundException
            extends RuntimeException {

        UserNotFoundException(String message) {
            super(message);
        }
    }
}