package com.apierrorkit.resolver;

public record ResolvedApiError(
        int status,
        String title,
        String type
) {
}