package com.apierrorkit.problem;

import com.apierrorkit.config.ApiErrorKitProperties;
import com.apierrorkit.resolver.ResolvedApiError;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public class ProblemDetailFactory {

    private final ApiErrorKitProperties properties;

    public ProblemDetailFactory(ApiErrorKitProperties properties) {
        this.properties = properties;
    }

    public ProblemDetail create(
            ResolvedApiError resolvedError,
            Throwable exception) {

        ProblemDetail problem =
                ProblemDetail.forStatus(resolvedError.status());

        problem.setTitle(resolvedError.title());

        problem.setType(
                URI.create(resolvedError.type())
        );

        if (properties.isIncludeExceptionMessage()) {
            problem.setDetail(exception.getMessage());
        } else {
            problem.setDetail("An unexpected error occurred");
        }

        return problem;
    }
}