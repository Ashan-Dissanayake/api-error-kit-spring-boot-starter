package com.apierrorkit.handler;

import com.apierrorkit.problem.ProblemDetailFactory;
import com.apierrorkit.resolver.ExceptionResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProblemExceptionHandler {

    private final ExceptionResolver exceptionResolver;
    private final ProblemDetailFactory problemDetailFactory;

    public ProblemExceptionHandler(
            ExceptionResolver exceptionResolver,
            ProblemDetailFactory problemDetailFactory) {

        this.exceptionResolver = exceptionResolver;
        this.problemDetailFactory = problemDetailFactory;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception exception) {

        return exceptionResolver
                .resolve(exception)
                .map(resolvedError ->
                        problemDetailFactory.create(
                                resolvedError,
                                exception
                        )
                )
                .orElseGet(() -> {

                    ProblemDetail problem =
                            ProblemDetail.forStatus(
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            );

                    problem.setTitle("Internal Server Error");
                    problem.setDetail(exception.getMessage());

                    return problem;
                });
    }
}