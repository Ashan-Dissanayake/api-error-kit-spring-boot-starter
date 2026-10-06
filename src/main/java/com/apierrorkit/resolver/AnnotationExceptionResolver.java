package com.apierrorkit.resolver;

import com.apierrorkit.annotation.ApiError;

import java.util.Optional;

public class AnnotationExceptionResolver
        implements ExceptionResolver {

    @Override
    public Optional<ResolvedApiError> resolve(Throwable exception) {

        Class<?> exceptionClass = exception.getClass();

        while (exceptionClass != null) {

            ApiError annotation =
                    exceptionClass.getAnnotation(ApiError.class);

            if (annotation != null) {

                return Optional.of(
                        new ResolvedApiError(
                                annotation.status(),
                                annotation.title(),
                                annotation.type()
                        )
                );
            }

            exceptionClass = exceptionClass.getSuperclass();
        }

        return Optional.empty();
    }
}