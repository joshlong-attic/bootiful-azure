package com.example.bootifulazure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.models.BlockBlobUploadResponse;
import io.reactivex.Flowable;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@SpringBootApplication
@EnableBinding({Source.class, Sink.class})
public class BootifulAzureApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootifulAzureApplication.class, args);
    }
}


@Log4j2
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
class SpringCloudStreamServiceBusDemo {

    private final Set<String> messages = new ConcurrentSkipListSet<>();
    private final ObjectMapper objectMapper;
    private final Source source;

    // producer
    @GetMapping("/send")
    public void send() {
        var msg = MessageBuilder
                .withPayload("Hello bootiful Azure @ " + Instant.now() + "!")
                .build();
        this.source.output().send(msg);
    }

    @GetMapping("/messages")
    Collection<String> read() {
        return this.messages;
    }

    // consumer
    @SneakyThrows
    @StreamListener(Sink.INPUT)
    public void incomingMessageHandler(Message<String> msg) {
        String json = this.objectMapper.writeValueAsString(msg);
        this.messages.add(json);
    }
}


@Data
@RequiredArgsConstructor
class Customer {
    public final Integer id;
    public final String name;
}

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
class SqlServerDemoRestController {

    private final JdbcTemplate jdbc;

    @GetMapping("/customers")
    Collection<Customer> get() {
        return this.jdbc.query(
                "select * from [dbo].[CUSTOMERS]", (rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name")));
    }
}

@Log4j2
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
class CosmosDbDemoRestController {

    private final ReservationRepository cosmos;

    @GetMapping("/reservations")
    Iterable<Reservation> get() {
        return cosmos.findAll();
    }
}

interface ReservationRepository extends CrudRepository<Reservation, String> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reservations")
class Reservation {

    @Id
    private String id;
    private String name;
}


@RestController
class GreetingsRestController {

    @GetMapping("/greetings")
    String greet() {
        return "hello, Bootiful Azure!";
    }
}


@Log4j2
@RestController
class ObjectStorageServiceDemo {

    @SneakyThrows
    ObjectStorageServiceDemo(@Value("classpath:/cat.jpg") Resource catJpg, ContainerURL containerURL) {
        InputStream inputStream = catJpg.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        BlockBlobUploadResponse uploadResponse = containerURL
                .createBlockBlobURL("azure-cat.jpg")
                .upload(Flowable.just(ByteBuffer.wrap(bytes)), bytes.length, null, null, null, null)
                .blockingGet();
        log.info("uploaded: " + uploadResponse.toString());
    }
}