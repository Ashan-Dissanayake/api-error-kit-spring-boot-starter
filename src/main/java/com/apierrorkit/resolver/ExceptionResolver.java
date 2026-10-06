package com.apierrorkit.resolver;

import java.util.Optional;

public interface ExceptionResolver {

    Optional<ResolvedApiError> resolve(Throwable exception);
}