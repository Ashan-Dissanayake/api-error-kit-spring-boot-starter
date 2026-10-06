package com.apierrorkit.problem;

import com.apierrorkit.resolver.ResolvedApiError;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public class ProblemDetailFactory {

    public ProblemDetail create(
            ResolvedApiError resolvedError,
            Throwable exception) {

        ProblemDetail problem =
                ProblemDetail.forStatus(resolvedError.status());

        problem.setTitle(resolvedError.title());

        problem.setType(
                URI.create(resolvedError.type())
        );

        problem.setDetail(exception.getMessage());

        return problem;
    }
}