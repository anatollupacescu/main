package org.kafka.tool.bean;

import com.google.common.base.Strings;
import kafka.javaapi.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;

import kafka.producer.KeyedMessage;
import org.springframework.beans.factory.annotation.Value;

public class SingleThreadProducer {

	private @Value("${kafka.topic}") String topic;

	private @Autowired Producer<byte[], String> kafkaProducer;

	public void sendMessage(final String message) {
        KeyedMessage<byte[], String> keyedMessage = null;
        if(!Strings.isNullOrEmpty(message)) {
            if(message.contains(" ")) {
                String[] keyAndMessage = message.split(" ");
                String key = parseKey(keyAndMessage[0]);
                String msg = keyAndMessage[1];
                keyedMessage = new KeyedMessage<>(topic, key.getBytes(), msg);
            } else {
                keyedMessage = new KeyedMessage<>(topic, message);
            }
            if(keyedMessage != null) {
                kafkaProducer.send(keyedMessage);
            }
        }
	}

    private String parseKey(String s) {
        try {
            Integer intValue = Integer.valueOf(s);
            return intValue.toString();
        } catch (Exception e) {
        }
        return "0";
    }

    public void shutdown() {
		kafkaProducer.close();
	}
}
