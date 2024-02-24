package com.store.phonebank.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfiguration {

    @Value("${resilience4j.circuitbreaker.instances.bookPhone.slidingWindowSize}")
    private int bookPhoneSlidingWindowSize;

    @Value("${resilience4j.circuitbreaker.instances.bookPhone.permittedNumberOfCallsInHalfOpenState}")
    private int bookPhonePermittedNumberOfCallsInHalfOpenState;

    @Value("${resilience4j.circuitbreaker.instances.bookPhone.waitDurationInOpenState}")
    private Duration bookPhoneWaitDurationInOpenState;

    @Value("${resilience4j.circuitbreaker.instances.bookPhone.failureRateThreshold}")
    private float bookPhoneFailureRateThreshold;

    @Value("${resilience4j.circuitbreaker.instances.bookPhone.automaticTransitionFromOpenToHalfOpenEnabled}")
    private boolean bookPhoneAutomaticTransitionFromOpenToHalfOpenEnabled;

    @Value("${resilience4j.circuitbreaker.instances.bookPhone.eventConsumerBufferSize}")
    private int bookPhoneEventConsumerBufferSize;

    @Value("${resilience4j.circuitbreaker.instances.returnPhone.slidingWindowSize}")
    private int returnPhoneSlidingWindowSize;

    @Value("${resilience4j.circuitbreaker.instances.returnPhone.permittedNumberOfCallsInHalfOpenState}")
    private int returnPhonePermittedNumberOfCallsInHalfOpenState;

    @Value("${resilience4j.circuitbreaker.instances.returnPhone.waitDurationInOpenState}")
    private Duration returnPhoneWaitDurationInOpenState;

    @Value("${resilience4j.circuitbreaker.instances.returnPhone.failureRateThreshold}")
    private float returnPhoneFailureRateThreshold;

    @Value("${resilience4j.circuitbreaker.instances.returnPhone.automaticTransitionFromOpenToHalfOpenEnabled}")
    private boolean returnPhoneSutomaticTransitionFromOpenToHalfOpenEnabled;

    @Value("${resilience4j.circuitbreaker.instances.returnPhone.eventConsumerBufferSize}")
    private int returnPhoneEventConsumerBufferSize;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig bookPhoneCircuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(bookPhoneFailureRateThreshold)
                .waitDurationInOpenState(bookPhoneWaitDurationInOpenState)
                .slidingWindowSize(bookPhoneSlidingWindowSize)
                .permittedNumberOfCallsInHalfOpenState(bookPhonePermittedNumberOfCallsInHalfOpenState)
                .automaticTransitionFromOpenToHalfOpenEnabled(bookPhoneAutomaticTransitionFromOpenToHalfOpenEnabled)
                .build();

        CircuitBreakerConfig returnPhoneCircuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(returnPhoneFailureRateThreshold)
                .waitDurationInOpenState(returnPhoneWaitDurationInOpenState)
                .slidingWindowSize(returnPhoneSlidingWindowSize)
                .permittedNumberOfCallsInHalfOpenState(returnPhonePermittedNumberOfCallsInHalfOpenState)
                .automaticTransitionFromOpenToHalfOpenEnabled(returnPhoneSutomaticTransitionFromOpenToHalfOpenEnabled)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        circuitBreakerRegistry.addConfiguration("bookPhone", bookPhoneCircuitBreakerConfig);
        circuitBreakerRegistry.addConfiguration("returnPhone", returnPhoneCircuitBreakerConfig);

        return circuitBreakerRegistry;
    }
}