package com.example.bootifulazure;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.io.WritableResource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@EnableBinding({Source.class, Sink.class})
@SpringBootApplication
public class BootifulAzureApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootifulAzureApplication.class, args);
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
//

@RestController
@RequestMapping("/oss")
class BlobController {

	private final Resource blobFile;

	BlobController(@Value("azure-blob://cats/azure-cat.jpg") Resource blobFile) {
		this.blobFile = blobFile;
	}

	@GetMapping (produces = MediaType.IMAGE_JPEG_VALUE)
	Mono<Resource> read()  {
		return Mono.just (blobFile).subscribeOn(Schedulers.elastic());
	}

	@PostMapping
	ResponseEntity<?> write(@RequestBody String data) throws IOException {
		try (OutputStream os = ((WritableResource) this.blobFile).getOutputStream()) {
			os.write(data.getBytes());
		}
		return ResponseEntity.ok().build() ;
	}
}
//

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

