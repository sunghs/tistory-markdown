### [SPRING BOOT] 스프링부트로 개발하기 8 - Apache Kafka 연동 2

#### SPRING BOOT랑 APACHE-KAFKA 연동하기

#### Maven Dependency 추가

```xml
<!-- KAFKA -->
<dependency>
	<groupId>org.apache.kafka</groupId>
	<artifactId>kafka-clients</artifactId>
</dependency>
<dependency>
	<groupId>org.apache.kafka</groupId>
	<artifactId>kafka-streams</artifactId>
	</dependency>
<dependency>
	<groupId>org.apache.kafka</groupId>
	<artifactId>kafka_2.11</artifactId>
</dependency>
```
Consumer는 Listener를 사용하기 위해 라이브러리를 하나 더 추가 해 준다.
```xml
<!-- KAFKA LISTENER -->
<dependency>
	<groupId>org.springframework.kafka</groupId>
	<artifactId>spring-kafka</artifactId>
</dependency>
```

#### application.properties 설정
spring boot에 key값이 기본적으로 내장되어 있다.

```nginx
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=sunghs-test
spring.kafka.consumer.enable-auto-commit=true
spring.kafka.consumer.auto-offset-reset=latest
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.max-poll-records=1000
spring.kafka.template.default-topic=sunghs-test
```
<br/>

#### properties 설명

**spring.kafka.bootstrap-servers**
카프카서버 정보, 기본적으로 9092 포트를 사용한다.<br/>
**spring.kafka.consumer.group-id**
컨슈머의 그룹id <br/>
**spring.kafka.consumer.enable-auto-commit**
데이터를 어디까지 읽었다는 offset을 주기적으로 저장할지 여부 <br/>
**spring.kafka.consumer.auto-offset-reset**
offset에 오류가 있을 경우 어디서부터 다시 할지 여부 
ealiest - 맨처음부터 다시 읽는다
latest - 이전꺼는 무시하고, 이제부터 들어오는 데이터부터 읽기 시작한다 <br/>
**spring.kafka.producer.key-serializer**
데이터를 kafka로 전달할때 사용하는 Key Encoder Class
StringSerializer는 문자열 형태의 데이터에만 사용 가능 <br/>
**spring.kafka.consumer.key-deserializer**
데이터를 kafka에서 받아서 사용하는 Key Decoder Class
StringDeserializer는 문자열 형태의 데이터에만 사용 가능 <br/>
**spring.kafka.producer.value-serializer**
데이터를 kafka로 전달할때 사용하는 Value Encoder Class
StringSerializer는 문자열 형태의 데이터에만 사용 가능 <br/>
**spring.kafka.consumer.value-deserializer**
데이터를 kafka에서 받아서 사용하는 Value Decoder Class
StringDeserializer는 문자열 형태의 데이터에만 사용 가능 <br/>
**spring.kafka.consumer.max-poll-records**
consumer가 한번에 가져오는 message 갯수 <br/>
**spring.kafka.template.default-topic**
기본 설정 topic name <br/>

####  

***
데이터를 JSON 형태로 넘길건데, Object 자체를 serialize 해서 produce/consume 할 예정이면 Serializer, Deserializer가 ByteArray 등의 Serializable 기반이어야 한다.
***
####  

편한 방법으로는 Object의 Getter를 이용해 JSonElement를 만들고 produce 하고,
consume해서 JSonParse를 이용해 Object에 Setter 하는 방법이 있다.

##  

#### Message를 KAFKA로 전달하는 Producer Class
```java
package sunghs.boot.mq;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

public @Component @Slf4j class Producer {

	private KafkaProducer<String, String> producer = null;
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServer;
	
	@Value("${spring.kafka.producer.key-serializer}")
	private String keySerializer;
	
	@Value("${spring.kafka.producer.value-serializer}")
	private String valueSerializer;
	
	@Value("${spring.kafka.template.default-topic}")
	private String topicName;
	
	@PostConstruct
	public void build() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
		producer = new KafkaProducer<>(properties);
	}
	
	public void send(String message) {
		String result = "SEND FAIL";
		ProducerRecord<String, String> prd = new ProducerRecord<String, String>(this.topicName, message);
		try {
			producer.send(prd, new Callback() {
				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					if(exception != null) {
						log.info(exception.getMessage());
					}
				}
			});
			result = "SEND SUCCESS";
		}
		catch(Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		finally {
			log.info(result + " : " + message);
			producer.close();
		}
	}
}
```

#### KAFKA에서 Message를 가져오는 Consumer Class
```java
package sunghs.boot.mq;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

public @Slf4j @Component class Consumer {
	
	private KafkaConsumer<String, String> consumer = null;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServer;
	
	@Value("${spring.kafka.consumer.group-id}")
	private String groupID;
	
	@Value("${spring.kafka.consumer.value-deserializer}")
	private String keyDeSerializer;
	
	@Value("${spring.kafka.consumer.value-deserializer}")
	private String valueDeSerializer;
	
	@Value("${spring.kafka.consumer.auto-offset-reset}")
	private String offsetReset;
	
	@Value("${spring.kafka.template.default-topic}")
	private String topicName;
	
	@Value("${spring.kafka.consumer.max-poll-records}")
	private String maxPollRecords;
	
	@Value("${spring.kafka.consumer.enable-auto-commit}")
	private String enableAutoCommit;
	
	@PostConstruct
	public void build() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
		consumer = new KafkaConsumer<>(properties);
	}
	
	
	@KafkaListener(topics="${spring.kafka.template.default-topic}")
	public void consume(@Headers MessageHeaders headers, @Payload String payload) {
		log.info("CONSUME HEADERS : " + headers.toString());
		log.info("CONSUME PAYLOAD : " + payload);
	}
}
```

