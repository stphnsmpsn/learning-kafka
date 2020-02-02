package dev.stephensampson.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.stephensampson.kafka.config.AutoOffsetResetConfig;

public class Consumer implements Runnable {

    private static final int POLL_INTERVAL = 100;
    private final Logger logger;
    private final KafkaConsumer<String, String> consumer;
    private final String[] topics;
    private final int id;

    public Consumer(int id, String bootstrapServer, String groupId, AutoOffsetResetConfig config, String[] topics){
        this.logger = LoggerFactory.getLogger(Consumer.class);
        this.topics = topics;
        this.id = id;
        // Create consumer properties (refer to section 3.4 in docs:
        // https://kafka.apache.org/documentation/#consumerconfigs)
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.value());
        // Create producer
        this.consumer = new KafkaConsumer<String, String>(properties);
    }

    public void run() {
        // subscribe to our topic(s)
        consumer.subscribe(Arrays.asList(topics));

        // poll for new data
        try {
            while(!Thread.currentThread().isInterrupted()){
                ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(POLL_INTERVAL));
                for(ConsumerRecord<String, String> record : records){
                    logger.info("Consumer: " + this.id + " consuming key: " + record.key() + " with value: " + record.value() + "\r\n");
                }
            }
        } catch (WakeupException e){
            logger.info("Received shutdown signal");
        } finally {
            consumer.close();
        }
    }

    public void shutdown(){
        // the wakeup() method is a special method to interrupt consumer.poll().
        // it causes it to throw a WakeupException
        consumer.wakeup();
    }

}
