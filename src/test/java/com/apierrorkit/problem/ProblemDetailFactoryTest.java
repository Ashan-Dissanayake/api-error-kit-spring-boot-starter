package com.apierrorkit.problem;

import com.apierrorkit.config.ApiErrorKitProperties;
import com.apierrorkit.resolver.ResolvedApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProblemDetailFactoryTest {

    private final ProblemDetailFactory factory =
            new ProblemDetailFactory(new ApiErrorKitProperties());

    @Test
    void shouldCreateProblemDetail() {

        ApiErrorKitProperties properties =
                new ApiErrorKitProperties();

        properties.setIncludeExceptionMessage(true);

        ProblemDetailFactory factory =
                new ProblemDetailFactory(properties);

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
        assertEquals("User Not Found", problem.getTitle());
        assertEquals(
                "https://example.com/problems/user-not-found",
                problem.getType().toString()
        );
        assertEquals(
                "User 123 was not found",
                problem.getDetail()
        );
    }

    @Test
    void shouldNotExposeExceptionMessageByDefault() {

        ResolvedApiError resolvedError =
                new ResolvedApiError(
                        404,
                        "User Not Found",
                        "https://example.com/problems/user-not-found"
                );

        RuntimeException exception =
                new RuntimeException("Sensitive internal information");

        ProblemDetail problem =
                factory.create(resolvedError, exception);

        assertThat(problem.getDetail())
                .isEqualTo("An unexpected error occurred");
    }

    @Test
    void shouldHandleNullExceptionMessage() {

        ApiErrorKitProperties properties =
                new ApiErrorKitProperties();

        properties.setIncludeExceptionMessage(true);

        ProblemDetailFactory factory =
                new ProblemDetailFactory(properties);

        ResolvedApiError resolvedError =
                new ResolvedApiError(
                        404,
                        "User Not Found",
                        "https://example.com/problems/user-not-found"
                );

        RuntimeException exception =
                new RuntimeException();

        ProblemDetail problem =
                factory.create(resolvedError, exception);

        assertNull(problem.getDetail());
    }

    @Test
    void shouldHandleEmptyExceptionMessage() {

        ApiErrorKitProperties properties =
                new ApiErrorKitProperties();

        properties.setIncludeExceptionMessage(true);

        ProblemDetailFactory factory =
                new ProblemDetailFactory(properties);

        ResolvedApiError resolvedError =
                new ResolvedApiError(
                        404,
                        "User Not Found",
                        "https://example.com/problems/user-not-found"
                );

        RuntimeException exception =
                new RuntimeException("");

        ProblemDetail problem =
                factory.create(resolvedError, exception);

        assertEquals("", problem.getDetail());
    }

    @Test
    void shouldHideExceptionMessageWhenDisabled() {

        ApiErrorKitProperties properties =
                new ApiErrorKitProperties();

        properties.setIncludeExceptionMessage(false);

        ProblemDetailFactory factory =
                new ProblemDetailFactory(properties);

        ResolvedApiError resolvedError =
                new ResolvedApiError(
                        404,
                        "User Not Found",
                        "https://example.com/problems/user-not-found"
                );

        RuntimeException exception =
                new RuntimeException(
                        "Sensitive internal database information"
                );

        ProblemDetail problem =
                factory.create(resolvedError, exception);

        assertEquals(
                "An unexpected error occurred",
                problem.getDetail()
        );
    }
}