package com.apierrorkit.resolver;

import com.apierrorkit.annotation.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationExceptionResolverTest {

    private AnnotationExceptionResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AnnotationExceptionResolver();
    }

    @Test
    void shouldResolveAnnotatedException() {

        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        Optional<ResolvedApiError> result =
                resolver.resolve(exception);

        assertTrue(result.isPresent());

        ResolvedApiError error = result.get();

        assertEquals(404, error.status());
        assertEquals("User Not Found", error.title());
        assertEquals(
                "https://example.com/problems/user-not-found",
                error.type()
        );
    }

    @Test
    void shouldReturnEmptyForUnannotatedException() {

        RuntimeException exception =
                new RuntimeException("Something went wrong");

        Optional<ResolvedApiError> result =
                resolver.resolve(exception);

        assertTrue(result.isEmpty());
    }

    @ApiError(
            status = 404,
            title = "User Not Found",
            type = "https://example.com/problems/user-not-found"
    )
    static class UserNotFoundException extends RuntimeException {

        UserNotFoundException(String message) {
            super(message);
        }
    }

    @Test
    void shouldResolveInheritedAnnotation() {

        ChildBusinessException exception =
                new ChildBusinessException("Child error");

        Optional<ResolvedApiError> result =
                resolver.resolve(exception);

        assertTrue(result.isPresent());

        ResolvedApiError error = result.get();

        assertEquals(400, error.status());
        assertEquals("Business Error", error.title());
        assertEquals(
                "https://example.com/problems/business-error",
                error.type()
        );
    }

    @ApiError(
            status = 400,
            title = "Business Error",
            type = "https://example.com/problems/business-error"
    )
    static class BusinessException extends RuntimeException {
    }

    static class ChildBusinessException extends BusinessException {
        public ChildBusinessException(String childError) {
            super();
        }
    }

    
}

