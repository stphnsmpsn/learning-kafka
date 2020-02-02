package dev.stephensampson.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemo {

    private static final String BOOTSTRAP_SERVER = "kafka.stephensampson.dev:9092";

    public static void main(String[] args) {

        // Create producer properties (refer to section 3.3 in docs: https://kafka.apache.org/documentation/#producerconfigs)
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String,String>(properties);

        // Send data
        StringBuilder sb;
        for(int i = 0; i < 10; i++){
            sb = new StringBuilder();
            sb.append("Message: ");
            sb.append(String.valueOf(i));
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", sb.toString());
            producer.send(record);
        }

        // Close the producer
        producer.close();

    }

}
