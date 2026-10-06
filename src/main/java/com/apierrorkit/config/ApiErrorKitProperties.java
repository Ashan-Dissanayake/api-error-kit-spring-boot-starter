package com.apierrorkit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api-error-kit")
public class ApiErrorKitProperties {

    private boolean includeExceptionMessage = false;

    public boolean isIncludeExceptionMessage() {
        return includeExceptionMessage;
    }

    public void setIncludeExceptionMessage(boolean includeExceptionMessage) {
        this.includeExceptionMessage = includeExceptionMessage;
    }
}