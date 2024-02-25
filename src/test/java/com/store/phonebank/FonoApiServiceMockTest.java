package com.store.phonebank;

import com.store.phonebank.services.seed.FonoApiService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Profile("test")
@Import({TestConfig.class, RestTemplateConfig.class})
public class FonoApiServiceMockTest  extends TestConfig {

    @Autowired
    private FonoApiService fonoApiService;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        fonoApiService.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);

        for (int i = 0; i < 3; i++) {
            mockServer.expect(MockRestRequestMatchers.anything())
                    .andRespond(MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Test
    public void testGetDeviceInfo_CircuitBreakerOpens() {
        CircuitBreaker.State state_before = fonoApiService.getCircuitBreaker().getState(); // You need to add a getter for circuitBreaker in FonoApiService
        assertEquals(CircuitBreaker.State.CLOSED, state_before);
        for (int i = 0; i < 3; i++) {
            assertThrows(RuntimeException.class, () -> fonoApiService.getDeviceInfo("brand", "model"));
        }

        CircuitBreaker.State state_after = fonoApiService.getCircuitBreaker().getState(); // You need to add a getter for circuitBreaker in FonoApiService
        assertEquals(CircuitBreaker.State.OPEN, state_after);
    }
}