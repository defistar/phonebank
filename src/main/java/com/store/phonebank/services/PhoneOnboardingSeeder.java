package com.store.phonebank.services;

import com.store.phonebank.dto.PhoneDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

@Service
public class PhoneOnboardingSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneOnboardingSeeder.class);
    private final PhoneOnboardingService phoneOnboardingService;
    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${seed.phone-datafile-path}")
    private String seedFilePath;

    public PhoneOnboardingSeeder(PhoneOnboardingService phoneOnboardingService) {
        this.phoneOnboardingService = phoneOnboardingService;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Validating seed file path: {}", seedFilePath);
        Resource resource = resourceLoader.getResource(seedFilePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Seed file does not exist: " + seedFilePath);
        }
    }

    public Mono<Long> runSeed() {
        LOGGER.info("Seeding phone data from file: {}", seedFilePath);
        Resource resource = resourceLoader.getResource(seedFilePath);
        try {
            Stream<String> lines = new BufferedReader(new InputStreamReader(resource.getInputStream())).lines();
            return Flux.defer(() -> Flux.fromStream(lines.skip(1))) // Skip the first line (header)
                    .map(this::lineToPhoneDto)
                    .flatMap(phoneDto -> phoneOnboardingService.savePhone(phoneDto).thenReturn(1L))
                    .doOnError(e -> LOGGER.error("Error while saving phone data", e))
                    .onErrorResume(e -> {
                        LOGGER.error("Error occurred during seed loading, skipping this record", e);
                        return Mono.just(0L);
                    })
                    .retry(3)
                    .reduce(0L, Long::sum);
        } catch (IOException e) {
            LOGGER.error("Error reading seed file", e);
            throw new RuntimeException("Error reading seed file", e);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        runSeed().subscribe(count -> LOGGER.info("Inserted {} records", count));
    }

    private PhoneDto lineToPhoneDto(String line) {
        String[] parts = line.split(",");
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setBrandName(parts[0]);
        phoneDto.setModelName(parts[1]);
        phoneDto.setModelCode(parts[2]);
        phoneDto.setPhoneCount(Integer.parseInt(parts[3]));
        return phoneDto;
    }
}