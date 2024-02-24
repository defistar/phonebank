




### Integration test with WebTestClient

1. to make POST Request with query-params
   here we are setting query params and making a POST request to the server.

```java
webTestClient.post()
    .uri(uriBuilder -> uriBuilder
            .path("/phone-booking/book")
            .queryParam("brandName", "Samsung")
            .queryParam("modelCode", "S9")
            .queryParam("userName", "Christopher Nolan")
            .build())
    .exchange()
    .expectStatus().isOk()
    .expectBody(String.class)
    .consumeWith(response -> {
        String bookingId = response.getResponseBody();
        assert bookingId != null;
        assert bookingId.length() > 0;
    });
```


2. to make POST Request with query-params and header
here we are setting header params and query params and making a POST request to the server.

```java
webTestClient.post()
    .uri(uriBuilder -> uriBuilder
            .path("/phone-booking/book")
            .queryParam("brandName", "Samsung")
            .queryParam("modelCode", "S9")
            .queryParam("userName", "Christopher Nolan")
            .build())
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer APIKey")
    .exchange()
    .expectStatus().isOk()
    .expectBody(String.class)
    .consumeWith(response -> {
        String bookingId = response.getResponseBody();
        assert bookingId != null;
        assert bookingId.length() > 0;
    });
```