KafkaListener는 메시지를 핸들링 하는 레벨의 메소드에 어노테이션을 사용하면 Spring Boot가 새로운 스레드에 메시지를 계속 subsribe 할 수 있도록 해준다. 
consume 메소드는 파라미터 타입이 @Headers, @Payload로 구성되어야 하며, 건 단위로 처리할 로직을 구현해 놓으면 된다.

####  

KafkaListener를 등록하지 않고 직접 subscribe를 구현하는 방법도 있다.
```java
public void consume2() {
		consumer.subscribe(Collections.singletonList(this.topicName));
		while (true) {
			ConsumerRecords<String, String> rcs = consumer.poll(Duration.ofMillis(1000));
			for(ConsumerRecord<String, String> rc : rcs) {
				if(rc.topic().equals(this.topicName)) {
					log.info("CONSUNE DATA : " + rc.value());
				}
			}
		}
	}
```
KafkaListener를 사용하는게 따로 Thread를 관리하지 않아도 되고 간단하므로 직접구현보다는 리스너 어노테이션 사용이 편함.

####  
```java
package sunghs.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sunghs.boot.mq.Consumer;
import sunghs.boot.mq.Producer;

@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
	
	@Autowired
	private Producer producer;
	
	@Autowired
	private Consumer consumer;
	
	public static void main(String[] args) {
		SpringApplication.run(BootExApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		producer.send("HELLO KAFKA");
		
		/* listener를 사용하는 경우에는 필요없음 */
		consumer.consume2();
	}
}
```
KafkaListener를 사용하면, 따로 consumer를 Autowired나 메소드를 호출 하지 않아도 Boot 에서 Kafka를 바라보고 있고, consume2() 같이 메소드를 따로 구현하는 경우에는 이런식으로 async를 사용하는 방법등을 통해 호출 해 줘야한다.

***
2019-07-18 13:21:59.071  INFO 69196 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.3.0
2019-07-18 13:21:59.071  INFO 69196 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: fc1aaa116b661c8a
2019-07-18 13:21:59.071  INFO 69196 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1563423719071
2019-07-18 13:21:59.072  INFO 69196 --- [           main] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=consumer-2, groupId=sunghs-test] Subscribed to topic(s): sunghs-test
2019-07-18 13:21:59.074  INFO 69196 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService
2019-07-18 13:21:59.086  INFO 69196 --- [           main] sunghs.boot.BootExApplication            : Started BootExApplication in 1.994 seconds (JVM running for 4.595)
2019-07-18 13:21:59.088  INFO 69196 --- [ntainer#0-0-C-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=consumer-2, groupId=sunghs-test] Cluster ID: _M052BlaRXKS4aP4-sJAFg
2019-07-18 13:21:59.090  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] Discovered group coordinator 10.172.16.177:9092 (id: 2147483647 rack: null)
2019-07-18 13:21:59.093  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] Revoking previously assigned partitions []
2019-07-18 13:21:59.094  INFO 69196 --- [ntainer#0-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : sunghs-test: partitions revoked: []
2019-07-18 13:21:59.095  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] (Re-)joining group
2019-07-18 13:21:59.106  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] (Re-)joining group
**2019-07-18 13:21:59.117  INFO 69196 --- [           main] sunghs.boot.mq.Producer                  : SEND SUCCESS : HELLO KAFKA**
2019-07-18 13:21:59.125  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] Successfully joined group with generation 3
2019-07-18 13:21:59.131  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] Setting newly assigned partitions: sunghs-test-0
2019-07-18 13:21:59.144  INFO 69196 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-2, groupId=sunghs-test] Setting offset for partition sunghs-test-0 to the committed offset FetchPosition{offset=1, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=10.172.16.177:9092 (id: 0 rack: null), epoch=0}}
2019-07-18 13:21:59.147  INFO 69196 --- [ntainer#0-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : sunghs-test: partitions assigned: [sunghs-test-0]
2019-07-18 13:21:59.189  INFO 69196 --- [ntainer#0-0-C-1] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
**2019-07-18 13:21:59.201  INFO 69196 --- [         task-1] sunghs.boot.mq.Consumer                  : CONSUME HEADERS : {kafka_offset=1, kafka_consumer=org.apache.kafka.clients.consumer.KafkaConsumer@6bb6a873, kafka_timestampType=CREATE_TIME, kafka_receivedMessageKey=null, kafka_receivedPartitionId=0, kafka_receivedTopic=sunghs-test, kafka_receivedTimestamp=1563423719103, kafka_groupId=sunghs-test}
2019-07-18 13:21:59.201  INFO 69196 --- [         task-1] sunghs.boot.mq.Consumer                  : CONSUME PAYLOAD : HELLO KAFKA**
***

메인스레드에서 send 하고, async 다른 스레드에서 consume하는 테스트