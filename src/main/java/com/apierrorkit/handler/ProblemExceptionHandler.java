package com.apierrorkit.handler;

import com.apierrorkit.problem.ProblemDetailFactory;
import com.apierrorkit.resolver.ExceptionResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException exception) {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Validation Failed");
        problem.setDetail("Request validation failed");

        var errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .toList();

        problem.setProperty("errors", errors);

        return problem;
    }
}