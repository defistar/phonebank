package com.store.phonebank.services;

import com.store.phonebank.dto.DeviceInfoDto;
import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.repository.DeviceInfoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class DeviceInfoSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoSeeder.class);
    private final DeviceInfoRepository deviceInfoRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${seed.device-info-file-path}")
    private String deviceInfoSeedFilePath;

    public DeviceInfoSeeder(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }


    @PostConstruct
    public void init() {
        LOGGER.info("Validating seed file path: {}", deviceInfoSeedFilePath);
        Resource resource = resourceLoader.getResource(deviceInfoSeedFilePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("deviceInfo-Seed file does not exist: " + deviceInfoSeedFilePath);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        runSeed().subscribe(count -> LOGGER.info("Inserted {} records", count));
    }

    public Mono<Long> runSeed() {
        LOGGER.info("Seeding device info data from file: {}", deviceInfoSeedFilePath);
        Resource resource = resourceLoader.getResource(deviceInfoSeedFilePath);
        try {
            Stream<String> lines = new BufferedReader(new InputStreamReader(resource.getInputStream())).lines();
            return Flux.defer(() -> Flux.fromStream(lines.skip(1))) // Skip the first line (header)
                    .map(this::lineToDeviceInfoDto)
                    .flatMap(deviceInfoDto -> this.saveDeviceInfoData(deviceInfoDto).thenReturn(1L))
                    .doOnError(e -> LOGGER.error("Error while saving device info data", e))
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

    public Mono<DeviceInfoDto> saveDeviceInfoData(DeviceInfoDto deviceInfoDto) {
        DeviceInfoEntity deviceInfoEntity = toEntity(deviceInfoDto);

        return this.deviceInfoRepository.findByBrandNameAndModelCode(deviceInfoDto.getBrandName(), deviceInfoDto.getModelCode())
                .flatMap(existingDeviceInfo -> {
                    deviceInfoEntity.setId(existingDeviceInfo.getId()); // Ensure the ID is set for existing entities
                    return this.deviceInfoRepository.save(deviceInfoEntity);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    deviceInfoEntity.setId(UUID.randomUUID().toString()); // Set ID to a new UUID for new entities
                    if (deviceInfoEntity.getCreatedAt() == null) {
                        deviceInfoEntity.setCreatedAt(LocalDateTime.now());
                    }
                    return this.deviceInfoRepository.insert(deviceInfoEntity);
                }))
                .map(this::toDto);
    }

    private DeviceInfoEntity toEntity(DeviceInfoDto deviceInfoDto) {
        DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
        deviceInfoEntity.setId(deviceInfoDto.getId());
        deviceInfoEntity.setBrandName(deviceInfoDto.getBrandName());
        deviceInfoEntity.setModelCode(deviceInfoDto.getModelCode());
        deviceInfoEntity.setTechnology(deviceInfoDto.getTechnology());
        deviceInfoEntity.set_2g_bands(deviceInfoDto.get_2g_bands());
        deviceInfoEntity.set_3g_bands(deviceInfoDto.get_3g_bands());
        deviceInfoEntity.set_4g_bands(deviceInfoDto.get_4g_bands());
        return deviceInfoEntity;
    }

    private DeviceInfoDto toDto(DeviceInfoEntity deviceInfoEntity) {
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setId(deviceInfoEntity.getId());
        deviceInfoDto.setBrandName(deviceInfoEntity.getBrandName());
        deviceInfoDto.setModelCode(deviceInfoEntity.getModelCode());
        deviceInfoDto.setTechnology(deviceInfoEntity.getTechnology());
        deviceInfoDto.set_2g_bands(deviceInfoEntity.get_2g_bands());
        deviceInfoDto.set_3g_bands(deviceInfoEntity.get_3g_bands());
        deviceInfoDto.set_4g_bands(deviceInfoEntity.get_4g_bands());
        return deviceInfoDto;
    }


    private DeviceInfoDto lineToDeviceInfoDto(String line) {
        try {
            CSVParser parser = new CSVParser(new StringReader(line), CSVFormat.DEFAULT);
            CSVRecord record = parser.getRecords().get(0); // We only have one line, so get the first record
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            deviceInfoDto.setBrandName(record.get(0));
            deviceInfoDto.setModelCode(record.get(1));
            deviceInfoDto.setTechnology(record.get(2));
            deviceInfoDto.set_2g_bands(record.get(3));
            deviceInfoDto.set_3g_bands(record.get(4));
            deviceInfoDto.set_4g_bands(record.get(5));

            return deviceInfoDto;
        } catch (IOException e) {
            throw new RuntimeException("Error parsing line", e);
        }
    }
}