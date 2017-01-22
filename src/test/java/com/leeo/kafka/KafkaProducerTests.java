package com.leeo.kafka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaProducerTests {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Test
	public void send() {
		String data = "{\"JSON\":{\"age\":24,\"availableBalance\":121.345678,\"birthday\":\"2015-12-28 18:47:01\",\"cost\":121.345678,\"name\":\"wjl\",\"sex\":\"female\"}}";
		this.kafkaTemplate.send("test", data);
	}

}
