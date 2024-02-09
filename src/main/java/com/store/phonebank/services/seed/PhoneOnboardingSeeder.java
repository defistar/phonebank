package com.store.phonebank.services.seed;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.entity.PhoneEntity;
import com.store.phonebank.repository.PhoneRepository;
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
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PhoneOnboardingSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneOnboardingSeeder.class);

    private final PhoneRepository phoneRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${seed.phone-datafile-path}")
    private String seedFilePath;

    public PhoneOnboardingSeeder(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Validating seed file path: {}", seedFilePath);
        Resource resource = resourceLoader.getResource(seedFilePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Seed file does not exist: " + seedFilePath);
        }
    }

//    public Mono<PhoneDto> savePhone(PhoneDto phoneDto) {
//        PhoneEntity phoneEntity = toEntity(phoneDto);
//
//        return this.phoneRepository.findByBrandNameAndModelCode(phoneDto.getBrandName(), phoneDto.getModelCode())
//                .flatMap(existingPhone -> {
//                    phoneEntity.setId(existingPhone.getId()); // Ensure the ID is set for existing entities
//                    return this.phoneRepository.save(phoneEntity);
//                })
//                .switchIfEmpty(Mono.defer(() -> {
//                    phoneEntity.setId(UUID.randomUUID().toString()); // Set ID to a new UUID for new entities
//                    if (phoneEntity.getCreatedAt() == null) {
//                        phoneEntity.setCreatedAt(LocalDateTime.now());
//                    }
//                    return this.phoneRepository.insert(phoneEntity);
//                }))
//                .map(this::toDto);
//    }

    private PhoneEntity toEntity(PhoneDto phoneDto) {
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setId(phoneDto.getId());
        phoneEntity.setBrandName(phoneDto.getBrandName());
        phoneEntity.setModelName(phoneDto.getModelName());
        phoneEntity.setModelCode(phoneDto.getModelCode());
        return phoneEntity;
    }

    public Mono<Long> runSeed() {
        LOGGER.info("Seeding phone data from file: {}", seedFilePath);
        Resource resource = resourceLoader.getResource(seedFilePath);
        try {
            Stream<String> lines = new BufferedReader(new InputStreamReader(resource.getInputStream())).lines();
            return Flux.defer(() -> Flux.fromStream(lines.skip(1))) // Skip the first line (header)
                    .map(this::lineToPhoneDto)
                    .filterWhen(phoneDto -> this.phoneRepository.findByBrandNameAndModelCode(phoneDto.getBrandName(), phoneDto.getModelCode())
                            .hasElement()
                            .map(hasElement -> !hasElement)) // Filter out existing phones
                    .flatMap(phoneDto -> {
                        PhoneEntity phoneEntity = toEntity(phoneDto);
                        phoneEntity.setId(UUID.randomUUID().toString()); // Set ID to a new UUID for new entities
                        if (phoneEntity.getCreatedAt() == null) {
                            phoneEntity.setCreatedAt(LocalDateTime.now());
                        }
                        return this.phoneRepository.insert(phoneEntity).thenReturn(1L);
                    })
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