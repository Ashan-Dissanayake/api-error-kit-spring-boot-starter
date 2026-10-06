package com.apierrorkit.problem;

import com.apierrorkit.resolver.ResolvedApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

class ProblemDetailFactoryTest {

    private final ProblemDetailFactory factory =
            new ProblemDetailFactory();

    @Test
    void shouldCreateProblemDetail() {

        ResolvedApiError resolvedError =
                new ResolvedApiError(
                        404,
                        "User Not Found",
                        "https://example.com/problems/user-not-found"
                );

        RuntimeException exception =
                new RuntimeException("User 123 was not found");

        ProblemDetail problem =
                factory.create(resolvedError, exception);

        assertEquals(404, problem.getStatus());
        assertEquals(
                "User Not Found",
                problem.getTitle()
        );
        assertEquals(
                "https://example.com/problems/user-not-found",
                problem.getType().toString()
        );
        assertEquals(
                "User 123 was not found",
                problem.getDetail()
        );
    }
}