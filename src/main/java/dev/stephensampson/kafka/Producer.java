package dev.stephensampson.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer implements Runnable {

    private static final int PRODUCE_INTERVAL = 100;
    private final Logger logger;
    private final KafkaProducer<String, String> producer;
    private final String topic;

    public Producer(String bootstrapServer, String topic){
        this.logger = LoggerFactory.getLogger(Producer.class);
        this.topic = topic;
        // Create producer properties (refer to section 3.3 in docs:
        // https://kafka.apache.org/documentation/#producerconfigs)
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Create producer
        this.producer = new KafkaProducer<String, String>(properties);
    }

    public void run() {
        for (int i = 0; i < 30; i++) {
            String value = "hello world " + String.valueOf(i);
            // i % 5 so that we only have 5 unique keys.
            // using this to demonstrate how records with the same key all go to the same partition.
            String key = "id_" + String.valueOf(i % 5); 
            logger.info("Producing record with key: " + key);
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.topic, key, value);
            this.producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        logger.info("Successfully produced record");
                        logger.info("Recieved new metadata. \r\n" + 
                        "\tTopic: " + metadata.topic() + "\r\n" + 
                        "\tPartition: " + metadata.partition() + "\r\n" + 
                        "\tOffset: " + metadata.offset() + "\r\n" + 
                        "\tTimestamp: " + metadata.timestamp() + "\r\n");
                    } else {
                        logger.error("Error producing record", exception);
                    }
                }
            });
           delay(PRODUCE_INTERVAL);
        }

        // Close the producer
        this.producer.close();

    }

    private void delay(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
