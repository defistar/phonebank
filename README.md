# PhoneBank

## Phone Background
PhoneBank is a simple application that allows you to manage Phone bookings and get device Info
PhoneBank contains a list of Phones available for rental via booking by Software Testers in the team
Testers can book a specific phone and return the phone after use

## Phone Categories
Each Phone is a device which can operate on multiple Bands (2G/2G/4G/5G) and has a specific OS (Android/iOS)

## Phone Onboarding and Identification
Phones are identified by brandName, modelCode and there can be multiple devices with the same brandName and modelCode
Each Phone has a unique id which is generated while onboarding the phone to the system


## System Functions

### Phone Booking
- Book a phone
- Return a phone
- Get all the phones available for booking
- Check availability of a phone

## Phone Info
- Get details of a phone by Phone-Id
- Get details of all the phones
- Get details of all the phones by brandName and modelCode

## Device Info
- Get the device info of a phone
- Get Info of all the devices

## Seed Functions
- Load all Phone information from a file (On Startup)
- Load all Device information from a file (On Startup)

## Tech Stack
- Java 17
- Maven
- Postgres
- spring-react
- Lombok
- MapStruct
- Swagger

## Java version

- This project is built using Java 17

- using `sdkman` , install java-17 on your machine and make it as default java version
- follow instructions here to install java-17 https://walterteng.com/using-sdkman-on-mac

```shell
curl -s "https://get.sdkman.io" | bash

source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install java 17.0.10-graal

sdk use java 17.0.10-graal
```

## Maven version

- install maven using `sdkman`

```shell
sdk install maven 3.9.5
```

## Building the application

```shell
mvn clean install
```

## Database Setup

- Use the `docker-compose.yml` file in the project's root directory to start the postgres database

```shell
docker-compose up -d
```

## environment variables

- environment variables are set in the `application.yml` file under: src/main/resources/application.yml
- please retain the values as-is in the file (unless you update the port number of postgres database in the docker-compose.yml file)

[application config](./src/main/resources/application.yml)


## Table creation

- Application doesn't have any migration script to handle auto-creation of tables
- Please pick the sql file `phonebank.sql` from the root directory and run it in the postgres database to create the tables and indexes
- You can load that file directly in your preferred sql editor and execute it

[phone-bank sql](./src/main/resources/phone-bank.sql)

## Table Verification

- After running the sql file, you can verify the tables and indexes created in the database
- Tables and columns are: 
  - phone
  - phone_booking
  - device_info
- primary keys are auto-generated as we have mentioned the extention
```
EXTENSION IF NOT EXISTS "uuid-ossp";
```

## Seed Data of Phones and Device Info
- Phone and Device Info data is loaded from the `phone-data.csv` and `device-info.csv` files in the root directory
- This is automated on startup of the application
- Verification: After application startup, please verify the data in the database for the tables `phone` and `device_info`

### SeedData
- The seed data is loaded from the csv files in the root directory
[phone-data](./src/main/resources/phone-data.csv)
[device-info](./src/main/resources/device-info.csv)

- Please maintain the header structure as-is and add new entries if you would like to add more data
- Once the seed data is loaded, you can topup by adding more data and checks are in place such that only new records are inserted
- existing records are not updated

## Running the application

```shell
 mvn -X spring-boot:run
```

## API Endpoints

- The application runs on port 8080
- The application has the following endpoints

1. Book Phone

http://localhost:8080/api/v1/phone-booking/book?brandName=Samsung&modelCode=S8&userName=lakshmi

- Query Params: brandName, modelCode, userName

2. Return Phone

- http://localhost:8080/api/v1/phone-booking/return?bookingId=3ba4f150-054a-441b-96fb-ae50007637f2

- Query Params: bookingId

3. Check Phone Availability

- http://localhost:8080/api/v1/phone-booking/check-availability?brandName=Samsung&modelCode=S8

- Query Params: brandName, modelCode

4. Get All Phones

- http://localhost:8080/api/v1/phone-info/all

5. Get One Phone

- http://localhost:8080/api/v1/phone-info/one?brandName=Samsung&modelCode=S8

- Query Params: brandName, modelCode

6. Get All Devices Info

- http://localhost:8080/api/v1/device-info/all

7. Get One Device Info

- http://localhost:8080/api/v1/device-info/one?phoneId=3ba4f150-054a-441b-96fb-ae50007637f2

- Query Params: phoneId


