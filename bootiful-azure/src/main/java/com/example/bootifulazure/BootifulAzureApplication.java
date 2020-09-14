package com.example.bootifulazure;

import com.microsoft.azure.storage.blob.ContainerURL;
import io.reactivex.Flowable;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
@EnableBinding({Source.class, Sink.class})
public class BootifulAzureApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootifulAzureApplication.class, args);
    }
}


@Log4j2
@RestController
@RequiredArgsConstructor
class SpringCloudStreamServiceBusDemo {

    private final Source source;

    // producer
    @GetMapping("/send")
    public void send() {
        var msg = MessageBuilder
                .withPayload("Hello bootiful Azure @ " + Instant.now() + "!")
                .build();
        this.source.output().send(msg);
    }

    // consumer
    @StreamListener(Sink.INPUT)
    public void incomingMessageHandler(Message<String> msg) {
        log.info("new message " + msg.getPayload());
        msg.getHeaders().forEach((k, v) -> log.info(k + '=' + v));
    }
}

@Log4j2
@Component
class ObjectStorageServiceDemo {

    @SneakyThrows
    ObjectStorageServiceDemo(
            @Value("classpath:/cat.jpg") Resource catJpg,
            ContainerURL containerURL) {

        var bytes = FileCopyUtils.copyToByteArray(catJpg.getFile());
        var uploadResponse = containerURL
                .createBlockBlobURL("cat.jpg")
                .upload(Flowable.just(ByteBuffer.wrap(bytes)), bytes.length, null, null, null, null)
                .blockingGet();
        log.info("uploaded: " + uploadResponse.toString());

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
@Component
class SqlServerDemo {

    private final JdbcTemplate template;

    @EventListener(ApplicationReadyEvent.class)
    public void begin() {
        var results = this.template.query(
                "select * from [dbo].[CUSTOMERS]", (rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name")));
        results.forEach(log::info);
    }
}

@Component
@Log4j2
@RequiredArgsConstructor
class CosmosDbDemo {

    private final ReservationRepository reservationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void goMongoGo() {
        Stream.of("A", "B", "C")
                .map(name -> new Reservation(null, name))
                .map(this.reservationRepository::save)
                .forEach(log::info);

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

