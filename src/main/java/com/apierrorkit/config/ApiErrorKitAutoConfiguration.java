package com.apierrorkit.config;

import com.apierrorkit.handler.ProblemExceptionHandler;
import com.apierrorkit.problem.ProblemDetailFactory;
import com.apierrorkit.resolver.AnnotationExceptionResolver;
import com.apierrorkit.resolver.ExceptionResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(ApiErrorKitProperties.class)
public class ApiErrorKitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExceptionResolver.class)
    ExceptionResolver exceptionResolver() {
        return new AnnotationExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(ProblemDetailFactory.class)
    ProblemDetailFactory problemDetailFactory(
            ApiErrorKitProperties properties) {

        return new ProblemDetailFactory(properties);
    }

    @Bean
    @ConditionalOnMissingBean(ProblemExceptionHandler.class)
    ProblemExceptionHandler problemExceptionHandler(
            ExceptionResolver exceptionResolver,
            ProblemDetailFactory problemDetailFactory) {

        return new ProblemExceptionHandler(
                exceptionResolver,
                problemDetailFactory
        );
    }
}